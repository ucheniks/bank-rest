package com.gshelgaas.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Ответ с информацией о выполненном переводе.
 *
 * @author Георгий Шельгаас
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponseDto {

    /**
     * Уникальный идентификатор перевода.
     */
    private Long id;

    /**
     * Идентификатор карты-отправителя.
     */
    private Long fromCardId;

    /**
     * Идентификатор карты-получателя.
     */
    private Long toCardId;

    /**
     * Сумма перевода.
     */
    private BigDecimal amount;

    /**
     * Дата и время выполнения перевода.
     */
    private LocalDateTime transferDate;

    /**
     * Статус перевода.
     * Возможные значения: SUCCESS, FAILED, PENDING
     */
    private String status;

    /**
     * Описание перевода.
     */
    private String description;
}