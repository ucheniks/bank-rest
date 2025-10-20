package com.gshelgaas.bankcards.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @Test
    void generateToken_withValidEmail_returnsToken() {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "testSecretKeyForTestingPurposesOnly123");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L);

        String email = "test@test.ru";

        String token = jwtUtil.generateToken(email);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractEmail_withValidToken_returnsEmail() {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "testSecretKeyForTestingPurposesOnly123");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L);

        String email = "test@test.ru";
        String token = jwtUtil.generateToken(email);

        String extractedEmail = jwtUtil.extractEmail(token);

        assertEquals(email, extractedEmail);
    }

    @Test
    void validateToken_withValidToken_returnsTrue() {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "testSecretKeyForTestingPurposesOnly123");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L);

        String token = jwtUtil.generateToken("test@test.ru");

        boolean isValid = jwtUtil.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void validateToken_withInvalidToken_returnsFalse() {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "testSecretKeyForTestingPurposesOnly123");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L);

        boolean isValid = jwtUtil.validateToken("invalid.token.here");

        assertFalse(isValid);
    }
}