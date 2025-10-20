package com.gshelgaas.bankcards.dto;

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
public class CardResponseDto {
    private Long id;
    private String cardNumber;
    private String cardHolder;
    private LocalDate expiryDate;
    private String status;
    private BigDecimal balance;
    private Long userId;
}