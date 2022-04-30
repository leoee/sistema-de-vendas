package com.puc.sistemasdevendas.model.exceptions;

public class UserNotAuthorized extends RuntimeException {
    public UserNotAuthorized(String message) {
        super(message);
    }
}
