package com.gshelgaas.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockRequestResponseDto {
    private Long id;
    private Long cardId;
    private String reason;
    private String status;
    private LocalDateTime requestedAt;
}