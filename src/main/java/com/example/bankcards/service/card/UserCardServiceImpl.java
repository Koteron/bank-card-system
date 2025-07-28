package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.util.CardStatus;
import com.example.bankcards.exception.ForbiddenException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class UserCardServiceImpl implements UserCardService {
    private final CardService cardService;
    private final CardRepository cardRepository;

    @Override
    public CardDto requestCardLock(UUID cardId, UUID ownerId) {
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new NotFoundException("Card not found!"));

        cardService.checkCardOwnership(card, ownerId);

        return cardService.changeCardStatus(card, CardStatus.PENDING_LOCK);
    }



    @Override
    public Page<CardDto> searchCards(Specification<Card> specification, Pageable pageable) {
        return cardService.searchCards(specification, pageable);
    }
}
