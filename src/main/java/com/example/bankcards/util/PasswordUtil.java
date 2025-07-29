package com.example.bankcards.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Utility class for handling password encryption and verification.
 * <p>
 * This class uses a {@link PasswordEncoder} (e.g. BCrypt) to securely hash passwords and
 * verify plaintext passwords against hashed ones.
 */
@Component
@RequiredArgsConstructor
public class PasswordUtil {
    private final PasswordEncoder encoder;

    /**
     * Encrypts (hashes) the given plaintext password.
     *
     * @param password the plaintext password to encrypt
     * @return the hashed password
     */
    public String encrypt(String password) {
        return encoder.encode(password);
    }

    /**
     * Verifies that a plaintext password matches the given hashed password.
     *
     * @param password the raw plaintext password to verify
     * @param encryptedPassword the previously hashed password
     * @return {@code true} if the password matches, {@code false} otherwise
     */
    public boolean matches(String password, String encryptedPassword) {
        return encoder.matches(password, encryptedPassword);
    }
}

