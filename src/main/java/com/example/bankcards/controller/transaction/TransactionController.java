package com.example.bankcards.controller.transaction;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.transaction.OneCardOperationDto;
import com.example.bankcards.dto.transaction.TransferRequestDto;
import com.example.bankcards.security.UserDetailsImpl;
import com.example.bankcards.service.transaction.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public CardDto transfer(
            @RequestBody @Valid TransferRequestDto request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return transactionService.transfer(userDetails.getUser(), request);
    }

    @PostMapping("/deposit")
    public CardDto deposit(
            @RequestBody @Valid OneCardOperationDto request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return transactionService.deposit(userDetails.getUser(), request);
    }

    @PostMapping("/withdraw")
    public CardDto withdraw(
            @RequestBody @Valid OneCardOperationDto request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return transactionService.withdraw(userDetails.getUser(), request);
    }
}
