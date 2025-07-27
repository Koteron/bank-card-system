package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.util.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CardService {
    Page<CardDto> searchCards(Specification<Card> specification, Pageable pageable);
    CardDto changeCardStatus(Card card, CardStatus newStatus);
}
