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

    RefreshTokenResponse refreshToken(String token);

    LoginResponse login(LoginRequest request);
    LoginResponse googleLogin(String accessToken);
    LoginResponse facebookLogin(String accessToken);
}
