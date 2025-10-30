package com.gshelgaas.bankcards.service;

import com.gshelgaas.bankcards.dto.UserRequestDto;
import com.gshelgaas.bankcards.dto.UserResponseDto;
import com.gshelgaas.bankcards.entity.User;
import com.gshelgaas.bankcards.exception.ConflictException;
import com.gshelgaas.bankcards.exception.NotFoundException;

import java.util.List;

/**
 * Сервис для управления пользователями системы.
 * Предоставляет функционал для создания, получения и удаления пользователей,
 * а также для работы с текущим аутентифицированным пользователем.
 *
 * @author Георгий Шельгаас
 */
public interface UserService {

    /**
     * Создает нового пользователя в системе.
     *
     * @param userRequestDto данные для создания пользователя
     * @return созданный пользователь (без пароля)
     * @throws ConflictException если пользователь с таким email уже существует
     */
    UserResponseDto createUser(UserRequestDto userRequestDto);

    /**
     * Получает пользователя по идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return информация о пользователе
     * @throws NotFoundException если пользователь не найден
     */
    UserResponseDto getUserById(Long userId);

    /**
     * Получает всех пользователей системы.
     *
     * @return список всех пользователей (без паролей)
     */
    List<UserResponseDto> getAllUsers();

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param userId идентификатор пользователя для удаления
     * @throws NotFoundException если пользователь не найден
     */
    void deleteUser(Long userId);

    /**
     * Получает текущего аутентифицированного пользователя.
     * Используется Spring Security Context для получения email пользователя.
     *
     * @return сущность текущего пользователя
     * @throws NotFoundException если пользователь не найден по email из Security Context
     */
    User getCurrentUser();
}