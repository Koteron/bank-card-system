package com.example.bankcards.exception;

public class CardExistsException extends InternalServerErrorException {
    public CardExistsException(String message) {super(message);}
}
