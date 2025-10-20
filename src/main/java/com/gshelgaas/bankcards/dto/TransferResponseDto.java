package com.gshelgaas.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponseDto {
    private Long id;
    private Long fromCardId;
    private Long toCardId;
    private BigDecimal amount;
    private LocalDateTime transferDate;
    private String status;
    private String description;
}