package com.example.bankcards.mapper;

import com.example.bankcards.dto.card.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.InternalServerErrorException;
import com.example.bankcards.util.AesEncryptionUtil;
import com.example.bankcards.util.CardNumberUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class CardMapper {

    protected AesEncryptionUtil aesEncryptionUtil;

    @Autowired
    public void setAesEncryptionUtil(AesEncryptionUtil aesEncryptionUtil) {
        this.aesEncryptionUtil = aesEncryptionUtil;
    }

    @Mapping(target = "maskedNumber", expression = "java(getMaskedNumber(card.getEncryptedNumber()))")
    @Mapping(target = "ownerId", source = "owner.id")
    public abstract CardDto toCardDto(Card card);

    protected String getMaskedNumber(String encryptedNumber) {
        try {
            String decrypted = aesEncryptionUtil.decrypt(encryptedNumber);
            return CardNumberUtil.maskCardNumber(decrypted);
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}
