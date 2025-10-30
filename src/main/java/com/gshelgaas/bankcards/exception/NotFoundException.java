package com.gshelgaas.bankcards.exception;

/**
 * Базовое исключение для ситуаций, когда запрашиваемый ресурс не найден.
 * Соответствует HTTP статусу 404 Not Found.
 *
 * @author Георгий Шельгаас
 */
public class NotFoundException extends RuntimeException {

    /**
     * Создает исключение с указанным сообщением об ошибке.
     *
     * @param message сообщение об ошибке
     */
    public NotFoundException(String message) {
        super(message);
    }
}
