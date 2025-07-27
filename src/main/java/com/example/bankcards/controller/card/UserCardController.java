package com.example.bankcards.controller.card;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.util.CardStatus;
import com.example.bankcards.security.UserDetailsImpl;
import com.example.bankcards.service.card.UserCardService;
import com.example.bankcards.specification.CardSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/cards")
public class UserCardController {
    private final UserCardService userCardService;

    @GetMapping("/search")
    public Page<CardDto> searchCards(
            @RequestParam(required = false) Double balance,
            @RequestParam(required = false) Boolean isGreater,
            @RequestParam(required = false) CardStatus status,

            @PageableDefault(page = 0, size = 5, sort = "balance", direction = Sort.Direction.DESC)
            Pageable pageable,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Specification<Card> specification = Specification.where(
                        CardSpecification.hasBalance(balance, isGreater))
                .and(CardSpecification.hasOwnerId(userDetails.getUser().getId()))
                .and(CardSpecification.hasStatus(status));

        return userCardService.searchCards(specification, pageable);
    }

    @PatchMapping("/lock/{card-id}")
    public CardDto requestCardLock(
            @PathVariable("card-id") UUID cardId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return userCardService.requestCardLock(cardId, userDetails.getUser().getId());
    }
}
