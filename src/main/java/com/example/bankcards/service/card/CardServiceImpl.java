package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.util.CardStatus;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    public Page<CardDto> searchCards(Specification<Card> specification, Pageable pageable){
        return cardRepository.findAll(specification, pageable).map(cardMapper::toCardDto);
    }

    @Override
    public CardDto changeCardStatus(Card card, CardStatus newStatus) {
        card.setStatus(newStatus);
        return cardMapper.toCardDto(cardRepository.save(card));
    }


}
