package com.example.bankcards.controller.card;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.card.CardStatusUpdateDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.util.CardStatus;
import com.example.bankcards.service.card.AdminCardService;
import com.example.bankcards.specification.CardSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/cards")
@RequiredArgsConstructor
public class AdminCardController {

    private final AdminCardService adminCardService;

    @GetMapping("/search")
    public Page<CardDto> searchCards(
            @RequestParam(required = false) Double balance,
            @RequestParam(required = false) Boolean isGreater,
            @RequestParam(required = false) UUID ownerId,
            @RequestParam(required = false) CardStatus status,

            @PageableDefault(page = 0, size = 5, sort = "balance", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Specification<Card> specification = Specification.where(
                        CardSpecification.hasBalance(balance, isGreater))
                .and(CardSpecification.hasOwnerId(ownerId))
                .and(CardSpecification.hasStatus(status));

        return adminCardService.searchCards(specification, pageable);
    }

    @GetMapping("/get/{id}")
    public CardDto getCardById(@PathVariable("id") UUID id) {
        return adminCardService.getCardById(id);
    }

    @PostMapping("/create/{owner-id}")
    public CardDto createCard(@PathVariable("owner-id") UUID ownerId) {
        return adminCardService.createCard(ownerId);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable("id") UUID id) {
        adminCardService.deleteCardById(id);
    }

    @PatchMapping("/status")
    public CardDto updateCardStatus(@RequestBody @Valid CardStatusUpdateDto cardStatusUpdateDto) {
        return adminCardService.updateCardStatus(cardStatusUpdateDto);
    }

}
