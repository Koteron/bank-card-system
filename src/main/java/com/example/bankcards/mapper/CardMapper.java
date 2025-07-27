package com.example.bankcards.mapper;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.SystemErrorException;
import com.example.bankcards.util.AesEncryptionUtil;
import com.example.bankcards.util.CardNumberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardMapper {
    private final AesEncryptionUtil aesEncryptionUtil;

    public CardDto toCardDto(Card card) {
        String decryptedCardNumber;
        try {
            decryptedCardNumber = aesEncryptionUtil.decrypt(card.getEncryptedNumber());
        }
        catch (Exception e) {
            throw new SystemErrorException(e.getMessage());
        }
        return CardDto.builder()
                .id(card.getId())
                .balance(card.getBalance())
                .maskedNumber(CardNumberUtil.maskCardNumber(decryptedCardNumber))
                .expirationDate(card.getExpirationDate())
                .ownerId(card.getOwner().getId())
                .status(card.getStatus())
                .build();
    }
}
