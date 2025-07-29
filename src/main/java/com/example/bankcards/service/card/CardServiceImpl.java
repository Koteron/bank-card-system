package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.util.CardStatus;
import com.example.bankcards.exception.ForbiddenException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.InternalServerErrorException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.AesEncryptionUtil;
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
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final AesEncryptionUtil aesEncryptionUtil;

    public Page<CardDto> searchCards(Specification<Card> specification, Pageable pageable){
        return cardRepository.findAll(specification, pageable).map(cardMapper::toCardDto);
    }

    @Override
    public CardDto changeCardStatus(Card card, CardStatus newStatus) {
        card.setStatus(newStatus);
        return cardMapper.toCardDto(cardRepository.save(card));
    }

    @Override
    public CardDto updateCard(Card updatedCard) {
        return cardMapper.toCardDto(cardRepository.save(updatedCard));
    }

    @Override
    public Card getEntityById(UUID cardId) {
        return cardRepository.findById(cardId).orElseThrow(
                () -> new NotFoundException("Card not found!"));
    }

    @Override
    public Card getEntityByCardNumber(String cardNumber) {
        String encryptedNumber;
        try {
            encryptedNumber = aesEncryptionUtil.encrypt(cardNumber);
        }
        catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
        return cardRepository.findByEncryptedNumber(encryptedNumber).orElseThrow(
                () -> new NotFoundException("Card not found!"));
    }

    @Override
    public void checkCardOwnership(UUID cardId, UUID userId) {
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new NotFoundException("Card not found!"));
        checkCardOwnership(card, userId);
    }

    public void checkCardOwnership(Card card, UUID userId) {
        if (!card.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("User is not an owner of this card!");
        }
    }
}
