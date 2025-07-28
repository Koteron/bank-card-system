package com.example.bankcards.service.transaction;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.transaction.OneCardOperationDto;
import com.example.bankcards.dto.transaction.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.util.TransactionType;
import com.example.bankcards.exception.ForbiddenException;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.service.card.CardService;
import com.example.bankcards.service.currency.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final CardService cardService;
    private final CurrencyService currencyService;

    @Override
    @Transactional
    public CardDto transfer(User user, TransferRequestDto transferRequestDto) {
        Card cardFrom;
        if (transferRequestDto.cardFromId() != null) {
            cardFrom = cardService.getEntityById(transferRequestDto.cardFromId());
        }
        else {
            cardFrom = cardService.getEntityByCardNumber(transferRequestDto.cardFromNumber());
        }
        cardService.checkCardOwnership(cardFrom, user.getId());

        Card cardTo;
        if (transferRequestDto.cardFromId() != null) {
            cardTo = cardService.getEntityById(transferRequestDto.cardToId());
        }
        else {
            cardTo = cardService.getEntityByCardNumber(transferRequestDto.cardToNumber());
        }
        cardService.checkCardOwnership(cardTo, user.getId());

        if (cardFrom.getBalance().compareTo(transferRequestDto.amount()) < 0) {
            throw new ForbiddenException("Insufficient balance");
        }

        BigDecimal convertedAmount = currencyService.convert(
                transferRequestDto.amount(),
                cardFrom.getCurrency(),
                cardTo.getCurrency()
        );

        cardFrom.setBalance(cardFrom.getBalance().subtract(transferRequestDto.amount()));
        cardTo.setBalance(cardTo.getBalance().add(convertedAmount));

        CardDto updatedCardFrom = cardService.updateCard(cardFrom);
        cardService.updateCard(cardTo);

        transactionRepository.save(
                Transaction.builder()
                        .amount(transferRequestDto.amount())
                        .cardFrom(cardFrom)
                        .cardTo(cardTo)
                        .currencyFrom(cardFrom.getCurrency())
                        .currencyTo(cardTo.getCurrency())
                        .timestamp(LocalDateTime.now())
                        .type(TransactionType.TRANSFER)
                        .build());
        return updatedCardFrom;
    }

    @Override
    @Transactional
    public CardDto deposit(User user, OneCardOperationDto oneCardOperationDto) {
        Card card;
        if (oneCardOperationDto.cardId() != null) {
            card = cardService.getEntityById(oneCardOperationDto.cardId());
        }
        else {
            card = cardService.getEntityByCardNumber(oneCardOperationDto.cardNumber());
        }
        cardService.checkCardOwnership(card, user.getId());

        BigDecimal convertedAmount = currencyService.convert(
                oneCardOperationDto.amount(),
                oneCardOperationDto.currency(),
                card.getCurrency()
        );

        card.setBalance(card.getBalance().add(convertedAmount));

        CardDto updatedCard = cardService.updateCard(card);

        transactionRepository.save(
                Transaction.builder()
                        .amount(oneCardOperationDto.amount())
                        .cardFrom(null)
                        .cardTo(card)
                        .currencyFrom(oneCardOperationDto.currency())
                        .currencyTo(card.getCurrency())
                        .timestamp(LocalDateTime.now())
                        .type(TransactionType.DEPOSIT)
                        .build());
        return updatedCard;
    }

    @Override
    @Transactional
    public CardDto withdraw(User user, OneCardOperationDto oneCardOperationDto) {
        Card card;
        if (oneCardOperationDto.cardId() != null) {
            card = cardService.getEntityById(oneCardOperationDto.cardId());
        }
        else {
            card = cardService.getEntityByCardNumber(oneCardOperationDto.cardNumber());
        }
        cardService.checkCardOwnership(card, user.getId());

        if (card.getBalance().compareTo(oneCardOperationDto.amount()) < 0) {
            throw new ForbiddenException("Insufficient balance");
        }

        BigDecimal convertedAmount = currencyService.convert(
                oneCardOperationDto.amount(),
                oneCardOperationDto.currency(),
                card.getCurrency()
        );
        card.setBalance(card.getBalance().subtract(convertedAmount));

        CardDto updatedCard = cardService.updateCard(card);

        transactionRepository.save(
                Transaction.builder()
                        .amount(oneCardOperationDto.amount())
                        .cardFrom(card)
                        .cardTo(null)
                        .currencyFrom(card.getCurrency())
                        .currencyTo(oneCardOperationDto.currency())
                        .timestamp(LocalDateTime.now())
                        .type(TransactionType.WITHDRAWAL)
                        .build());
        return updatedCard;
    }
}
