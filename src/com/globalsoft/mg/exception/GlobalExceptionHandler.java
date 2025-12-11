package com.jobportal.exception;

import com.jobportal.dto.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Resource Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        ApiResponse<String> response = ApiResponse.error(ex.getMessage(), 404);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Duplicate Resource
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<String>> handleDuplicateResource(DuplicateResourceException ex, WebRequest request) {
        ApiResponse<String> response = ApiResponse.error(ex.getMessage(), 409);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // Validation Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String, String>> response = ApiResponse.error("Validation failed", 400, errors);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Business rule violation
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<String>> handleBusinessException(BusinessException ex, WebRequest request) {
        ApiResponse<String> response = ApiResponse.error(ex.getMessage(), 400);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Custom Access Denied
    @ExceptionHandler(com.jobportal.exception.AccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleCustomAccessDenied(com.jobportal.exception.AccessDeniedException ex, WebRequest request) {
        ApiResponse<String> response = ApiResponse.error(ex.getMessage(), 403);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // Spring Security Access Denied
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        ApiResponse<String> response = ApiResponse.error("Access denied: " + ex.getMessage(), 403);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // Authentication & Security Exceptions
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<String>> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        ApiResponse<String> response = ApiResponse.error(ex.getMessage(), 401);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> handleBadCredentials(BadCredentialsException ex, WebRequest request) {
        ApiResponse<String> response = ApiResponse.error("Invalid username or password", 401);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse<String>> handleDisabledException(DisabledException ex, WebRequest request) {
        ApiResponse<String> response = ApiResponse.error("Account is disabled", 401);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiResponse<String>> handleLockedException(LockedException ex, WebRequest request) {
        ApiResponse<String> response = ApiResponse.error("Account is locked", 401);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // Validation Exception
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<String>> handleValidationException(ValidationException ex, WebRequest request) {
        ApiResponse<String> response = ApiResponse.error(ex.getMessage(), 400);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Invalid Operation
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidOperation(InvalidOperationException ex, WebRequest request) {
        ApiResponse<String> response = ApiResponse.error(ex.getMessage(), 400);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // File Storage Exceptions
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ApiResponse<String>> handleFileStorageException(FileStorageException ex, WebRequest request) {
        ApiResponse<String> response = ApiResponse.error("File storage error: " + ex.getMessage(), 500);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<String>> handleMaxSizeException(MaxUploadSizeExceededException ex, WebRequest request) {
        ApiResponse<String> response = ApiResponse.error("File size exceeds maximum limit", 400);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileAccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleFileAccessDenied(FileAccessDeniedException ex, WebRequest request) {
        ApiResponse<String> response = ApiResponse.error("File access denied: " + ex.getMessage(), 403);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // Payment Processing Exception
    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<ApiResponse<String>> handlePaymentProcessing(PaymentProcessingException ex, WebRequest request) {
        ApiResponse<String> response = ApiResponse.error("Payment processing error: " + ex.getMessage(), 400);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // JPA Entity Not Found
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {
        ApiResponse<String> response = ApiResponse.error("Requested resource not found", 404);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        ApiResponse<String> response = ApiResponse.error("Invalid argument: " + ex.getMessage(), 400);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Generic Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex, WebRequest request) {
        // Log the exception for debugging
        ex.printStackTrace();

        ApiResponse<String> response = ApiResponse.error("Internal Server Error: " + ex.getMessage(), 500);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle all RuntimeException (catch-all for runtime exceptions)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        ApiResponse<String> response = ApiResponse.error("Runtime error: " + ex.getMessage(), 500);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}