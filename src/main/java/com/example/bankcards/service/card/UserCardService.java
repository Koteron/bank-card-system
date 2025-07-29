package com.example.bankcards.service.card;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

/**
 * Service interface for user-specific card operations.
 */
public interface UserCardService {

    /**
     * Requests a lock on a user's card.
     * The card status will be set to {@code PENDING_LOCK} if the user owns the card and it is active.
     *
     * @param cardId  the ID of the card to lock
     * @param ownerId the ID of the user who owns the card
     * @return a DTO representation of the updated card
     * @throws com.example.bankcards.exception.NotFoundException if the card is not found
     * @throws com.example.bankcards.exception.ForbiddenException if the card is not active or the user is not the owner
     */
    CardDto requestCardLock(UUID cardId, UUID ownerId);

    /**
     * Searches cards owned by the user using provided filtering specification and pagination options.
     *
     * @param specification the JPA specification used to filter results
     * @param pageable      the pagination information
     * @return a page of card DTOs matching the criteria
     */
    Page<CardDto> searchCards(Specification<Card> specification, Pageable pageable);
}
