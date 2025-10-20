package com.gshelgaas.bankcards.service;

import com.gshelgaas.bankcards.dto.AuthRequestDto;
import com.gshelgaas.bankcards.dto.AuthResponseDto;
import com.gshelgaas.bankcards.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponseDto login(AuthRequestDto authRequest) {
        log.info("Login attempt for email: {}", authRequest.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        String token = jwtUtil.generateToken(authRequest.getEmail());

        log.info("Login successful for email: {}", authRequest.getEmail());
        return AuthResponseDto.builder()
                .token(token)
                .build();
    }
}