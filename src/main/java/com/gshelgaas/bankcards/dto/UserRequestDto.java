package com.gshelgaas.bankcards.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Запрос на создание пользователя.
 * Используется при регистрации новых пользователей.
 *
 * @author Георгий Шельгаас
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    /**
     * Имя пользователя.
     * Не может быть пустым.
     */
    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    /**
     * Фамилия пользователя.
     * Не может быть пустой.
     */
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    /**
     * Email пользователя.
     * Должен быть уникальным в системе.
     */
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    /**
     * Пароль пользователя.
     * Будет захеширован перед сохранением в базу данных.
     */
    @NotBlank(message = "Password cannot be blank")
    private String password;
}