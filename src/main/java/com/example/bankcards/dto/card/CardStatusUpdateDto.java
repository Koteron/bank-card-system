package com.example.bankcards.dto.card;

import com.example.bankcards.entity.util.CardStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

import java.util.UUID;

public record CardStatusUpdateDto(
        @NonNull
        @JsonProperty(value = "card_id")
        UUID cardId,

        @NonNull
        CardStatus status
) {
}
