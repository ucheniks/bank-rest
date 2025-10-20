package com.gshelgaas.bankcards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandler {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        return buildApiError(
                e.getMessage(),
                "The required object was not found.",
                HttpStatus.NOT_FOUND,
                Collections.singletonList(e.getMessage())
        );
    }

    @ExceptionHandler({ConflictException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConflictException e) {
        return buildApiError(
                e.getMessage(),
                "For the requested operation the conditions are not met.",
                HttpStatus.CONFLICT,
                Collections.singletonList(e.getMessage())
        );
    }

    @ExceptionHandler({UnauthorizedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiError handleUnauthorizedException(final UnauthorizedException e) {
        return buildApiError(
                e.getMessage(),
                "Authentication required.",
                HttpStatus.UNAUTHORIZED,
                Collections.singletonList(e.getMessage())
        );
    }

    @ExceptionHandler({ForbiddenException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbiddenException(final ForbiddenException e) {
        return buildApiError(
                e.getMessage(),
                "Forbidden.",
                HttpStatus.FORBIDDEN,
                Collections.singletonList(e.getMessage())
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiError handleBadCredentialsException(final BadCredentialsException e) {
        return buildApiError(
                "Invalid email or password",
                "Incorrect credentials.",
                HttpStatus.UNAUTHORIZED,
                Collections.singletonList(e.getMessage())
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleAccessDeniedException(final AccessDeniedException e) {
        return buildApiError(
                "Access denied",
                "Forbidden.",
                HttpStatus.FORBIDDEN,
                Collections.singletonList(e.getMessage())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        return buildApiError(
                "Validation failed",
                "Incorrectly made request.",
                HttpStatus.BAD_REQUEST,
                errors
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingParams(MissingServletRequestParameterException e) {
        return buildApiError(
                "Required parameter '" + e.getParameterName() + "' is missing",
                "Incorrectly made request",
                HttpStatus.BAD_REQUEST,
                Collections.singletonList(e.getMessage())
        );
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingHeader(final MissingRequestHeaderException e) {
        return buildApiError(
                "Missing header: " + e.getHeaderName(),
                "Incorrectly made request.",
                HttpStatus.BAD_REQUEST,
                Collections.singletonList("Missing required header: " + e.getHeaderName())
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleHttpMessageNotReadable(final HttpMessageNotReadableException e) {
        return buildApiError(
                "JSON parse error",
                "Incorrectly made request.",
                HttpStatus.BAD_REQUEST,
                Collections.singletonList(e.getMessage())
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgument(final IllegalArgumentException e) {
        return buildApiError(
                e.getMessage(),
                "Incorrectly made request.",
                HttpStatus.BAD_REQUEST,
                Collections.singletonList(e.getMessage())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleErrors(final Throwable e) {
        return buildApiError(
                "Internal server error",
                "An unexpected error occurred.",
                HttpStatus.INTERNAL_SERVER_ERROR,
                Collections.singletonList(e.getMessage())
        );
    }

    private ApiError buildApiError(String message, String reason, HttpStatus status, List<String> errors) {
        return ApiError.builder()
                .message(message)
                .reason(reason)
                .status(status.name())
                .timestamp(LocalDateTime.now().format(TIMESTAMP_FORMATTER))
                .errors(errors)
                .build();
    }
}