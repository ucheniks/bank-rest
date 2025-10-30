package com.gshelgaas.bankcards.service;

import com.gshelgaas.bankcards.dto.AuthRequestDto;
import com.gshelgaas.bankcards.dto.AuthResponseDto;
import org.springframework.security.authentication.BadCredentialsException;

/**
 * Сервис для аутентификации пользователей.
 * Предоставляет функционал для входа в систему и генерации JWT токенов.
 *
 * @author Георгий Шельгаас
 */
public interface AuthService {

    /**
     * Выполняет аутентификацию пользователя и генерирует JWT токен.
     * Проверяет учетные данные и возвращает токен для доступа к защищенным endpoint'ам.
     *
     * @param authRequest данные для аутентификации (email и пароль)
     * @return ответ с JWT токеном
     * @throws BadCredentialsException если email или пароль неверные
     */
    AuthResponseDto login(AuthRequestDto authRequest);
}