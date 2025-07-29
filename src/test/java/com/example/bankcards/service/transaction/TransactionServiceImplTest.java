package com.example.bankcards.service.transaction;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.transaction.OneCardOperationDto;
import com.example.bankcards.dto.transaction.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.util.Currency;
import com.example.bankcards.exception.ForbiddenException;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.service.card.CardService;
import com.example.bankcards.service.currency.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CardService cardService;

    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User user;
    private UUID userId;
    private Card cardFrom;
    private Card cardTo;
    private CardDto cardDtoFrom;
    private CardDto cardDtoTo;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);

        cardFrom = new Card();
        cardFrom.setId(UUID.randomUUID());
        cardFrom.setOwner(user);
        cardFrom.setBalance(new BigDecimal("100.00"));
        cardFrom.setCurrency(Currency.USD);

        cardTo = new Card();
        cardTo.setId(UUID.randomUUID());
        cardTo.setOwner(user);
        cardTo.setBalance(new BigDecimal("50.00"));
        cardTo.setCurrency(Currency.EUR);

        cardDtoFrom = CardDto.builder().build();
        cardDtoTo = CardDto.builder().build();
    }

    @Test
    void transfer_successful_decreasesAndIncreasesBalances_andSavesTransaction() {
        BigDecimal amount = new BigDecimal("20.00");
        UUID fromId = cardFrom.getId();
        UUID toId = cardTo.getId();
        TransferRequestDto dto = new TransferRequestDto(fromId,
                null, toId, null, amount);

        when(cardService.getEntityById(fromId)).thenReturn(cardFrom);
        when(cardService.getEntityById(toId)).thenReturn(cardTo);
        doNothing().when(cardService).checkCardOwnership(cardFrom, userId);
        doNothing().when(cardService).checkCardOwnership(cardTo, userId);
        when(currencyService.convert(amount, Currency.USD, Currency.EUR)).thenReturn(
                new BigDecimal("18.00"));
        when(cardService.updateCard(cardFrom)).thenReturn(cardDtoFrom);
        when(cardService.updateCard(cardTo)).thenReturn(cardDtoTo);

        CardDto result = transactionService.transfer(user, dto);

        assertEquals(cardDtoFrom, result);
        assertEquals(new BigDecimal("80.00"), cardFrom.getBalance());
        assertEquals(new BigDecimal("68.00"), cardTo.getBalance());

        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void transfer_insufficientBalance_throwsForbidden() {
        BigDecimal amount = new BigDecimal("200.00");
        TransferRequestDto dto = new TransferRequestDto(cardFrom.getId(),
                null, cardTo.getId(), null, amount);

        when(cardService.getEntityById(cardFrom.getId())).thenReturn(cardFrom);
        doNothing().when(cardService).checkCardOwnership(cardFrom, userId);

        assertThrows(ForbiddenException.class, () -> transactionService.transfer(user, dto));
        verify(cardService).checkCardOwnership(cardFrom, userId);
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void deposit_successful_addsConvertedAmount_andSavesTransaction() {
        BigDecimal amount = new BigDecimal("30.00");
        OneCardOperationDto dto = new OneCardOperationDto(
                cardTo.getId(), null, Currency.USD, amount);

        when(cardService.getEntityById(cardTo.getId())).thenReturn(cardTo);
        doNothing().when(cardService).checkCardOwnership(cardTo, userId);
        when(currencyService.convert(amount, Currency.USD, Currency.EUR))
                .thenReturn(new BigDecimal("25.50"));
        when(cardService.updateCard(cardTo)).thenReturn(cardDtoTo);

        CardDto result = transactionService.deposit(user, dto);

        assertEquals(cardDtoTo, result);
        assertEquals(new BigDecimal("75.50"), cardTo.getBalance());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void withdraw_successful_subtractsConvertedAmount_andSavesTransaction() {
        BigDecimal amount = new BigDecimal("20.00");
        OneCardOperationDto dto = new OneCardOperationDto(cardFrom.getId(),
                null, Currency.USD, amount);

        when(cardService.getEntityById(cardFrom.getId())).thenReturn(cardFrom);
        doNothing().when(cardService).checkCardOwnership(cardFrom, userId);
        when(currencyService.convert(amount, Currency.USD, Currency.USD)).thenReturn(amount);
        when(cardService.updateCard(cardFrom)).thenReturn(cardDtoFrom);

        CardDto result = transactionService.withdraw(user, dto);

        assertEquals(cardDtoFrom, result);
        assertEquals(new BigDecimal("80.00"), cardFrom.getBalance());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void withdraw_insufficientBalance_throwsForbidden() {
        BigDecimal amount = new BigDecimal("200.00");
        OneCardOperationDto dto = new OneCardOperationDto(cardFrom.getId(),
                null, Currency.USD, amount);

        when(cardService.getEntityById(cardFrom.getId())).thenReturn(cardFrom);
        doNothing().when(cardService).checkCardOwnership(cardFrom, userId);

        assertThrows(ForbiddenException.class, () -> transactionService.withdraw(user, dto));
        verify(transactionRepository, never()).save(any());
    }
}
