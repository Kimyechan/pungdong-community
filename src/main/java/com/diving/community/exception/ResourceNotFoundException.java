package com.diving.community.exception;

public class ResourceNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "요청하신 자원은 존재하지 않습니다";

    public ResourceNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
