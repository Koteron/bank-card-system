package com.example.bankcards.service.transaction;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.dto.transaction.OneCardOperationDto;
import com.example.bankcards.dto.transaction.TransferRequestDto;
import com.example.bankcards.entity.User;

public interface TransactionService {
    CardDto transfer(User user, TransferRequestDto transferRequestDto);
    CardDto deposit(User user, OneCardOperationDto oneCardOperationDto);
    CardDto withdraw(User user, OneCardOperationDto oneCardOperationDto);
}
