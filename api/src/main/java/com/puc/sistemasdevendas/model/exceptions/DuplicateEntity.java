package com.puc.sistemasdevendas.model.exceptions;

public class DuplicateEntity extends RuntimeException {
    public DuplicateEntity(String message) {
        super(message);
    }
}
