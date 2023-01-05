package com.mohaeng.common.exception;

import org.springframework.http.HttpStatus;

public record ErrorResponseDto(
        String code,
        String message
) {

    public static final ErrorResponseDto BAD_REQUEST = toErrorResponseDto(HttpStatus.BAD_REQUEST);
    public static final ErrorResponseDto UNAUTHORIZED = toErrorResponseDto(HttpStatus.UNAUTHORIZED);
    public static final ErrorResponseDto FORBIDDEN = toErrorResponseDto(HttpStatus.FORBIDDEN);
    public static final ErrorResponseDto NOT_FOUND = toErrorResponseDto(HttpStatus.NOT_FOUND);
    public static final ErrorResponseDto INTERNAL_SERVER_ERROR = toErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR);
    public static final ErrorResponseDto CONFLICT = toErrorResponseDto(HttpStatus.CONFLICT);

    private static ErrorResponseDto toErrorResponseDto(final HttpStatus httpStatus) {
        return new ErrorResponseDto(httpStatus.name(), httpStatus.getReasonPhrase());
    }
}