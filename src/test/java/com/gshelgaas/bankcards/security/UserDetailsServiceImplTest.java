package com.gshelgaas.bankcards.security;

import com.gshelgaas.bankcards.entity.User;
import com.gshelgaas.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_withValidEmail_returnsUserDetails() {
        String email = "user@mail.ru";
        User user = User.builder()
                .id(1L)
                .email(email)
                .password("encodedPassword")
                .role(User.Role.ROLE_USER)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        verify(userRepository).findByEmail(email);
    }

    @Test
    void loadUserByUsername_withNonExistingEmail_throwsException() {
        String email = "notfound@test.ru";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(email));
        verify(userRepository).findByEmail(email);
    }

    @Test
    void loadUserByUsername_withAdminRole_returnsAdminAuthorities() {
        String email = "admin@mail.ru";
        User user = User.builder()
                .id(1L)
                .email(email)
                .password("encodedPassword")
                .role(User.Role.ROLE_ADMIN)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }
}