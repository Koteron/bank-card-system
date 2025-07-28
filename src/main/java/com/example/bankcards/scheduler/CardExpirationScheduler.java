package com.example.bankcards.scheduler;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.util.CardStatus;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CardExpirationScheduler {

    private final CardRepository cardRepository;

    @Scheduled(cron = "${app.cards.validity-check-cron}")
    public void expireCards() {
        List<Card> expiredCards = cardRepository.findAllByExpirationDateBeforeAndStatusNot(
                LocalDate.now(), CardStatus.EXPIRED
        );

        for (Card card : expiredCards) {
            card.setStatus(CardStatus.EXPIRED);
        }

        cardRepository.saveAll(expiredCards);
    }
}
