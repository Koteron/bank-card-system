package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.CardCreationDto;
import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.card.CardStatusUpdateDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.util.CardStatus;
import com.example.bankcards.exception.CardExistsException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.InternalServerErrorException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.user.UserService;
import com.example.bankcards.util.AesEncryptionUtil;
import com.example.bankcards.util.CardNumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class AdminCardServiceImpl implements AdminCardService {
    private final static int CARD_CREATION_RETRY_AMOUNT = 5;
    private final static int CARD_CREATION_RETRY_MILLS = 50;
    private final static double CARD_INITIAL_BALANCE = 0.0;

    private final long cardTtl;

    public AdminCardServiceImpl(@Value("${app.cards.validity-period-days}") long cardTtl,
                                CardRepository cardRepository,
                                AesEncryptionUtil aesEncryptionUtil,
                                CardMapper cardMapper,
                                UserService userService,
                                CardService cardService) {
        this.cardTtl = cardTtl;
        this.cardRepository = cardRepository;
        this.aesEncryptionUtil = aesEncryptionUtil;
        this.cardMapper = cardMapper;
        this.userService = userService;
        this.cardService = cardService;
    }

    private final CardRepository cardRepository;
    private final AesEncryptionUtil aesEncryptionUtil;
    private final CardMapper cardMapper;
    private final UserService userService;
    private final CardService cardService;

    @Override
    @Retryable(retryFor = CardExistsException.class,
            maxAttempts = CARD_CREATION_RETRY_AMOUNT,
            backoff = @Backoff(delay = CARD_CREATION_RETRY_MILLS))
    public CardDto createCard(CardCreationDto cardCreationDto) {
        User owner = userService.getEntityById(cardCreationDto.userId());

        String encryptedCardNumber;
        try {
            encryptedCardNumber = aesEncryptionUtil.encrypt(
                    CardNumberUtil.generateCardNumber());
        }
        catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
        if (cardRepository.existsByEncryptedNumber(encryptedCardNumber))
        {
            throw new CardExistsException("Failed to generate a unique card number");
        }

        Card newCard = cardRepository.save(
                Card.builder()
                .owner(owner)
                .expirationDate(LocalDate.now().plusDays(cardTtl))
                .balance(BigDecimal.valueOf(CARD_INITIAL_BALANCE))
                .currency(cardCreationDto.currency())
                .status(CardStatus.ACTIVE)
                .encryptedNumber(encryptedCardNumber)
                .build());
        CardDto cardDto = cardMapper.toCardDto(newCard);
        log.info("New card with id {} was created for user with id {}",
                newCard.getId(), owner.getId());
        return cardDto;
    }

    @Override
    public void deleteCardById(UUID cardId) {
        log.info("Deleting card with id {}", cardId);
        cardRepository.deleteById(cardId);
    }

    @Override
    public CardDto getCardById(UUID cardId) {
        return cardMapper.toCardDto(cardService.getEntityById(cardId));
    }

    @Override
    public CardDto updateCardStatus(CardStatusUpdateDto cardStatusUpdateDto) {
        Card card = cardRepository.findById(cardStatusUpdateDto.cardId()).orElseThrow(
                () -> new NotFoundException("Card not found!"));
        CardDto cardDto = cardService.changeCardStatus(card, cardStatusUpdateDto.status());
        log.info("Status of card with id {} was updated to {}",
                card.getId(), cardStatusUpdateDto.status());
        return cardDto;
    }

    @Override
    public Page<CardDto> searchCards(Specification<Card> specification, Pageable pageable) {
        Page<CardDto> page = cardService.searchCards(specification, pageable);
        log.info("Successfully returned card list");
        return page;
    }
}
