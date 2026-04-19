package com.codelens.backend.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
// import org.springframework.*;
import com.codelens.backend.dto.ErrorResponse;

// import org.springframework.web.bind.MethodArgumentNotValidException; // Correct Import
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex){
        ErrorResponse error = ErrorResponse.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message(ex.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    // / Handle cases where something is not found (Customizing for later)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("An unexpected error occurred: " + ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse>handleValidationException(org.springframework.web.bind.MethodArgumentNotValidException ex){
            String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
            ErrorResponse error = ErrorResponse.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message(errorMessage)
                                .timestamp(LocalDateTime.now())
                                .build();
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);                        
    }

    // 1. Handle "Bad JSON" (e.g., a user sends a string where a number should be, or broken syntax)
@ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(org.springframework.http.converter.HttpMessageNotReadableException ex) {
    ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message("Malformed JSON request. Check your syntax!")
            .timestamp(LocalDateTime.now())
            .build();
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
}

// 2. Handle "Method Not Allowed" (e.g., user tries to POST to a GET endpoint)
@ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
public ResponseEntity<ErrorResponse> handleMethodNotSupported(org.springframework.web.HttpRequestMethodNotSupportedException ex) {
    ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.METHOD_NOT_ALLOWED.value())
            .message("This endpoint does not support " + ex.getMethod())
            .timestamp(LocalDateTime.now())
            .build();
    return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
}

@ExceptionHandler(ProjectNotFoundException.class)
public ResponseEntity<ErrorResponse> handleProjectNotFound(ProjectNotFoundException ex) {
    ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.NOT_FOUND.value())
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
}
}
