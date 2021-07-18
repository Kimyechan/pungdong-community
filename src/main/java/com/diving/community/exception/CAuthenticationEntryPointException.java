package com.diving.community.exception;

public class CAuthenticationEntryPointException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "해당 리소스에 접근하기 위한 권한이 없습니다.";

    public CAuthenticationEntryPointException() {
        super(DEFAULT_MESSAGE);
    }

    public CAuthenticationEntryPointException(String message) {
        super(message);
    }

    public CAuthenticationEntryPointException(String message, Throwable cause) {
        super(message, cause);
    }
}
