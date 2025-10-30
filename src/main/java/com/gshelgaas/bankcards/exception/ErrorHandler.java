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

/**
 * Глобальный обработчик исключений для REST API.
 * Перехватывает исключения со всех контроллеров и преобразует их в стандартизированные JSON ответы.
 * Обеспечивает единообразную обработку ошибок во всем приложении.
 *
 * @author Георгий Шельгаас
 */
@RestControllerAdvice
public class ErrorHandler {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Обрабатывает исключения NotFoundException.
     * Возникает когда запрашиваемый ресурс не найден в системе.
     */
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

    /**
     * Обрабатывает исключения ConflictException.
     * Возникает при нарушении бизнес-правил или конфликте данных.
     */
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

    /**
     * Обрабатывает исключения UnauthorizedException.
     * Возникает когда пользователь не аутентифицирован.
     */
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

    /**
     * Обрабатывает исключения ForbiddenException.
     * Возникает когда пользователь аутентифицирован, но не имеет необходимых прав.
     */
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

    /**
     * Обрабатывает исключения BadCredentialsException от Spring Security.
     * Возникает при неверных учетных данных при аутентификации.
     */
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

    /**
     * Обрабатывает исключения AccessDeniedException от Spring Security.
     * Возникает когда аутентифицированный пользователь пытается получить доступ к запрещенному ресурсу.
     */
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

    /**
     * Обрабатывает ошибки валидации @Valid аннотаций.
     * Возникает при некорректных данных в DTO при валидации.
     */
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

    /**
     * Обрабатывает отсутствующие обязательные параметры запроса.
     * Возникает когда обязательный query parameter отсутствует в запросе.
     */
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

    /**
     * Обрабатывает отсутствующие обязательные заголовки запроса.
     * Возникает когда обязательный header отсутствует в запросе.
     */
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

    /**
     * Обрабатывает некорректный JSON в теле запроса.
     * Возникает при синтаксических ошибках в JSON или несоответствии типам данных.
     */
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

    /**
     * Обрабатывает некорректные аргументы методов.
     * Возникает при передаче недопустимых значений в параметры методов.
     */
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

    /**
     * Обрабатывает все неперехваченные исключения.
     * Является обработчиком для любых непредвиденных ошибок.
     */
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

    /**
     * Создает стандартизированный объект ApiError для ответа.
     */
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