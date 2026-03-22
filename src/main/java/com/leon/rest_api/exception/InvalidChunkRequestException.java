package com.leon.rest_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidChunkRequestException extends RuntimeException {
    public InvalidChunkRequestException(String message) {
        super(message);
    }
}
