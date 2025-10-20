package com.gshelgaas.bankcards.controller;

import com.gshelgaas.bankcards.dto.AuthRequestDto;
import com.gshelgaas.bankcards.dto.AuthResponseDto;
import com.gshelgaas.bankcards.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody AuthRequestDto authRequest) {
        log.info("POST /auth/login - login attempt for email: {}", authRequest.getEmail());
        return authService.login(authRequest);
    }
}