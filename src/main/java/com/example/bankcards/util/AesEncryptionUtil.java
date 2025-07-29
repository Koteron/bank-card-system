package com.example.bankcards.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for AES encryption and decryption using CBC mode with PKCS5 padding.
 * <p>
 * The secret key must be provided as a base64-encoded string via the `aes.secret-key` property.
 * Each encryption generates a new random IV and prepends it to the resulting ciphertext.
 */
@Component
public class AesEncryptionUtil {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int IV_LENGTH = 16;

    private final SecretKey secretKey;

    public AesEncryptionUtil(@Value("${aes.secret-key}") String base64Key) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    /**
     * Encrypts the given plaintext using AES/CBC/PKCS5Padding with a random IV.
     *
     * @param plainText the string to encrypt
     * @return a base64-encoded string containing the IV and ciphertext
     * @throws Exception if encryption fails
     */
    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        byte[] iv = new byte[IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

        byte[] encrypted = cipher.doFinal(plainText.getBytes());

        byte[] ivAndCipher = new byte[IV_LENGTH + encrypted.length];
        System.arraycopy(iv, 0, ivAndCipher, 0, IV_LENGTH);
        System.arraycopy(encrypted, 0, ivAndCipher, IV_LENGTH, encrypted.length);

        return Base64.getEncoder().encodeToString(ivAndCipher);
    }

    /**
     * Decrypts a base64-encoded string that contains both the IV and the encrypted data.
     *
     * @param base64IvAndCipher base64 string containing the IV and ciphertext
     * @return the original plaintext
     * @throws Exception if decryption fails
     */
    public String decrypt(String base64IvAndCipher) throws Exception {
        byte[] ivAndCipher = Base64.getDecoder().decode(base64IvAndCipher);

        byte[] iv = new byte[IV_LENGTH];
        byte[] encrypted = new byte[ivAndCipher.length - IV_LENGTH];

        System.arraycopy(ivAndCipher, 0, iv, 0, IV_LENGTH);
        System.arraycopy(ivAndCipher, IV_LENGTH, encrypted, 0, encrypted.length);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted);
    }
}
