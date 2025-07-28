package com.example.bankcards.dto.card;

import com.example.bankcards.entity.util.CardStatus;
import com.example.bankcards.entity.util.Currency;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Digits;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record CardDto(
        UUID id,

        @JsonProperty(value = "masked_number")
        String maskedNumber,

        @JsonProperty(value = "owner_id")
        UUID ownerId,

        @JsonProperty(value = "expiration_date")
        LocalDate expirationDate,

        CardStatus status,

        Currency currency,

        @Digits(integer = 19, fraction = 2)
        BigDecimal balance
) {
}
