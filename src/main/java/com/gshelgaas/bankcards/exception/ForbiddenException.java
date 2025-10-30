package com.gshelgaas.bankcards.exception;

/**
 * Исключение для ситуаций, когда доступ к ресурсу запрещен.
 * Соответствует HTTP статусу 403 Forbidden.
 * Используется когда пользователь аутентифицирован, но не имеет необходимых прав.
 *
 * @author Георгий Шельгаас
 */
public class ForbiddenException extends RuntimeException {

    /**
     * Создает исключение с указанным сообщением об ошибке.
     *
     * @param message сообщение об ошибке
     */
    public ForbiddenException(String message) {
        super(message);
    }
}
