package com.ticketgo.service.impl;

import com.ticketgo.contant.PredenfinedRole;
import com.ticketgo.dto.request.BusCompanyRegistrationRequest;
import com.ticketgo.dto.request.LoginRequest;
import com.ticketgo.dto.request.UserRegistrationRequest;
import com.ticketgo.dto.response.LoginResponse;
import com.ticketgo.exception.AppException;
import com.ticketgo.model.*;
import com.ticketgo.repository.*;
import com.ticketgo.service.AuthService;
import com.ticketgo.service.EmailService;
import com.ticketgo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final BusCompanyRepository busCompanyRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerficationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse authenticate(LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("account", "Invalid username or password", "Login failed", HttpStatus.UNAUTHORIZED));

        if(!passwordEncoder.matches(password, account.getPassword())) {
            throw new AppException("account", "Invalid username or password", "Login failed", HttpStatus.UNAUTHORIZED);
        }

        if(!account.isEnabled()) {
            throw new AppException("account", "Your account haven't activated", "Login failed", HttpStatus.UNAUTHORIZED);
        }

        return new LoginResponse(jwtUtil.generateAccessToken(account), jwtUtil.generateRefreshToken(account));
    }

    @Override
    public Integer registerNewUser(UserRegistrationRequest request) {
        validateUserRequest(request);
        Account account = createAccount(request);
        User user = createUser(request, account);

        generateAndSendVerificationToken(account.getEmail());

        return user.getId();
    }

    @Override
    public Integer registerNewBusCompany(BusCompanyRegistrationRequest request) {
        validateCompanyRequest(request);
        Account account = createAccount(request);
        BusCompany busCompany = createBusCompany(request, account);

        generateAndSendVerificationToken(account.getEmail());

        return busCompany.getId();
    }

    private Account createAccount(UserRegistrationRequest request) {
        Role role = getRoleByName(PredenfinedRole.USER_ROLE);
        Account account = Account.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .phone(request.getPhone())
                .fullName(request.getFullName())
                .enabled(false)
                .build();
        return accountRepository.save(account);
    }

    private User createUser(UserRegistrationRequest request, Account account) {
        User user = User.builder()
                .account(account)
                .identityNo(request.getIdentityNo())
                .address(request.getAddress())
                .dateOfBirth(convertToDate(request.getDateOfBirth()))
                .build();
        return userRepository.save(user);
    }

    private Account createAccount(BusCompanyRegistrationRequest request) {
        Role role = getRoleByName(PredenfinedRole.BUS_COMPANY_ROLE);
        Account account = Account.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .enabled(false)
                .companyName(request.getCompanyName())
                .phone(request.getPhone())
                .build();
        return accountRepository.save(account);
    }

    private BusCompany createBusCompany(BusCompanyRegistrationRequest request, Account account) {
        BusCompany busCompany = BusCompany.builder()
                .account(account)
                .address(request.getAddress())
                .description(request.getDescription())
                .businessLicense(request.getBusinessLicense())
                .build();
        return busCompanyRepository.save(busCompany);
    }

    @Override
    public void generateAndSendVerificationToken(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("account", "Account not found", "Fail to send verification email", HttpStatus.NOT_FOUND));

        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = new EmailVerificationToken(token, account);

        emailVerificationTokenRepository.save(verificationToken);
        emailService.sendVerificationEmail(email, token);
    }

    @Override
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new AppException("token", "Token not found", "Fail to verify email", HttpStatus.NOT_FOUND));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            // Token expired, create and send a new token
            Account account = verificationToken.getAccount();
            String newToken = UUID.randomUUID().toString();
            EmailVerificationToken newVerificationToken = new EmailVerificationToken(newToken, account);

            emailVerificationTokenRepository.save(newVerificationToken);
            emailService.sendVerificationEmail(account.getEmail(), newToken);
            emailVerificationTokenRepository.delete(verificationToken);

            throw new AppException("token", "Token expired. A new verification email has been sent.", "Fail to verify email", HttpStatus.GONE);
        }

        // Token is valid, enable the account
        Account account = verificationToken.getAccount();
        account.setEnabled(true);
        accountRepository.save(account);

        emailVerificationTokenRepository.delete(verificationToken);
    }

    private void validateUserRequest(UserRegistrationRequest request) {
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw createAppException("email", "Email already exists");
        }

        if (accountRepository.existsByPhone(request.getPhone())) {
            throw createAppException("phone", "Phone number already exists");
        }

        if (userRepository.existsByIdentityNo(request.getIdentityNo())) {
            throw createAppException("identityNo", "Identity number already exists");
        }
    }

    private void validateCompanyRequest(BusCompanyRegistrationRequest request) {
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw createAppException("email", "Email already exists");
        }

        if (accountRepository.existsByPhone(request.getPhone())) {
            throw createAppException("phone", "Phone number already exists");
        }

        if (busCompanyRepository.existsByBusinessLicense(request.getBusinessLicense())) {
            throw createAppException("businessLicense", "Business license already exists");
        }
    }

    private Role getRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    private AppException createAppException(String field, String message) {
        return new AppException(field, message, "Registration failed", HttpStatus.BAD_REQUEST);
    }

    private Date convertToDate(String dateString) {
        return Date.valueOf(dateString);
    }
}
