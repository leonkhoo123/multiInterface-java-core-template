package com.leon.rest_api.exception;

public class NovelNotFoundException extends RuntimeException {
    public NovelNotFoundException(String message) {
        super(message);
    }
}