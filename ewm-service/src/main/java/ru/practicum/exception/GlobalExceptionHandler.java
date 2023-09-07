package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 403
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleObjectNotFoundException(final CannotBeModifiedException e) {
        log.info(e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.FORBIDDEN)
                .reason(HttpStatus.FORBIDDEN.name())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 404
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleObjectNotFoundException(final NotFoundException e) {
        log.info(e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .reason(HttpStatus.NOT_FOUND.name())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 400
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleRequestNotValidException(final NotValidException e) {
        log.info(e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason(HttpStatus.BAD_REQUEST.name())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 409
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleRequestConflictException(final ConflictException e) {
        log.info(e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .reason(HttpStatus.CONFLICT.name())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

}
