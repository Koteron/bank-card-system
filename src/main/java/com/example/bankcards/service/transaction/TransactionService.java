package com.example.bankcards.service.transaction;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.transaction.OneCardOperationDto;
import com.example.bankcards.dto.transaction.TransferRequestDto;
import com.example.bankcards.entity.User;

/**
 * Service interface for handling financial transactions between and on cards.
 * Includes methods for transfer, deposit, and withdrawal operations.
 */
public interface TransactionService {

    /**
     * Transfers money between two cards owned by the same user. Currency conversion
     * is applied if the source and destination cards have different currencies.
     *
     * @param user the user who owns both cards
     * @param transferRequestDto contains source and destination card identifiers or numbers,
     *                           and the amount to transfer
     * @return updated {@link CardDto} representing the source card after transfer
     * @throws com.example.bankcards.exception.ForbiddenException if the user doesn't own the cards,
     *         if the source card is not active, or if there are insufficient funds
     */
    CardDto transfer(User user, TransferRequestDto transferRequestDto);

    /**
     * Deposits funds to a user's card. Currency conversion is applied if the deposit currency
     * differs from the card's currency.
     *
     * @param user the owner of the target card
     * @param oneCardOperationDto contains the card identifier or number, currency, and deposit amount
     * @return updated {@link CardDto} representing the card after deposit
     * @throws com.example.bankcards.exception.ForbiddenException if the card is not active
     *         or does not belong to the user
     */
    CardDto deposit(User user, OneCardOperationDto oneCardOperationDto);

    /**
     * Withdraws funds from a user's card. Currency conversion is applied if the requested
     * withdrawal currency differs from the card's currency.
     *
     * @param user the owner of the source card
     * @param oneCardOperationDto contains the card identifier or number, currency, and withdrawal amount
     * @return updated {@link CardDto} representing the card after withdrawal
     * @throws com.example.bankcards.exception.ForbiddenException if the card is not active,
     *         doesn't belong to the user, or has insufficient funds
     */
    CardDto withdraw(User user, OneCardOperationDto oneCardOperationDto);
}
