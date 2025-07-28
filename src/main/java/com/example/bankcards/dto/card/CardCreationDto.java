package com.example.bankcards.dto.card;

import com.example.bankcards.entity.util.Currency;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CardCreationDto (
        @NotNull(message = "User id cannot be null")
        @JsonProperty(value = "user_id")
        UUID userId,

        @NotNull(message = "Currency cannot be null")
        Currency currency
) {
}