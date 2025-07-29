package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.util.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

/**
 * Service interface that provides operations for working with cards.
 */
public interface CardService {

    /**
     * Searches for cards matching the provided specification with pagination support.
     *
     * @param specification JPA specification used to filter the results
     * @param pageable      pagination and sorting information
     * @return page of cards matching the specification
     */
    Page<CardDto> searchCards(Specification<Card> specification, Pageable pageable);

    /**
     * Changes the status of a given card and returns the updated DTO.
     *
     * @param card      card entity to update
     * @param newStatus new status to set
     * @return updated card DTO
     */
    CardDto changeCardStatus(Card card, CardStatus newStatus);

    /**
     * Updates an existing card in the repository.
     *
     * @param updatedCard card entity with updated fields
     * @return updated card DTO
     */
    CardDto updateCard(Card updatedCard);

    /**
     * Retrieves a card entity by its unique ID.
     *
     * @param cardId the UUID of the card
     * @return the corresponding card entity
     * @throws com.example.bankcards.exception.NotFoundException if the card is not found
     */
    Card getEntityById(UUID cardId);

    /**
     * Retrieves a card entity by its (plaintext) card number.
     * The number is encrypted before lookup.
     *
     * @param cardNumber the plain card number
     * @return the corresponding card entity
     * @throws com.example.bankcards.exception.NotFoundException if the card is not found
     * @throws com.example.bankcards.exception.InternalServerErrorException if encryption fails
     */
    Card getEntityByCardNumber(String cardNumber);

    /**
     * Checks if the given user is the owner of the card with the provided ID.
     *
     * @param cardId  the UUID of the card
     * @param ownerId the UUID of the user
     * @throws com.example.bankcards.exception.ForbiddenException if the user is not the owner
     * @throws com.example.bankcards.exception.NotFoundException if the card is not found
     */
    void checkCardOwnership(UUID cardId, UUID ownerId);

    /**
     * Checks if the given user is the owner of the provided card.
     *
     * @param card    the card entity
     * @param ownerId the UUID of the user
     * @throws com.example.bankcards.exception.ForbiddenException if the user is not the owner
     */
    void checkCardOwnership(Card card, UUID ownerId);
}
