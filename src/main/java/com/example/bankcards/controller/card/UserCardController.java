package com.example.bankcards.controller.card;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.util.CardStatus;
import com.example.bankcards.security.UserDetailsImpl;
import com.example.bankcards.service.card.UserCardService;
import com.example.bankcards.specification.CardSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cards")
@Tag(name = "User - Cards")
public class UserCardController {
    private final UserCardService userCardService;

    @Operation(
            summary = "Search cards with optional filters",
            description = "Returns a paginated list of cards based on optional filters:" +
                    " balance, comparison type, owner ID, and status",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Card's pagination was returned successfully filled with CardDtos",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CardDto.class)
                            )),
                    @ApiResponse(responseCode = "400",
                            description = "Invalid request parameters",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )),
                    @ApiResponse(responseCode = "401",
                            description = "User is not authorized for the action",
                            content = @Content()),
                    @ApiResponse(responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ))
            }
    )
    @GetMapping("/search")
    public Page<CardDto> searchCards(
            @Parameter(description = "Balance to compare", example = "1000.0")
            @RequestParam(name = "balance", required = false) Double balance,

            @Parameter(description = "Whether balance should be greater than the given value (true/false)",
                    example = "true")
            @RequestParam(name = "isGreater", required = false) Boolean isGreater,

            @Parameter(description = "Card status to filter by",
                    schema = @Schema(implementation = CardStatus.class))
            @RequestParam(name = "status", required = false) CardStatus status,

            @Parameter(hidden = true)
            @PageableDefault(page = 0, size = 5, sort = "balance", direction = Sort.Direction.DESC)
            Pageable pageable,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Specification<Card> specification = Specification.where(
                        CardSpecification.hasBalance(balance, isGreater))
                .and(CardSpecification.hasOwnerId(userDetails.getUser().getId()))
                .and(CardSpecification.hasStatus(status));

        return userCardService.searchCards(specification, pageable);
    }

    @Operation(
            summary = "Request card lock",
            description = "Changes card status to \"PENDING_LOCK\" to be later locked by admin",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Card's status was changed",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CardDto.class)
                            )),
                    @ApiResponse(responseCode = "401",
                            description = "User is not authorized for the action",
                            content = @Content()),
                    @ApiResponse(responseCode = "403",
                            description = "Card is not active or user is not an owner of the card",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )),
                    @ApiResponse(responseCode = "404",
                            description = "Card not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )),
                    @ApiResponse(responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            ))
            }
    )
    @PatchMapping("/lock/{card-id}")
    public CardDto requestCardLock(
            @Parameter(description = "UUID of the card",
                    example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("card-id") UUID cardId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return userCardService.requestCardLock(cardId, userDetails.getUser().getId());
    }
}
