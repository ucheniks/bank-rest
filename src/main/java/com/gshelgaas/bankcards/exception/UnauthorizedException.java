package com.gshelgaas.bankcards.exception;

/**
 * Исключение для ситуаций, когда пользователь не аутентифицирован.
 * Соответствует HTTP статусу 401 Unauthorized.
 *
 * @author Георгий Шельгаас
 */
public class UnauthorizedException extends RuntimeException {

    /**
     * Создает исключение с указанным сообщением об ошибке.
     *
     * @param message сообщение об ошибке
     */
    public UnauthorizedException(String message) {
        super(message);
    }
}