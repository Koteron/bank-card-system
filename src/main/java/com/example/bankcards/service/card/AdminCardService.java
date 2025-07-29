package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.CardCreationDto;
import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.card.CardStatusUpdateDto;
import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

/**
 * Service interface for managing card-related administrative operations,
 * such as creating, deleting, updating status, and retrieving cards.
 */
public interface AdminCardService {

    /**
     * Creates a new card for the specified user.
     * May retry on duplicate card number generation.
     *
     * @param cardCreationDto the DTO containing card creation details
     * @return the created card as a DTO
     */
    CardDto createCard(CardCreationDto cardCreationDto);

    /**
     * Deletes a card by its ID.
     *
     * @param cardId the UUID of the card to delete
     */
    void deleteCardById(UUID cardId);

    /**
     * Retrieves a card by its ID.
     *
     * @param cardId the UUID of the card
     * @return the corresponding card DTO
     */
    CardDto getCardById(UUID cardId);

    /**
     * Updates the status of a card (e.g. ACTIVE → LOCKED).
     *
     * @param cardStatusUpdateDto DTO containing the card ID and new status
     * @return updated card DTO
     */
    CardDto updateCardStatus(CardStatusUpdateDto cardStatusUpdateDto);

    /**
     * Searches for cards based on the provided JPA specification and pagination parameters.
     *
     * @param specification criteria to filter cards
     * @param pageable      pagination and sorting information
     * @return page of matching card DTOs
     */
    Page<CardDto> searchCards(Specification<Card> specification, Pageable pageable);
}
