package com.example.bankcards.dto.card;

import com.example.bankcards.entity.util.CardStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CardStatusUpdateDto(
        @NotNull(message = "Currency cannot be blank")
        @JsonProperty(value = "card_id")
        UUID cardId,

        @NotNull(message = "Currency cannot be null")
        CardStatus status
) {
}
