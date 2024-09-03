package com.ticketgo.controller;

import com.ticketgo.dto.request.BusCompanyRegistrationRequest;
import com.ticketgo.dto.request.LoginRequest;
import com.ticketgo.dto.request.CustomerRegistrationRequest;
import com.ticketgo.dto.request.RefreshTokenRequest;
import com.ticketgo.dto.response.ApiResponse;
import com.ticketgo.dto.response.LoginResponse;
import com.ticketgo.dto.response.RefreshTokenResponse;
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

    @PostMapping("/register/customer")
    public ResponseEntity<ApiResponse> registerNewCustomer(@Valid @RequestBody CustomerRegistrationRequest request) {
        Integer userId = authService.registerNewCustomer(request);
        RegistrationResponse response = new RegistrationResponse(userId, null);
        return ApiResponseUtil.createSuccessResponse(response, "Customer registered successfully.", HttpStatus.CREATED);
    }

    @PostMapping("/register/bus-company")
    public ResponseEntity<ApiResponse> registerNewBusCompany(@Valid @RequestBody BusCompanyRegistrationRequest request) {
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
        return ApiResponseUtil.createSuccessResponse(loginResponse,"Login successfully", HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = authService.refreshToken(request.getRefreshToken());
        return ApiResponseUtil.createSuccessResponse(response,"Refresh token successfully", HttpStatus.OK);
    }
}
