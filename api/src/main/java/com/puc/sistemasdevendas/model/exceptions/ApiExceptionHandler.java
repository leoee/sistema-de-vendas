package com.puc.sistemasdevendas.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(InternalErrorException.class)
    public ResponseEntity<GlobalError> internalServerError(InternalErrorException e) {
        GlobalError globalError = new GlobalError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(globalError);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<GlobalError> badRequest(BadRequestException e) {
        GlobalError globalError = new GlobalError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(globalError);
    }

    @ExceptionHandler(DuplicateEntity.class)
    public ResponseEntity<GlobalError> duplicateEntity(DuplicateEntity e) {
        GlobalError globalError = new GlobalError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(globalError);
    }

    @ExceptionHandler(UserNotAuthorizedException.class)
    public ResponseEntity<GlobalError> unauthorizedError(UserNotAuthorizedException e) {
        GlobalError globalError = new GlobalError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(globalError);
    }

    @ExceptionHandler(ForbidenException.class)
    public ResponseEntity<GlobalError> forbiddenError(ForbidenException e) {
        GlobalError globalError = new GlobalError(HttpStatus.FORBIDDEN.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(globalError);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<GlobalError> notFoundError(NotFoundException e) {
        GlobalError globalError = new GlobalError(HttpStatus.NOT_FOUND.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(globalError);
    }
}
