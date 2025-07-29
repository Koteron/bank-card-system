package com.example.bankcards.controller.transaction;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.transaction.OneCardOperationDto;
import com.example.bankcards.dto.transaction.TransferRequestDto;
import com.example.bankcards.security.UserDetailsImpl;
import com.example.bankcards.service.transaction.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @Operation(
            summary = "Transfer money from one user's card to another",
            description = "Transfers money from card to card with currency conversion",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Transfer parameters",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TransferRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Card with updated balance returned",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CardDto.class)
                            )),
                    @ApiResponse(responseCode = "401",
                            description = "User is not authorized for the action",
                            content = @Content()),
                    @ApiResponse(responseCode = "403",
                            description = "One of the cards is not active or user" +
                                    " is not an owner of the card",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )),
                    @ApiResponse(responseCode = "404",
                            description = "One of the cards not found",
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
    @PostMapping("/transfer")
    public CardDto transfer(
            @RequestBody @Valid TransferRequestDto request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return transactionService.transfer(userDetails.getUser(), request);
    }

    @Operation(
            summary = "Deposit money to user's card",
            description = "Deposits money to user's card with currency conversion",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Parameters needed for one card operation",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OneCardOperationDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Card with updated balance returned",
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
    @PostMapping("/deposit")
    public CardDto deposit(
            @RequestBody @Valid OneCardOperationDto request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return transactionService.deposit(userDetails.getUser(), request);
    }

    @Operation(
            summary = "Withdraw money to user's card",
            description = "Withdraws money from user's card with currency conversion",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Parameters needed for one card operation",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OneCardOperationDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Card with updated balance returned",
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
    @PostMapping("/withdraw")
    public CardDto withdraw(
            @RequestBody @Valid OneCardOperationDto request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return transactionService.withdraw(userDetails.getUser(), request);
    }
}
