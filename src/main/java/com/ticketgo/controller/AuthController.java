package com.ticketgo.controller;

import com.ticketgo.dto.request.BusCompanyRegistrationRequest;
import com.ticketgo.dto.request.LoginRequest;
import com.ticketgo.dto.request.UserRegistrationRequest;
import com.ticketgo.dto.response.ApiResponse;
import com.ticketgo.dto.response.LoginResponse;
import com.ticketgo.dto.response.RegistrationResponse;
import com.ticketgo.service.AuthService;
import com.ticketgo.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/user")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        Integer userId = authService.registerNewUser(request);
        RegistrationResponse response = new RegistrationResponse(userId, null);
        return ApiResponseUtil.createSuccessResponse(response, "User registered successfully.", HttpStatus.CREATED);
    }

    @PostMapping("/register/bus-company")
    public ResponseEntity<ApiResponse> registerBusCompany(@Valid @RequestBody BusCompanyRegistrationRequest request) {
        Integer busCompanyId = authService.registerNewBusCompany(request);
        RegistrationResponse response =  new RegistrationResponse(null, busCompanyId);
        return ApiResponseUtil.createSuccessResponse(response, "Bus company registered successfully.", HttpStatus.CREATED);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ApiResponseUtil.createSuccessResponse(null, "Email verified successfully.", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.authenticate(request);
        return ApiResponseUtil.createSuccessResponse(loginResponse,"Login successful", HttpStatus.OK);
    }
}
