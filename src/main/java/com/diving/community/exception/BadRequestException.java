package com.diving.community.exception;

public class BadRequestException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "요청 정보가 잘못 되었습니다";
    public BadRequestException() {
        super(DEFAULT_MESSAGE);
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
