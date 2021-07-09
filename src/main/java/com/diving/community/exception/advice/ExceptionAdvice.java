package com.diving.community.exception.advice;

import com.diving.community.exception.BadRequestException;
import com.diving.community.exception.NoPermissionsException;
import com.diving.community.exception.ResourceNotFoundException;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse badRequest(BadRequestException e) {
        return ExceptionResponse.builder()
                .code(-1000)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse resourceNotFound(ResourceNotFoundException e) {
        return ExceptionResponse.builder()
                .code(-1001)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(NoPermissionsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionResponse noPermissionException(NoPermissionsException e) {
        return ExceptionResponse.builder()
                .code(-1002)
                .message(e.getMessage())
                .build();
    }

    @Data
    @Builder
    public static class ExceptionResponse {
        private int code;
        private String message;
    }
}
