package com.example.bankcards.util;

import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;

@Slf4j
public class CardNumberUtil {
    private static final SecureRandom secureRandom =  new SecureRandom();
    private static final int CARD_NUMBER_LENGTH = 16;
    private static final int UNMASKED_NUMBER_LENGTH = 4;

    private CardNumberUtil() {}

    public static String generateCardNumber() {
        StringBuilder cardNumber = new StringBuilder();
        for(int i = 0; i < CARD_NUMBER_LENGTH; i++) {
            cardNumber.append(secureRandom.nextInt(10));
        }
        return cardNumber.toString();
    }

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
