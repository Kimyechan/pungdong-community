package com.diving.community.exception;

public class NoPermissionsException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "요청하신 자원은 존재하지 않습니다";

    public NoPermissionsException() {
        super(DEFAULT_MESSAGE);
    }

    public NoPermissionsException(String message) {
        super(message);
    }

    public NoPermissionsException(String message, Throwable cause) {
        super(message, cause);
    }
}
