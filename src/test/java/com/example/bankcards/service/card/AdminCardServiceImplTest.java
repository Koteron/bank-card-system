package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.CardCreationDto;
import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.card.CardStatusUpdateDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.util.CardStatus;
import com.example.bankcards.entity.util.Currency;
import com.example.bankcards.exception.CardExistsException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.InternalServerErrorException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.user.UserService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private AesEncryptionUtil aesEncryptionUtil;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private UserService userService;

    @Mock
    private CardService cardService;

    private AdminCardServiceImpl adminCardService;

    private UUID userId;
    private CardCreationDto creationDto;
    private Card card;
    private CardDto cardDto;

    @BeforeEach
    void setUp() {
        long cardTtl = 30L;
        adminCardService = new AdminCardServiceImpl(
                cardTtl,
                cardRepository,
                aesEncryptionUtil,
                cardMapper,
                userService,
                cardService
        );

        userId = UUID.randomUUID();
        creationDto = new CardCreationDto(userId, Currency.USD);
        card = new Card();
        card.setId(UUID.randomUUID());
        card.setOwner(new User());
        card.getOwner().setId(userId);
        card.setBalance(BigDecimal.ZERO);
        card.setCurrency(Currency.USD);
        card.setStatus(CardStatus.ACTIVE);
        card.setExpirationDate(LocalDate.now());
        card.setEncryptedNumber("enc123");
        cardDto = CardDto.builder().build();
    }

    @Test
    void createCard_successful_returnsCardDto() throws Exception {
        when(userService.getEntityById(userId)).thenReturn(card.getOwner());
        when(aesEncryptionUtil.encrypt(anyString())).thenReturn("enc123");
        when(cardRepository.existsByEncryptedNumber("enc123")).thenReturn(false);
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toCardDto(card)).thenReturn(cardDto);

        CardDto result = adminCardService.createCard(creationDto);

        assertEquals(cardDto, result);
        verify(userService).getEntityById(userId);
        verify(aesEncryptionUtil).encrypt(anyString());
        verify(cardRepository).existsByEncryptedNumber("enc123");
        verify(cardRepository).save(any(Card.class));
        verify(cardMapper).toCardDto(card);
    }

    @Test
    void createCard_encryptionFails_throwsSystemError() throws Exception {
        when(aesEncryptionUtil.encrypt(anyString())).thenThrow(new RuntimeException("fail"));

        assertThrows(InternalServerErrorException.class, () -> adminCardService.createCard(creationDto));
        verify(aesEncryptionUtil).encrypt(anyString());
        verifyNoInteractions(cardRepository);
    }

    @Test
    void createCard_duplicate_throwsCardExists() throws Exception {
        when(aesEncryptionUtil.encrypt(anyString())).thenReturn("enc123");
        when(cardRepository.existsByEncryptedNumber("enc123")).thenReturn(true);

        assertThrows(CardExistsException.class, () -> adminCardService.createCard(creationDto));
        verify(aesEncryptionUtil).encrypt(anyString());
        verify(cardRepository).existsByEncryptedNumber("enc123");
    }

    @Test
    void deleteCardById_callsRepository() {
        UUID id = UUID.randomUUID();

        adminCardService.deleteCardById(id);

        verify(cardRepository).deleteById(id);
    }

    @Test
    void getCardById_delegatesToCardServiceAndMapper() {
        UUID id = UUID.randomUUID();
        Card found = new Card();
        CardDto dto = CardDto.builder().build();

        when(cardService.getEntityById(id)).thenReturn(found);
        when(cardMapper.toCardDto(found)).thenReturn(dto);

        CardDto result = adminCardService.getCardById(id);

        assertEquals(dto, result);
        verify(cardService).getEntityById(id);
        verify(cardMapper).toCardDto(found);
    }

    @Test
    void updateCardStatus_successful_returnsDto() {
        UUID id = UUID.randomUUID();
        CardStatusUpdateDto statusDto = new CardStatusUpdateDto(id, CardStatus.LOCKED);
        when(cardRepository.findById(id)).thenReturn(Optional.of(card));
        when(cardService.changeCardStatus(card, CardStatus.LOCKED)).thenReturn(cardDto);

        CardDto result = adminCardService.updateCardStatus(statusDto);

        assertEquals(cardDto, result);
        verify(cardRepository).findById(id);
        verify(cardService).changeCardStatus(card, CardStatus.LOCKED);
    }

    @Test
    void updateCardStatus_notFound_throwsNotFound() {
        UUID id = UUID.randomUUID();
        CardStatusUpdateDto statusDto = new CardStatusUpdateDto(id, CardStatus.LOCKED);
        when(cardRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> adminCardService.updateCardStatus(statusDto));
        verify(cardRepository).findById(id);
    }

    @Test
    void searchCards_delegatesToCardService() {
        Specification<Card> spec = (root, query, cb) -> null;
        Pageable pageable = PageRequest.of(0, 5);
        Page<CardDto> page = new PageImpl<>(List.of(cardDto), pageable, 1);

        when(cardService.searchCards(spec, pageable)).thenReturn(page);

        Page<CardDto> result = adminCardService.searchCards(spec, pageable);

        assertEquals(page, result);
        verify(cardService).searchCards(spec, pageable);
    }
}
