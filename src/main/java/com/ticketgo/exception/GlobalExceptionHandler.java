package com.ticketgo.exception;

import com.ticketgo.dto.response.ApiResponse;
import com.ticketgo.util.ApiResponseUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ApiResponseUtil.createErrorResponse("Validation failed.", errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ ConstraintViolationException.class, MissingServletRequestParameterException.class })
    public ResponseEntity<ApiResponse> handleValidationException(Exception e) {
        Map<String, String> errors = new HashMap<>();
        String message = "";

        if (e instanceof ConstraintViolationException) {
            // Handle ConstraintViolationException
            ConstraintViolationException ex = (ConstraintViolationException) e;
            Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
            for (ConstraintViolation<?> violation : violations) {
                String propertyPath = violation.getPropertyPath().toString();
                String errorMessage = violation.getMessage();
                errors.put(propertyPath, errorMessage);
            }
            message = "Constraint violation occurred.";
        } else if (e instanceof MissingServletRequestParameterException) {
            MissingServletRequestParameterException ex = (MissingServletRequestParameterException) e;
            String parameterName = ex.getParameterName();
            errors.put(parameterName, "Missing request parameter");
            message = "Missing request parameter.";
        }

        return ApiResponseUtil.createErrorResponse(message, errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse> handleUserRegistrationException(AppException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getField(), ex.getMessage());
        return ApiResponseUtil.createErrorResponse(ex.getErrorMessage(), errors, ex.getStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException ex) {
        return ApiResponseUtil.createErrorResponse(ex.getMessage(), null, HttpStatus.BAD_REQUEST);
    }
}
