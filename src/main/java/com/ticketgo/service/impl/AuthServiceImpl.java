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
                .orElseThrow(() -> new AppException("Invalid username or password", HttpStatus.UNAUTHORIZED));

        if(!passwordEncoder.matches(password, account.getPassword())) {
            throw new AppException("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }

        if(!account.isEnabled()) {
            throw new AppException( "Your account haven't activated", HttpStatus.UNAUTHORIZED);
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
                .orElseThrow(() -> new AppException("Account not found", HttpStatus.NOT_FOUND));

        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = new EmailVerificationToken(token, account);

        emailVerificationTokenRepository.save(verificationToken);
        emailService.sendVerificationEmail(email, token);
    }

    @Override
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new AppException("Token not found", HttpStatus.NOT_FOUND));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            // Token expired, create and send a new token
            Account account = verificationToken.getAccount();
            String newToken = UUID.randomUUID().toString();
            EmailVerificationToken newVerificationToken = new EmailVerificationToken(newToken, account);

            emailVerificationTokenRepository.save(newVerificationToken);
            emailService.sendVerificationEmail(account.getEmail(), newToken);
            emailVerificationTokenRepository.delete(verificationToken);

            throw new AppException("Token expired. A new verification email has been sent.", HttpStatus.GONE);
        }

        // Token is valid, enable the account
        Account account = verificationToken.getAccount();
        account.setEnabled(true);
        accountRepository.save(account);

        emailVerificationTokenRepository.delete(verificationToken);
    }

    private void validateUserRequest(UserRegistrationRequest request) {
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email already exists", HttpStatus.BAD_REQUEST);
        }

        if (accountRepository.existsByPhone(request.getPhone())) {
            throw new AppException("Phone number already exists", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByIdentityNo(request.getIdentityNo())) {
            throw new AppException("Identity number already exists", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateCompanyRequest(BusCompanyRegistrationRequest request) {
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email already exists", HttpStatus.BAD_REQUEST);
        }

        if (accountRepository.existsByPhone(request.getPhone())) {
            throw new AppException("Phone number already exists", HttpStatus.BAD_REQUEST);
        }

        if (busCompanyRepository.existsByBusinessLicense(request.getBusinessLicense())) {
            throw new AppException("Business license already exists", HttpStatus.BAD_REQUEST);
        }
    }

    private Role getRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    private Date convertToDate(String dateString) {
        return Date.valueOf(dateString);
    }
}
