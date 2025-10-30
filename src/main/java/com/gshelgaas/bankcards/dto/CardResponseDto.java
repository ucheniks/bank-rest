package com.gshelgaas.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Ответ с информацией о банковской карте.
 * Номер карты возвращается в замаскированном виде.
 *
 * @author Георгий Шельгаас
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardResponseDto {

    /**
     * Уникальный идентификатор карты.
     */
    private Long id;

    /**
     * Номер карты в замаскированном виде.
     * Формат: **** **** **** 1234
     */
    private String cardNumber;

    /**
     * Имя владельца карты.
     */
    private String cardHolder;

    /**
     * Дата истечения срока действия карты.
     */
    private LocalDate expiryDate;

    /**
     * Текущий статус карты.
     * Возможные значения: ACTIVE, BLOCKED, EXPIRED, PENDING_BLOCK
     */
    private String status;

    /**
     * Текущий баланс карты.
     */
    private BigDecimal balance;

    /**
     * Идентификатор пользователя-владельца карты.
     */
    private Long userId;
}