package com.gshelgaas.bankcards.service;

import com.gshelgaas.bankcards.dto.UserRequestDto;
import com.gshelgaas.bankcards.dto.UserResponseDto;
import com.gshelgaas.bankcards.entity.User;
import com.gshelgaas.bankcards.exception.ConflictException;
import com.gshelgaas.bankcards.exception.NotFoundException;
import com.gshelgaas.bankcards.exception.UnauthorizedException;
import com.gshelgaas.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_withValidData_returnsUserResponse() {
        UserRequestDto requestDto = UserRequestDto.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@test.ru")
                .password("password123")
                .build();

        User savedUser = User.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("test@test.ru")
                .password("encodedPassword")
                .role(User.Role.ROLE_USER)
                .build();

        when(userRepository.existsByEmail("test@test.ru")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDto result = userService.createUser(requestDto);

        assertNotNull(result);
        assertEquals("Test", result.getFirstName());
        assertEquals("User", result.getLastName());
        assertEquals("test@test.ru", result.getEmail());
        assertEquals("ROLE_USER", result.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_withExistingEmail_throwsException() {
        UserRequestDto requestDto = UserRequestDto.builder()
                .firstName("Test")
                .lastName("User")
                .email("user@mail.ru")
                .password("password123")
                .build();

        when(userRepository.existsByEmail("user@mail.ru")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.createUser(requestDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_withValidId_returnsUserResponse() {
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .firstName("Test")
                .lastName("User")
                .email("test@test.ru")
                .role(User.Role.ROLE_USER)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponseDto result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Test", result.getFirstName());
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserById_withNonExistingId_throwsException() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void getAllUsers_returnsListOfUsers() {
        User user1 = User.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("test@test.ru")
                .role(User.Role.ROLE_USER)
                .build();

        User user2 = User.builder()
                .id(2L)
                .firstName("Admin")
                .lastName("User")
                .email("admin@mail.ru")
                .role(User.Role.ROLE_ADMIN)
                .build();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserResponseDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test", result.get(0).getFirstName());
        assertEquals("Admin", result.get(1).getFirstName());
        verify(userRepository).findAll();
    }

    @Test
    void getAllUsers_withNoUsers_returnsEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponseDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteUser_withValidId_deletesUser() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_withNonExistingId_throwsException() {
        Long userId = 999L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).deleteById(userId);
    }

    @Test
    void getCurrentUser_withAuthenticatedUser_returnsUser() {
        String userEmail = "user@mail.ru";
        User user = User.builder()
                .id(1L)
                .email(userEmail)
                .firstName("Test")
                .lastName("User")
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(userEmail);
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

        SecurityContextHolder.setContext(securityContext);

        User result = userService.getCurrentUser();

        assertNotNull(result);
        assertEquals(userEmail, result.getEmail());
        verify(userRepository).findByEmail(userEmail);
    }

    @Test
    void getCurrentUser_withNoAuthentication_throwsException() {
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(UnauthorizedException.class, () -> userService.getCurrentUser());
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void getCurrentUser_withNonExistingEmail_throwsException() {
        String userEmail = "notfound@test.ru";

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(userEmail);
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        SecurityContextHolder.setContext(securityContext);

        assertThrows(NotFoundException.class, () -> userService.getCurrentUser());
        verify(userRepository).findByEmail(userEmail);
    }

    @Test
    void createUser_withAdminData_returnsAdminUser() {
        UserRequestDto requestDto = UserRequestDto.builder()
                .firstName("Admin")
                .lastName("User")
                .email("admin@test.ru")
                .password("admin123")
                .build();

        User savedUser = User.builder()
                .id(1L)
                .firstName("Admin")
                .lastName("User")
                .email("admin@test.ru")
                .password("encodedPassword")
                .role(User.Role.ROLE_ADMIN)
                .build();

        when(userRepository.existsByEmail("admin@test.ru")).thenReturn(false);
        when(passwordEncoder.encode("admin123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDto result = userService.createUser(requestDto);

        assertNotNull(result);
        assertEquals("Admin", result.getFirstName());
        assertEquals("admin@test.ru", result.getEmail());
        assertEquals("ROLE_ADMIN", result.getRole());
    }
}