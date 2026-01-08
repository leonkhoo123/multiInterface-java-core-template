package com.leon.rest_api.exception;

public class NovelSequencenotFoundException extends RuntimeException {
    public NovelSequencenotFoundException(String message) {
        super(message);
    }
}