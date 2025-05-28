package com.example.shieldus.config.security.utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;


public class RSAUtil {

    private static final String ALGORITHM = "RSA";
    private static final String CIPHER_TRANSFORMATION = "RSA/ECB/PKCS1Padding";

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
    public static byte[] encrypt(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     * 데이터를 개인키로 복호화합니다.
     */
    public static byte[] decrypt(byte[] data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }
}