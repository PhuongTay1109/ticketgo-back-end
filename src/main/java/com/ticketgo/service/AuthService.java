package com.ticketgo.service;

import com.ticketgo.dto.request.BusCompanyRegistrationRequest;
import com.ticketgo.dto.request.LoginRequest;
import com.ticketgo.dto.request.CustomerRegistrationRequest;
import com.ticketgo.dto.response.LoginResponse;
import com.ticketgo.dto.response.RefreshTokenResponse;

public interface AuthService {

    Integer registerNewCustomer(CustomerRegistrationRequest request);
    Integer registerNewBusCompany(BusCompanyRegistrationRequest request);
    void generateAndSendVerificationToken(String email);
    void verifyEmail(String token);
    LoginResponse authenticate(LoginRequest request);
    RefreshTokenResponse refreshToken(String token);
}
