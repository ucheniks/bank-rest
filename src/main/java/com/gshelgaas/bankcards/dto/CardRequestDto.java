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
    @NotBlank
    private String cardNumber;

    @NotBlank
    private String cardHolder;

    @Future
    @NotNull
    private LocalDate expiryDate;

    @NotNull
    @PositiveOrZero
    private BigDecimal balance;
}