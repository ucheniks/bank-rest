package com.gshelgaas.bankcards.controller;

import com.gshelgaas.bankcards.dto.AuthRequestDto;
import com.gshelgaas.bankcards.dto.AuthResponseDto;
import com.gshelgaas.bankcards.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для аутентификации пользователей.
 * Предоставляет endpoint'ы для входа в систему и получения JWT токенов.
 *
 * @author Георгий Шельгаас
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Выполняет аутентификацию пользователя и возвращает JWT токен.
     *
     * @param authRequest данные для входа (email и пароль)
     * @return JWT токен для доступа к защищенным endpoint'ам
     */
    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody AuthRequestDto authRequest) {
        log.info("POST /auth/login - login attempt for email: {}", authRequest.getEmail());
        return authService.login(authRequest);
    }
}