package com.gshelgaas.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ответ с результатом аутентификации.
 * Содержит JWT токен для доступа к защищенным endpoint'ам.
 *
 * @author Георгий Шельгаас
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {

    /**
     * JWT токен для аутентифицированных запросов.
     * Должен передаваться в заголовке Authorization: Bearer {token}
     */
    private String token;
}