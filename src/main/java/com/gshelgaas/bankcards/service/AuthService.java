package com.gshelgaas.bankcards.service;

import com.gshelgaas.bankcards.dto.AuthRequestDto;
import com.gshelgaas.bankcards.dto.AuthResponseDto;

public interface AuthService {
    AuthResponseDto login(AuthRequestDto authRequest);
}