package com.inventory.management.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.MethodArgumentNotValidException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex) {

        ErrorResponse error = new ErrorResponse(
                false,ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPassword(
            InvalidPasswordException ex) {

        ErrorResponse error = new ErrorResponse(
                false,ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(
            ProductNotFoundException ex) {

        ErrorResponse error = new ErrorResponse(
                false,ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(
            DuplicateResourceException ex) {

        ErrorResponse error = new ErrorResponse(
                false,ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleStock(
            InsufficientStockException ex) {

        ErrorResponse error = new ErrorResponse(
                false,ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProcurementNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProcurement(
            ProcurementNotFoundException ex) {

        ErrorResponse error = new ErrorResponse(
                false,ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SupplierNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSupplier(
            SupplierNotFoundException ex) {

        ErrorResponse error = new ErrorResponse(
                false,ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()));

        Map<String, Object> response = new HashMap<>();

        response.put("success", false);

        response.put("message", "Validation failed");

        response.put("status", HttpStatus.BAD_REQUEST.value());

        response.put("errors", errors);

        response.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex) {

        ErrorResponse error = new ErrorResponse(
                false,ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now());

        return new ResponseEntity<>(error,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}