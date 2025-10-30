package com.gshelgaas.bankcards.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Запрос на блокировку карты от пользователя.
 *
 * @author Георгий Шельгаас
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockRequestDto {

    /**
     * Причина блокировки карты.
     * Обязательна для заполнения пользователем.
     */
    @NotBlank(message = "Reason cannot be blank")
    private String reason;
}