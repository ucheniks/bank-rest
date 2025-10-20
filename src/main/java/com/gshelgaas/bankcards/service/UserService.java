package com.gshelgaas.bankcards.service;

import com.gshelgaas.bankcards.dto.UserRequestDto;
import com.gshelgaas.bankcards.dto.UserResponseDto;
import com.gshelgaas.bankcards.entity.User;

import java.util.List;

public interface UserService {
    UserResponseDto createUser(UserRequestDto userRequestDto);
    UserResponseDto getUserById(Long userId);
    List<UserResponseDto> getAllUsers();
    void deleteUser(Long userId);
    User getCurrentUser();
}