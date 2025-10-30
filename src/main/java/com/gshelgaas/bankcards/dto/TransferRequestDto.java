package com.gshelgaas.bankcards.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Запрос на перевод между картами.
 * Используется для выполнения денежных переводов.
 *
 * @author Георгий Шельгаас
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDto {

    /**
     * Идентификатор карты-отправителя.
     * Должна принадлежать текущему пользователю.
     */
    @NotNull(message = "From card ID cannot be null")
    private Long fromCardId;

    /**
     * Идентификатор карты-получателя.
     */
    @NotNull(message = "To card ID cannot be null")
    private Long toCardId;

    /**
     * Сумма перевода.
     * Должна быть положительной.
     */
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    /**
     * Описание перевода (необязательное).
     * Может содержать комментарий к операции.
     */
    private String description;
}