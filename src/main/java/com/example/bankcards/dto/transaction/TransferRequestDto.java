package com.example.bankcards.dto.transaction;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequestDto(

        UUID cardFromId,

        @Size(min = 16, max = 19, message = "Card number must be 16-19 digits")
        String cardFromNumber,

        UUID cardToId,

        @Size(min = 16, max = 19, message = "Card number must be 16-19 digits")
        String cardToNumber,

        @Positive(message = "Amount must be positive")
        BigDecimal amount
) {
        @AssertTrue(message = "Exactly one sender identifier must be provided (cardFromId or cardFromNumber)")
        private boolean isSourceValid() {
                return exactlyOneNonNull(cardFromId, cardFromNumber);
        }

        @AssertTrue(message = "Exactly one recipient identifier must be provided (cardToId or cardToNumber)")
        private boolean isTargetValid() {
                return exactlyOneNonNull(cardToId, cardToNumber);
        }

        private static boolean exactlyOneNonNull(Object a, Object b) {
                return (a != null) ^ (b != null);
        }
}
