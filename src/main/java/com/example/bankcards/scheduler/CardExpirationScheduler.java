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

@Component
@RequiredArgsConstructor
public class CardExpirationScheduler {

    private static final Logger log = LoggerFactory.getLogger(CardExpirationScheduler.class);
    private final CardRepository cardRepository;

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
