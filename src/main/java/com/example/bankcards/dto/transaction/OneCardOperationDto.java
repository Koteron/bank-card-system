package com.example.bankcards.dto.transaction;

import com.example.bankcards.entity.util.Currency;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record OneCardOperationDto(

        UUID cardId,

        @Size(min = 16, max = 19, message = "Card number must be 16-19 digits")
        String cardNumber,

        @NotNull(message = "Currency cannot be null")
        Currency currency,

        @Positive(message = "Amount must be positive")
        BigDecimal amount
) {
    @AssertTrue(message = "cardId must be null when cardNumber is provided")
    private boolean isCardIdNullWhenCardNumberProvided() {
        return cardNumber == null || cardId == null;
    }

    @AssertTrue(message = "cardNumber must be null when cardId is provided")
    private boolean isCardNumberNullWhenCardIdProvided() {
        return cardId == null || cardNumber == null;
    }

    @AssertTrue(message = "Exactly one card identifier must be provided (cardId or cardNumber)")
    private boolean isCardIdentifierValid() {
        return (cardId != null) ^ (cardNumber != null);
    }
}
