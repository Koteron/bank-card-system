package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.util.CardStatus;
import com.example.bankcards.exception.ForbiddenException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.SystemErrorException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.AesEncryptionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private AesEncryptionUtil aesEncryptionUtil;

    private CardServiceImpl cardService;

    private Card card;
    private CardDto cardDto;
    private UUID cardId;
    private UUID ownerId;

    @BeforeEach
    void setUp() {
        cardService = new CardServiceImpl(cardRepository, cardMapper, aesEncryptionUtil);

        cardId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        card = new Card();
        card.setId(cardId);
        card.setOwner(new com.example.bankcards.entity.User());
        card.getOwner().setId(ownerId);
        card.setStatus(CardStatus.ACTIVE);
        cardDto = CardDto.builder().build();
    }

    @Test
    void searchCards_returnsPagedCardDtos() {
        Specification<Card> spec = (root, query, cb) -> null;
        Pageable pageable = PageRequest.of(0, 10);
        List<Card> cards = List.of(card);
        Page<Card> page = new PageImpl<>(cards, pageable, cards.size());

        when(cardRepository.findAll(spec, pageable)).thenReturn(page);
        when(cardMapper.toCardDto(card)).thenReturn(cardDto);

        Page<CardDto> result = cardService.searchCards(spec, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(cardDto, result.getContent().get(0));
        verify(cardRepository).findAll(spec, pageable);
        verify(cardMapper).toCardDto(card);
    }

    @Test
    void changeCardStatus_setsStatusAndReturnsDto() {
        CardStatus newStatus = CardStatus.LOCKED;
        when(cardRepository.save(card)).thenReturn(card);
        when(cardMapper.toCardDto(card)).thenReturn(cardDto);

        CardDto result = cardService.changeCardStatus(card, newStatus);

        assertEquals(newStatus, card.getStatus());
        assertEquals(cardDto, result);
        verify(cardRepository).save(card);
        verify(cardMapper).toCardDto(card);
    }

    @Test
    void updateCard_savesAndReturnsDto() {
        when(cardRepository.save(card)).thenReturn(card);
        when(cardMapper.toCardDto(card)).thenReturn(cardDto);

        CardDto result = cardService.updateCard(card);

        assertEquals(cardDto, result);
        verify(cardRepository).save(card);
        verify(cardMapper).toCardDto(card);
    }

    @Test
    void getEntityById_existingId_returnsCard() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        Card result = cardService.getEntityById(cardId);

        assertEquals(card, result);
        verify(cardRepository).findById(cardId);
    }

    @Test
    void getEntityById_nonExistingId_throwsNotFound() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cardService.getEntityById(cardId));
        verify(cardRepository).findById(cardId);
    }

    @Test
    void getEntityByCardNumber_successfulDecryptionAndLookup() throws Exception {
        String number = "1234";
        String encrypted = "enc1234";
        when(aesEncryptionUtil.encrypt(number)).thenReturn(encrypted);
        when(cardRepository.findByEncryptedNumber(encrypted)).thenReturn(Optional.of(card));

        Card result = cardService.getEntityByCardNumber(number);

        assertEquals(card, result);
        verify(aesEncryptionUtil).encrypt(number);
        verify(cardRepository).findByEncryptedNumber(encrypted);
    }

    @Test
    void getEntityByCardNumber_encryptionFails_throwsSystemError() throws Exception {
        String number = "1234";
        when(aesEncryptionUtil.encrypt(number)).thenThrow(new RuntimeException("fail"));

        assertThrows(SystemErrorException.class, () -> cardService.getEntityByCardNumber(number));
        verify(aesEncryptionUtil).encrypt(number);
    }

    @Test
    void getEntityByCardNumber_notFound_throwsNotFound() throws Exception {
        String number = "1234";
        String encrypted = "enc1234";
        when(aesEncryptionUtil.encrypt(number)).thenReturn(encrypted);
        when(cardRepository.findByEncryptedNumber(encrypted)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cardService.getEntityByCardNumber(number));
        verify(aesEncryptionUtil).encrypt(number);
        verify(cardRepository).findByEncryptedNumber(encrypted);
    }

    @Test
    void checkCardOwnership_byId_validOwner_passes() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        assertDoesNotThrow(() -> cardService.checkCardOwnership(cardId, ownerId));
        verify(cardRepository).findById(cardId);
    }

    @Test
    void checkCardOwnership_byId_cardNotFound_throwsNotFound() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cardService.checkCardOwnership(cardId, ownerId));
        verify(cardRepository).findById(cardId);
    }

    @Test
    void checkCardOwnership_byCard_validOwner_passes() {
        assertDoesNotThrow(() -> cardService.checkCardOwnership(card, ownerId));
    }

    @Test
    void checkCardOwnership_byCard_invalidOwner_throwsForbidden() {
        UUID otherId = UUID.randomUUID();
        assertThrows(ForbiddenException.class, () -> cardService.checkCardOwnership(card, otherId));
    }
}
