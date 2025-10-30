package com.gshelgaas.bankcards.exception;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * DTO для стандартизированного представления ошибок API.
 * Используется для возвращения структурированных ошибок клиентам.
 *
 * @author Георгий Шельгаас
 */
@Getter
@Builder
public class ApiError {

    /**
     * Сообщение об ошибке для разработчика.
     */
    private final String message;

    /**
     * Причина ошибки для конечного пользователя.
     */
    private final String reason;

    /**
     * HTTP статус ошибки.
     */
    private final String status;

    /**
     * Временная метка возникновения ошибки.
     */
    private final String timestamp;

    /**
     * Детали ошибки (опционально).
     * Может содержать список полей с ошибками валидации.
     */
    private final List<String> errors;
}