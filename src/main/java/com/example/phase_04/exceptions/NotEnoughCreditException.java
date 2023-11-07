package com.example.phase_04.exceptions;

public class NotEnoughCreditException extends RuntimeException{
    public NotEnoughCreditException(String message) {
        super(message);
    }
}
