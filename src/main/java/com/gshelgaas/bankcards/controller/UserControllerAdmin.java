package com.gshelgaas.bankcards.controller;

import com.gshelgaas.bankcards.dto.UserRequestDto;
import com.gshelgaas.bankcards.dto.UserResponseDto;
import com.gshelgaas.bankcards.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для административного управления пользователями.
 * Предоставляет endpoint'ы для создания, просмотра и удаления пользователей.
 * Доступно только пользователям с ролью ADMIN.
 *
 * @author Георгий Шельгаас
 */
@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserControllerAdmin {

    private final UserService userService;

    /**
     * Создает нового пользователя в системе.
     *
     * @param userRequestDto данные для создания пользователя
     * @return созданный пользователь
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        log.info("POST /admin/users - create user with email: {}", userRequestDto.getEmail());
        return userService.createUser(userRequestDto);
    }

    /**
     * Получает пользователя по идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return информация о пользователе
     */
    @GetMapping("/{userId}")
    public UserResponseDto getUser(@PathVariable Long userId) {
        log.info("GET /admin/users/{} - get user by id", userId);
        return userService.getUserById(userId);
    }

    /**
     * Получает всех пользователей системы.
     *
     * @return список всех пользователей
     */
    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        log.info("GET /admin/users - get all users");
        return userService.getAllUsers();
    }

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param userId идентификатор пользователя для удаления
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("DELETE /admin/users/{} - delete user", userId);
        userService.deleteUser(userId);
    }
}