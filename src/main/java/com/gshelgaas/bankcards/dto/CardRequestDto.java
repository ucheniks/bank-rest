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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardRequestDto {
    @NotBlank(message = "Card number cannot be blank")
    private String cardNumber;

    @NotBlank(message = "Card holder cannot be blank")
    private String cardHolder;

    @Future(message = "Expiry date must be in the future")
    @NotNull(message = "Expiry date cannot be null")
    private LocalDate expiryDate;

    @NotNull(message = "Balance cannot be null")
    @PositiveOrZero(message = "Balance must be positive or zero")
    private BigDecimal balance;
}