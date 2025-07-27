package com.example.bankcards.exception;

public class CardExistsException extends SystemErrorException{
    public CardExistsException(String message) {super(message);}
}
