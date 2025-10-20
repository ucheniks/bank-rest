package com.gshelgaas.bankcards.service;

import com.gshelgaas.bankcards.dto.AuthRequestDto;
import com.gshelgaas.bankcards.dto.AuthResponseDto;
import com.gshelgaas.bankcards.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void login_withValidCredentials_returnsToken() {
        AuthRequestDto authRequest = new AuthRequestDto("user@mail.ru", "user123");
        UserDetails userDetails = new User("user@mail.ru", "user123", Collections.emptyList());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userDetailsService.loadUserByUsername("user@mail.ru")).thenReturn(userDetails);
        when(jwtUtil.generateToken("user@mail.ru")).thenReturn("jwt-token");

        AuthResponseDto result = authService.login(authRequest);

        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken("user@mail.ru");
    }

    @Test
    void login_withInvalidCredentials_throwsException() {
        AuthRequestDto authRequest = new AuthRequestDto("user@mail.ru", "wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(authRequest));
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void login_withAdminCredentials_returnsToken() {
        AuthRequestDto authRequest = new AuthRequestDto("admin@mail.ru", "admin123");
        UserDetails userDetails = new User("admin@mail.ru", "admin123", Collections.emptyList());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userDetailsService.loadUserByUsername("admin@mail.ru")).thenReturn(userDetails);
        when(jwtUtil.generateToken("admin@mail.ru")).thenReturn("admin-jwt-token");

        AuthResponseDto result = authService.login(authRequest);

        assertNotNull(result);
        assertEquals("admin-jwt-token", result.getToken());
        verify(jwtUtil).generateToken("admin@mail.ru");
    }
}