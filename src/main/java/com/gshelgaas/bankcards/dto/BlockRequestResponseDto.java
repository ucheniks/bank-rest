package com.gshelgaas.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Ответ с информацией о запросе на блокировку.
 *
 * @author Георгий Шельгаас
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockRequestResponseDto {

    /**
     * Уникальный идентификатор запроса.
     */
    private Long id;

    /**
     * Идентификатор карты для блокировки.
     */
    private Long cardId;

    /**
     * Причина блокировки.
     */
    private String reason;

    /**
     * Статус запроса.
     * Возможные значения: PENDING, APPROVED, REJECTED
     */
    private String status;

    /**
     * Дата и время создания запроса.
     */
    private LocalDateTime requestedAt;
}