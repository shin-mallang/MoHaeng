package com.mohaeng.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class ExceptionRestControllerAdvice {

    private static final String BAD_REQUEST_ERROR_CODE = "1000";

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
        return new ErrorResponseDto(BAD_REQUEST_ERROR_CODE, "입력되지 않은 필드가 있습니다. [%s]".formatted(fields));
    }

    /**
     * 400
     */
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ErrorResponseDto handleException(HttpMessageNotReadableException e) {
        log.error(e.getMessage());
        return new ErrorResponseDto(BAD_REQUEST_ERROR_CODE, "ENUM 매핑 시 오류 발생");
    }

    @ExceptionHandler(BaseException.class)
    ResponseEntity<ErrorResponseDto> handleException(BaseException e) {
        BaseExceptionType type = e.exceptionType();
        log.error("[ERROR] MESSAGE: {}, CAUSE: {}", type.errorMessage(), e.getCause());
        return new ResponseEntity<>(
                new ErrorResponseDto(String.valueOf(type.errorCode()), type.errorMessage()),
                type.httpStatus());
    }
}
