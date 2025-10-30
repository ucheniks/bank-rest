package com.gshelgaas.bankcards.exception;

/**
 * Исключение для ситуаций конфликта бизнес-логики.
 * Соответствует HTTP статусу 409 Conflict.
 * Используется когда операция не может быть выполнена из-за текущего состояния системы.
 *
 * @author Георгий Шельгаас
 */
public class ConflictException extends RuntimeException {

    /**
     * Создает исключение с указанным сообщением об ошибке.
     *
     * @param message сообщение об ошибке
     */
    public ConflictException(String message) {
        super(message);
    }
}
