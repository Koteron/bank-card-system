package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.util.CardStatus;
import com.example.bankcards.exception.ForbiddenException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
class UserCardServiceImplTest {

    @Mock
    private CardService cardService;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private UserCardServiceImpl userCardService;

    private UUID cardId;
    private UUID ownerId;
    private Card card;
    private CardDto cardDto;

    @BeforeEach
    void setUp() {
        cardId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        card = new Card();
        card.setId(cardId);
        card.setOwner(new com.example.bankcards.entity.User());
        card.getOwner().setId(ownerId);
        cardDto = CardDto.builder().build();
    }

    @Test
    void requestCardLock_successful_returnsCardDto() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        doNothing().when(cardService).checkCardOwnership(card, ownerId);
        when(cardService.changeCardStatus(card, CardStatus.PENDING_LOCK)).thenReturn(cardDto);

        CardDto result = userCardService.requestCardLock(cardId, ownerId);

        assertEquals(cardDto, result);
        verify(cardRepository).findById(cardId);
        verify(cardService).checkCardOwnership(card, ownerId);
        verify(cardService).changeCardStatus(card, CardStatus.PENDING_LOCK);
    }

    @Test
    void requestCardLock_cardNotFound_throwsNotFound() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userCardService.requestCardLock(cardId, ownerId));
        verify(cardRepository).findById(cardId);
    }

    @Test
    void requestCardLock_notOwner_throwsForbidden() {
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        doThrow(new ForbiddenException("Not owner")).when(cardService).checkCardOwnership(card, ownerId);

        assertThrows(ForbiddenException.class, () -> userCardService.requestCardLock(cardId, ownerId));
        verify(cardRepository).findById(cardId);
        verify(cardService).checkCardOwnership(card, ownerId);
    }

    @Test
    void searchCards_delegatesToCardService() {
        Specification<Card> spec = (root, query, cb) -> null;
        Pageable pageable = PageRequest.of(0, 5);
        List<CardDto> dtos = List.of(cardDto);
        Page<CardDto> page = new PageImpl<>(dtos, pageable, dtos.size());

        when(cardService.searchCards(spec, pageable)).thenReturn(page);

        Page<CardDto> result = userCardService.searchCards(spec, pageable);

        assertEquals(page, result);
        verify(cardService).searchCards(spec, pageable);
    }
}
