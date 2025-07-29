package com.example.bankcards.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        log.error("Not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder(ex, HttpStatus.NOT_FOUND, ex.getMessage()).build());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        log.error("Unauthorized: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.builder(ex, HttpStatus.UNAUTHORIZED, ex.getMessage()).build());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex) {
        log.error("Forbidden: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.builder(ex, HttpStatus.FORBIDDEN, ex.getMessage()).build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.builder(ex, HttpStatus.FORBIDDEN, "Access denied").build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("Validation Error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, ex.getMessage()).build());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex) {
        log.error("Missing request parameter: {}", ex.getParameterName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, "Missing parameter: "
                        + ex.getParameterName()).build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(HttpMessageNotReadableException ex) {
        log.error("Malformed JSON request: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, "Malformed JSON request").build());
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerErrorException(InternalServerErrorException ex) {
        log.error("Internal Server Error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder(ex, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()).build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder(ex, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()).build());
    }
}
