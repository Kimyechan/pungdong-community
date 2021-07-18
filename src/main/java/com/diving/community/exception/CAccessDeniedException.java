package com.diving.community.exception;

public class CAccessDeniedException extends RuntimeException{
    private static final String DEFAULT_MESSAGE = "해당 리소스에 접근하기 위한 권한이 없습니다.";

    public CAccessDeniedException() {
        super(DEFAULT_MESSAGE);
    }

    public CAccessDeniedException(String message) {
        super(message);
    }

    public CAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
