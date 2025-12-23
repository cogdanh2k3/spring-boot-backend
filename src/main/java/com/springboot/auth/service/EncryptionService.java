package com.springboot.auth.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Service for encrypting and decrypting sensitive user data.
 * Uses AES-256 for data encryption and RSA-2048 to protect AES keys.
 */
@Service
public class EncryptionService {

    @Value("${encryption.rsa.key.path:src/main/resources/keys/}")
    private String rsaKeyPath;

    @Value("${encryption.rsa.key.size:2048}")
    private int rsaKeySize;

    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";
    private static final int AES_KEY_SIZE = 256;
    private static final int IV_SIZE = 16; // 128 bits for AES

    private PrivateKey rsaPrivateKey;
    private PublicKey rsaPublicKey;

    /**
     * Initialize RSA keys after Spring dependency injection
     */
    @PostConstruct
    public void init() {
        try {
            initializeRsaKeys();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize encryption service", e);
        }
    }

    /**
     * Initialize RSA key pair - load from file or generate new ones
     */
    private void initializeRsaKeys() throws Exception {
        File keyDir = new File(rsaKeyPath);
        File privateKeyFile = new File(keyDir, "private_key.der");
        File publicKeyFile = new File(keyDir, "public_key.der");

        if (privateKeyFile.exists() && publicKeyFile.exists()) {
            // Load existing keys
            rsaPrivateKey = loadPrivateKey(privateKeyFile);
            rsaPublicKey = loadPublicKey(publicKeyFile);
            System.out.println("Loaded existing RSA keys from: " + rsaKeyPath);
        } else {
            // Generate new keys
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(rsaKeySize);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            rsaPrivateKey = keyPair.getPrivate();
            rsaPublicKey = keyPair.getPublic();

            // Save keys to files
            keyDir.mkdirs();
            savePrivateKey(privateKeyFile, rsaPrivateKey);
            savePublicKey(publicKeyFile, rsaPublicKey);
            System.out.println("Generated and saved new RSA keys to: " + rsaKeyPath);
        }
    }

    /**
     * Load private key from file
     */
    private PrivateKey loadPrivateKey(File file) throws Exception {
        byte[] keyBytes = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(keyBytes);
        }
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    /**
     * Load public key from file
     */
    private PublicKey loadPublicKey(File file) throws Exception {
        byte[] keyBytes = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(keyBytes);
        }
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    /**
     * Save private key to file
     */
    private void savePrivateKey(File file, PrivateKey privateKey) throws Exception {
        byte[] keyBytes = privateKey.getEncoded();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(keyBytes);
        }
    }

    /**
     * Save public key to file
     */
    private void savePublicKey(File file, PublicKey publicKey) throws Exception {
        byte[] keyBytes = publicKey.getEncoded();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(keyBytes);
        }
    }

    /**
     * Generate a random AES key
     */
    private SecretKey generateAesKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(AES_KEY_SIZE);
        return keyGenerator.generateKey();
    }

    /**
     * Generate a random IV (Initialization Vector)
     */
    private byte[] generateIv() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    /**
     * Encrypt data with AES-256
     */
    private String encryptWithAes(String data, SecretKey aesKey, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec);

        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Decrypt data with AES-256
     */
    private String decryptWithAes(String encryptedData, SecretKey aesKey, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec);

        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * Encrypt AES key with RSA public key
     */
    private String encryptAesKey(SecretKey aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);

        byte[] encryptedKey = cipher.doFinal(aesKey.getEncoded());
        return Base64.getEncoder().encodeToString(encryptedKey);
    }

    /**
     * Decrypt AES key with RSA private key
     */
    private SecretKey decryptAesKey(String encryptedKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);

        byte[] encryptedKeyBytes = Base64.getDecoder().decode(encryptedKey);
        byte[] decryptedKeyBytes = cipher.doFinal(encryptedKeyBytes);
        return new SecretKeySpec(decryptedKeyBytes, "AES");
    }

    /**
     * Encrypt sensitive data
     * Returns format: {IV}:{encryptedData}:{encryptedAesKey}
     */
    public String encryptData(String data) {
        if (data == null || data.trim().isEmpty()) {
            return null;
        }

        try {
            // Generate AES key and IV
            SecretKey aesKey = generateAesKey();
            byte[] iv = generateIv();

            // Encrypt data with AES
            String encryptedData = encryptWithAes(data, aesKey, iv);

            // Encrypt AES key with RSA
            String encryptedAesKey = encryptAesKey(aesKey);

            // Encode IV to Base64
            String ivBase64 = Base64.getEncoder().encodeToString(iv);

            // Return combined format: IV:encryptedData:encryptedAesKey
            return ivBase64 + ":" + encryptedData + ":" + encryptedAesKey;

        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt data", e);
        }
    }

    /**
     * Decrypt sensitive data
     * Expects format: {IV}:{encryptedData}:{encryptedAesKey}
     */
    public String decryptData(String encryptedValue) {
        if (encryptedValue == null || encryptedValue.trim().isEmpty()) {
            return null;
        }

        try {
            // Split the encrypted value
            String[] parts = encryptedValue.split(":");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid encrypted data format");
            }

            String ivBase64 = parts[0];
            String encryptedData = parts[1];
            String encryptedAesKey = parts[2];

            // Decode IV
            byte[] iv = Base64.getDecoder().decode(ivBase64);

            // Decrypt AES key with RSA
            SecretKey aesKey = decryptAesKey(encryptedAesKey);

            // Decrypt data with AES
            return decryptWithAes(encryptedData, aesKey, iv);

        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt data", e);
        }
    }

    /**
     * Check if a value is encrypted (basic check)
     */
    public boolean isEncrypted(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        // Check if it matches the format: {base64}:{base64}:{base64}
        String[] parts = value.split(":");
        return parts.length == 3;
    }
}
