package com.example.bankcards.scheduler;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.util.CardStatus;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Scheduler component responsible for automatically expiring bank cards
 * that have passed their expiration date.
 * <p>
 * This scheduler runs periodically according to the configured cron expression
 * and updates the status of all cards with an expiration date before today,
 * provided they are not already marked as {@link CardStatus#EXPIRED}.
 */
@Component
@RequiredArgsConstructor
public class CardExpirationScheduler {

    private static final Logger log = LoggerFactory.getLogger(CardExpirationScheduler.class);
    private final CardRepository cardRepository;

    /**
     * Scheduled method that runs at fixed intervals (defined via cron expression)
     * to check for expired cards and update their status to {@link CardStatus#EXPIRED}.
     * <p>
     * The cron expression is read from the `app.cards.validity-check-cron` property.
     */
    @Scheduled(cron = "${app.cards.validity-check-cron}")
    public void expireCards() {
        log.info("Checking expired cards...");
        List<Card> expiredCards = cardRepository.findAllByExpirationDateBeforeAndStatusNot(
                LocalDate.now(), CardStatus.EXPIRED
        );

        for (Card card : expiredCards) {
            card.setStatus(CardStatus.EXPIRED);
        }
        log.info("{} cards expired", expiredCards.size());
        cardRepository.saveAll(expiredCards);
    }
}
