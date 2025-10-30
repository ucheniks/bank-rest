package com.gshelgaas.bankcards.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

/**
 * Утилита для шифрования и дешифрования данных.
 * Использует AES алгоритм для шифрования номеров банковских карт.
 * Ключ шифрования настраивается через properties файл.
 *
 * @author Георгий Шельгаас
 */
@Slf4j
@Component
public class EncryptionUtil {

    @Value("${encryption.secret:myDefaultEncryptionKey1234567890123456}")
    private String secret;

    /**
     * Создает ключ для шифрования на основе секретной строки.
     * Использует SHA-256 хеш для получения ключа фиксированной длины.
     *
     * @return SecretKeySpec для AES шифрования
     * @throws RuntimeException если генерация ключа не удалась
     */
    private SecretKeySpec getKey() {
        try {
            byte[] key = secret.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            return new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            throw new RuntimeException("Key generation failed", e);
        }
    }

    /**
     * Шифрует данные с использованием AES алгоритма.
     * Используется для шифрования номеров банковских карт перед сохранением в БД.
     *
     * @param data данные для шифрования
     * @return зашифрованная строка в Base64 формате
     * @throws RuntimeException если шифрование не удалось
     */
    public String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, getKey());
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /**
     * Дешифрует данные, зашифрованные методом encrypt.
     * Используется для получения оригинального номера карты перед маскированием.
     *
     * @param encryptedData зашифрованная строка в Base64 формате
     * @return оригинальные данные
     * @throws RuntimeException если дешифрование не удалось
     */
    public String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, getKey());
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}