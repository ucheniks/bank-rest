package com.gshelgaas.bankcards.service;

import com.gshelgaas.bankcards.dto.UserRequestDto;
import com.gshelgaas.bankcards.dto.UserResponseDto;
import com.gshelgaas.bankcards.entity.User;
import com.gshelgaas.bankcards.exception.ConflictException;
import com.gshelgaas.bankcards.exception.NotFoundException;
import com.gshelgaas.bankcards.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Реализация сервиса для управления пользователями.
 * Обрабатывает регистрацию пользователей и работу с аутентификацией.
 *
 * @author Георгий Шельгаас
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * {@inheritDoc}
     * <p>
     * Реализация включает хеширование пароля и проверку уникальности email.
     */
    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        log.info("Creating user with email: {}", userRequestDto.getEmail());

        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new ConflictException("User with this email already exists");
        }

        User user = User.builder()
                .firstName(userRequestDto.getFirstName())
                .lastName(userRequestDto.getLastName())
                .email(userRequestDto.getEmail())
                .password(passwordEncoder.encode(userRequestDto.getPassword()))
                .role(User.Role.ROLE_USER)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created with id: {}", savedUser.getId());

        return mapToResponseDto(savedUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserResponseDto getUserById(Long userId) {
        log.info("Getting user by id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        return mapToResponseDto(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserResponseDto> getAllUsers() {
        log.info("Getting all users");

        return userRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("Deleting user with id: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Реализация использует SecurityContextHolder для получения аутентификации.
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }

    /**
     * Инициализирует тестовых пользователей при первом запуске приложения.
     * Создает администратора и обычного пользователя для демонстрации.
     */
    @PostConstruct
    public void createTestUsers() {
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .firstName("Admin")
                    .lastName("User")
                    .email("admin@mail.ru")
                    .password(passwordEncoder.encode("admin123"))
                    .role(User.Role.ROLE_ADMIN)
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.save(admin);

            User user = User.builder()
                    .firstName("Georgiy")
                    .lastName("Shelgaas")
                    .email("user@mail.ru")
                    .password(passwordEncoder.encode("user123"))
                    .role(User.Role.ROLE_USER)
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.save(user);

            log.info("Test users created");
        }
    }

    /**
     * Преобразует сущность User в DTO для ответа.
     */
    private UserResponseDto mapToResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}