package com.ticketgo.util;

import com.ticketgo.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class ApiResponseUtil {

    public static ResponseEntity<ApiResponse> createSuccessResponse(Object data, String message, HttpStatus status) {
        ApiResponse apiResponse = ApiResponse.builder()
                .status(status.value())
                .message(message)
                .data(data)
                .errors(null)
                .build();
        return new ResponseEntity<>(apiResponse, status);
    }

    public static ResponseEntity<ApiResponse> createErrorResponse(String message, Map<String, String> errors, HttpStatus status) {
        ApiResponse apiResponse = ApiResponse.builder()
                .status(status.value())
                .message(message)
                .data(null)
                .errors(errors)
                .build();
        return new ResponseEntity<>(apiResponse, status);
    }
}
