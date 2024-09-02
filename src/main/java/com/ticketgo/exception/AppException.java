package com.ticketgo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException {

    private HttpStatus status;
    private String field;
    private String errorMessage;
    // Constructor for field-specific errors
    public AppException(String field, String message, String errorMessage, HttpStatus status) {
        super(message);
        this.field = field;
        this.status = status;
        this.errorMessage = errorMessage;
    }

}
