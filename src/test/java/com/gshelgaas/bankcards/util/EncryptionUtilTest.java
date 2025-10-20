package com.gshelgaas.bankcards.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EncryptionUtilTest {

    @Test
    void encryptAndDecrypt_withValidData_returnsOriginalData() {
        EncryptionUtil encryptionUtil = new EncryptionUtil();
        ReflectionTestUtils.setField(encryptionUtil, "secret", "myTestEncryptionKey1234567890123456");

        String originalData = "4111111111111111";

        String encrypted = encryptionUtil.encrypt(originalData);
        String decrypted = encryptionUtil.decrypt(encrypted);

        assertNotNull(encrypted);
        assertNotEquals(originalData, encrypted);
        assertEquals(originalData, decrypted);
    }

    @Test
    void encrypt_differentInputs_produceDifferentOutputs() {
        EncryptionUtil encryptionUtil = new EncryptionUtil();
        ReflectionTestUtils.setField(encryptionUtil, "secret", "myTestEncryptionKey1234567890123456");

        String data1 = "4111111111111111";
        String data2 = "4222222222222222";

        String encrypted1 = encryptionUtil.encrypt(data1);
        String encrypted2 = encryptionUtil.encrypt(data2);

        assertNotEquals(encrypted1, encrypted2);
    }

    @Test
    void encrypt_nullInput_throwsException() {
        EncryptionUtil encryptionUtil = new EncryptionUtil();
        ReflectionTestUtils.setField(encryptionUtil, "secret", "myTestEncryptionKey1234567890123456");

        assertThrows(RuntimeException.class, () -> encryptionUtil.encrypt(null));
    }

    @Test
    void decrypt_nullInput_throwsException() {
        EncryptionUtil encryptionUtil = new EncryptionUtil();
        ReflectionTestUtils.setField(encryptionUtil, "secret", "myTestEncryptionKey1234567890123456");

        assertThrows(RuntimeException.class, () -> encryptionUtil.decrypt(null));
    }

    @Test
    void encrypt_emptyString_returnsEncrypted() {
        EncryptionUtil encryptionUtil = new EncryptionUtil();
        ReflectionTestUtils.setField(encryptionUtil, "secret", "myTestEncryptionKey1234567890123456");

        String encrypted = encryptionUtil.encrypt("");
        String decrypted = encryptionUtil.decrypt(encrypted);

        assertNotNull(encrypted);
        assertEquals("", decrypted);
    }
}