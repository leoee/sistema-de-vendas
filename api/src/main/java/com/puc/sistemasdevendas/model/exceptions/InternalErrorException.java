package com.puc.sistemasdevendas.model.exceptions;

public class InternalErrorException extends RuntimeException{
    public InternalErrorException(String message) {
        super(message);
    }
}
