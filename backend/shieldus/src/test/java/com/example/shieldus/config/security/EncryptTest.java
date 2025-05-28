package com.example.shieldus.config.security;

import com.example.shieldus.config.security.utils.RSAUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
@Disabled
public class EncryptTest {
    private static RSAUtil rsaUtil;
    private static final String ORIGINAL_TEXT = "test";


    @BeforeAll
    static void setup() throws Exception {
        // 테스트 리소스 디렉토리에서 키 파일 로드
        Path privateKeyPath = getResourcePath("private_key.pem");
        Path publicKeyPath = getResourcePath("public_key.pem");

        rsaUtil = new RSAUtil(privateKeyPath.toString(), publicKeyPath.toString());
    }

    private static Path getResourcePath(String resourceName) throws URISyntaxException {
        return Paths.get(EncryptTest.class.getClassLoader().getResource(resourceName).toURI());
    }


    @Test
    @DisplayName("공개키로 암호화 후 개인키로 복호화 테스트")
    void tesBase64EncryptDecrypt() throws Exception {
        String rsaBase64Text = rsaUtil.encryptStringBase64(ORIGINAL_TEXT);
        System.out.println(rsaBase64Text);

        String plainText = rsaUtil.decryptRsaBase64(rsaBase64Text);
        System.out.println(plainText);
    }
    @Test

    @Disabled
    @DisplayName("공개키로 암호화 후 개인키로 복호화 테스트")
    void testEncryptionDecryptionWithRSAKeys() throws Exception {
        // 1. 원본 텍스트를 바이트 배열로 변환
        byte[] originalBytes = ORIGINAL_TEXT.getBytes("UTF-8");

        // 2. 공개키로 암호화
        byte[] encryptedBytes = rsaUtil.encrypt(originalBytes);
        assertNotNull(encryptedBytes, "암호화된 바이트 배열은 null이 아니어야 합니다.");
        assertTrue(encryptedBytes.length > 0, "암호화된 바이트 배열은 비어있지 않아야 합니다.");

        System.out.println("원본 텍스트: " + ORIGINAL_TEXT);
        System.out.println("암호화된 텍스트 (Base64): " + Base64.getEncoder().encodeToString(encryptedBytes));

        // 3. 개인키로 복호화
        byte[] decryptedBytes = rsaUtil.decrypt(encryptedBytes);
        assertNotNull(decryptedBytes, "복호화된 바이트 배열은 null이 아니어야 합니다.");
        assertTrue(decryptedBytes.length > 0, "복호화된 바이트 배열은 비어있지 않아야 합니다.");

        String decryptedText = new String(decryptedBytes, "UTF-8");
        System.out.println("복호화된 텍스트: " + decryptedText);

        // 4. 원본 텍스트와 복호화된 텍스트 비교
        assertEquals(ORIGINAL_TEXT, decryptedText, "원본 텍스트와 복호화된 텍스트가 일치해야 합니다.");
    }

    @Test

    @Disabled
    @DisplayName("암호화/복호화 실패 시 예외 발생 테스트 (잘못된 키)")
    void testDecryptionFailureWithWrongKey() {
        // 임의의 바이트 배열 (유효하지 않은 암호문)
        byte[] wrongEncryptedBytes = "잘못된 암호문입니다".getBytes();

        // 개인키가 아닌 공개키로 복호화 시도 (혹은 다른 키로 복호화 시도)
        assertThrows(Exception.class, () -> {
            rsaUtil.decrypt(wrongEncryptedBytes);
        }, "잘못된 키로 복호화 시도 시 예외가 발생해야 합니다.");
    }
}