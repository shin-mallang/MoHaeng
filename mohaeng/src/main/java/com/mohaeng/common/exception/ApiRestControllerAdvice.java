package com.mohaeng.common.exception;

import com.mohaeng.authentication.application.exception.IncorrectAuthenticationException;
import com.mohaeng.authentication.domain.exception.NotFoundAccessTokenException;
import com.mohaeng.authentication.infrastructure.jwt.service.exception.InvalidAccessTokenException;
import com.mohaeng.member.application.exception.DuplicateUsernameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ApiRestControllerAdvice {

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 400
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ErrorResponseDto handleException(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        String fields = result.getFieldErrors()
                .stream()
                .map(FieldError::getField)
                .collect(joining(", "));
        log.error("필드들이 입력되지 않았습니다. [%s]".formatted(fields));
        return new ErrorResponseDto(BAD_REQUEST.name(), "입력되지 않은 필드가 있습니다. [%s]".formatted(fields));
    }

    /**
     * 400
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ErrorResponseDto handleException(HttpMessageNotReadableException e) {
        log.error(e.getMessage());
        return new ErrorResponseDto(BAD_REQUEST.name(), "ENUM 매핑 시 오류 발생");
    }

    /**
     * 401
     */
    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(IncorrectAuthenticationException.class)
    ErrorResponseDto handleException(IncorrectAuthenticationException e) {
        log.error(e.getMessage());
        return new ErrorResponseDto(UNAUTHORIZED.name(), e.getMessage());
    }

    /**
     * 401
     */
    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(NotFoundAccessTokenException.class)
    ErrorResponseDto handleException(NotFoundAccessTokenException e) {
        log.error(e.getMessage());
        return new ErrorResponseDto(UNAUTHORIZED.name(), e.getMessage());
    }

    /**
     * 401
     */
    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(InvalidAccessTokenException.class)
    ErrorResponseDto handleException(InvalidAccessTokenException e) {
        log.error(e.getMessage());
        return new ErrorResponseDto(UNAUTHORIZED.name(), e.getMessage());
    }

    /**
     * 409
     */
    @ResponseStatus(CONFLICT)
    @ExceptionHandler(DuplicateUsernameException.class)
    ErrorResponseDto handleException(DuplicateUsernameException e) {
        log.error(e.getMessage());
        return new ErrorResponseDto(CONFLICT.name(), e.getMessage());
    }
}
