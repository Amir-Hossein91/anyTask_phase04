package com.example.phase_04.exceptions;

public class InvalidImageException extends RuntimeException{
    public InvalidImageException(String msg){
        super(msg);
    }
}
