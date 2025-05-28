package com.example.shieldus.config.security.utils;

import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

@Component
public class RSAUtil {

    private static final String ALGORITHM = "RSA";
    private static final String CIPHER_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public RSAUtil(String privateKeyPath, String publicKeyPath) throws Exception {
        this.privateKey = loadPrivateKey(privateKeyPath);
        this.publicKey = loadPublicKey(publicKeyPath);
    }
    public RSAUtil() throws Exception {

        Path privateKeyPath = getResourcePath("private_key.pem");
        Path publicKeyPath = getResourcePath("public_key.pem");
        this.privateKey = loadPrivateKey(privateKeyPath.toString());
        this.publicKey = loadPublicKey(publicKeyPath.toString());
    }

    private Path getResourcePath(String resourceName) throws URISyntaxException {
        return Paths.get(RSAUtil.class.getClassLoader().getResource(resourceName).toURI());
    }
    /**
     * 개인키 파일에서 PrivateKey 객체를 로드합니다.
     */
    public static PrivateKey loadPrivateKey(String filePath) throws Exception {
        String keyContent = new String(Files.readAllBytes(Paths.get(filePath)));
        String privateKeyPEM = keyContent
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 공개키 파일에서 PublicKey 객체를 로드합니다.
     */
    public static PublicKey loadPublicKey(String filePath) throws Exception {
        String keyContent = new String(Files.readAllBytes(Paths.get(filePath)));
        String publicKeyPEM = keyContent
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 데이터를 공개키로 암호화합니다.
     */
    public byte[] encrypt(byte[] data, PublicKey customPublicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, customPublicKey);
        return cipher.doFinal(data);
    }
    public byte[] encrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);
        return cipher.doFinal(data);
    }
    public byte[] encryptString(String plainText) throws Exception {
        return encrypt(plainText.getBytes(StandardCharsets.UTF_8));
    }
    public String encryptStringBase64(String plainText) throws Exception {
        byte[] encryptedBytes = encrypt(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 데이터를 개인키로 복호화합니다.
     */
    public byte[] decrypt(byte[] data, PrivateKey customPrivateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, customPrivateKey);
        return cipher.doFinal(data);
    }
    public byte[] decrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }
    public String decryptRsaBase64(String encrypted) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encrypted);
            byte[] decrypted = decrypt(decodedBytes);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("RSA 복호화 실패", e);
        }
    }
}