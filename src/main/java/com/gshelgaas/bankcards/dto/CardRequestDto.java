package com.gshelgaas.bankcards.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Запрос на создание банковской карты.
 * Используется администратором для создания новых карт.
 *
 * @author Георгий Шельгаас
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardRequestDto {

    /**
     * Номер банковской карты.
     * Будет зашифрован перед сохранением в базу данных.
     */
    @NotBlank(message = "Card number cannot be blank")
    private String cardNumber;

    /**
     * Имя владельца карты.
     * Должно совпадать с именем на физической карте.
     */
    @NotBlank(message = "Card holder cannot be blank")
    private String cardHolder;

    /**
     * Дата истечения срока действия карты.
     * Должна быть в будущем.
     */
    @Future(message = "Expiry date must be in the future")
    @NotNull(message = "Expiry date cannot be null")
    private LocalDate expiryDate;

    /**
     * Начальный баланс карты.
     * Не может быть отрицательным.
     */
    @NotNull(message = "Balance cannot be null")
    @PositiveOrZero(message = "Balance must be positive or zero")
    private BigDecimal balance;
}