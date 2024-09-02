package com.ticketgo.service;

import com.ticketgo.dto.request.BusCompanyRegistrationRequest;
import com.ticketgo.dto.request.LoginRequest;
import com.ticketgo.dto.request.UserRegistrationRequest;
import com.ticketgo.dto.response.LoginResponse;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {

    Integer registerNewUser(UserRegistrationRequest request);
    Integer registerNewBusCompany(BusCompanyRegistrationRequest request);
    void generateAndSendVerificationToken(String email);
    void verifyEmail(String token);
    LoginResponse authenticate(LoginRequest request);
}
