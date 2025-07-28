package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.CardCreationDto;
import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.card.CardStatusUpdateDto;
import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface AdminCardService {
    CardDto createCard(CardCreationDto cardCreationDto);
    void deleteCardById(UUID cardId);
    CardDto getCardById(UUID cardId);
    CardDto updateCardStatus(CardStatusUpdateDto cardStatusUpdateDto);
    Page<CardDto> searchCards(Specification<Card> specification, Pageable pageable);
}
