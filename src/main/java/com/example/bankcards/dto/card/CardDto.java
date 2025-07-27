package com.example.bankcards.dto.card;

import com.example.bankcards.entity.util.CardStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

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

        double balance
) {
}
