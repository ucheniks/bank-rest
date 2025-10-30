package com.gshelgaas.bankcards.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Запрос на аутентификацию пользователя.
 * Используется для входа в систему.
 *
 * @author Георгий Шельгаас
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDto {

    /**
     * Email пользователя.
     * Должен быть валидным email адресом.
     */
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;

    /**
     * Пароль пользователя.
     * Передается в открытом виде и проверяется на сервере.
     */
    @NotBlank(message = "Password cannot be blank")
    private String password;
}