package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface UserCardService {
    CardDto requestCardLock(UUID cardId, UUID ownerId);
    Page<CardDto> searchCards(Specification<Card> specification, Pageable pageable);
}
