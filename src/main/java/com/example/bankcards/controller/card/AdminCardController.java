package com.example.bankcards.controller.card;

import com.example.bankcards.dto.card.CardCreationDto;
import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.card.CardStatusUpdateDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.util.CardStatus;
import com.example.bankcards.service.card.AdminCardService;
import com.example.bankcards.specification.CardSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/cards")
@RequiredArgsConstructor
@Tag(name = "Admin - Cards", description = "Admin operations for cards")
public class AdminCardController {

    private static final Logger log = LoggerFactory.getLogger(AdminCardController.class);
    private final AdminCardService adminCardService;

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

            @Parameter(description = "UUID of the card owner",
                    example = "123e4567-e89b-12d3-a456-426614174000")
            @RequestParam(name = "ownerId", required = false) UUID ownerId,

            @Parameter(description = "Card status to filter by",
                    schema = @Schema(implementation = CardStatus.class))
            @RequestParam(name = "status", required = false) CardStatus status,

            @Parameter(hidden = true)
            @PageableDefault(size = 5, sort = "balance", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        log.debug("Admin request received: Search cards with: " +
                "balance = {}, isGreater = {}, ownerId = {}, status = {}", balance, isGreater, ownerId, status);

        Specification<Card> specification = Specification.where(
                        CardSpecification.hasBalance(balance, isGreater))
                .and(CardSpecification.hasOwnerId(ownerId))
                .and(CardSpecification.hasStatus(status));

        return adminCardService.searchCards(specification, pageable);
    }

    @Operation(
            summary = "Get card by id",
            description = "Returns card dto with given id",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Card was found and returned",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CardDto.class)
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
    @GetMapping("/get/{id}")
    public CardDto getCardById(
            @Parameter(description = "UUID of the card",
                    example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("id") UUID id
    ) {
        log.debug("Admin request received: Get card by id: {}", id);
        return adminCardService.getCardById(id);
    }

    @Operation(
            summary = "Create card",
            description = "Creates a card with given currency for given user",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Card creation parameters",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CardCreationDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Card was created and returned",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CardDto.class)
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
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public CardDto createCard(@RequestBody CardCreationDto cardCreationDto) {
        log.debug("Request received: Create card in {} for user with id: {}",
                cardCreationDto.currency(), cardCreationDto.userId());
        return adminCardService.createCard(cardCreationDto);
    }

    @Operation(
            summary = "Delete card by id",
            description = "Deletes card with given id",
            responses = {
                    @ApiResponse(responseCode = "204",
                            description = "Card was deleted",
                            content = @Content()),
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
    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(
            @Parameter(description = "UUID of the card",
                    example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("id") UUID id
    ) {
        log.debug("Admin request received: Delete card with id: {}", id);
        adminCardService.deleteCardById(id);
    }

    @Operation(
            summary = "Update card status",
            description = "Changes status of the card with given id to a desired one",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Card status update parameters",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CardStatusUpdateDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Card's status was updated",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CardDto.class)
                            )),
                    @ApiResponse(responseCode = "401",
                            description = "User is not authorized for the action",
                            content = @Content()),
                    @ApiResponse(responseCode = "404",
                            description = "Card with given id not found",
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
    @PatchMapping("/status")
    public CardDto updateCardStatus(@RequestBody @Valid CardStatusUpdateDto cardStatusUpdateDto) {
        log.debug("Admin request received: Update status of card with id {} to {}",
                cardStatusUpdateDto.cardId(), cardStatusUpdateDto.status());
        return adminCardService.updateCardStatus(cardStatusUpdateDto);
    }
}
