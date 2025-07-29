package com.example.bankcards.util;

import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;

/**
 * Utility class for generating and masking credit/debit card numbers.
 * <p>
 * This class uses a secure random number generator to create 16-digit numeric card numbers.
 * It also supports masking card numbers, leaving only the last 4 digits visible.
 */
@Slf4j
public class CardNumberUtil {
    private static final SecureRandom secureRandom =  new SecureRandom();
    private static final int CARD_NUMBER_LENGTH = 16;
    private static final int UNMASKED_NUMBER_LENGTH = 4;

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private CardNumberUtil() {}

    /**
     * Generates a new random 16-digit card number.
     *
     * @return a randomly generated numeric card number as a String
     */
    public static String generateCardNumber() {
        StringBuilder cardNumber = new StringBuilder();
        for(int i = 0; i < CARD_NUMBER_LENGTH; i++) {
            cardNumber.append(secureRandom.nextInt(10));
        }
        return cardNumber.toString();
    }

    /**
     * Masks the given card number by replacing all but the last 4 digits with asterisks.
     * <p>
     * Example: "1234567890123456" → "************3456"
     *
     * @param cardNumber the original card number
     * @return the masked card number
     */
    public static String maskCardNumber(String cardNumber) {
        StringBuilder maskedCardNumber = new StringBuilder();
        int maskedNumberLength = CARD_NUMBER_LENGTH - UNMASKED_NUMBER_LENGTH;
        maskedCardNumber.append("*".repeat(maskedNumberLength));
        for(int i = maskedNumberLength; i < CARD_NUMBER_LENGTH; i++) {
            maskedCardNumber.append(cardNumber.charAt(i));
        }
        return maskedCardNumber.toString();
    }
}
