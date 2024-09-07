package com.ticketgo.service.impl;

import com.ticketgo.contant.PredefinedRole;
import com.ticketgo.dto.request.BusCompanyRegistrationRequest;
import com.ticketgo.dto.request.LoginRequest;
import com.ticketgo.dto.request.CustomerRegistrationRequest;
import com.ticketgo.dto.response.GoogleUserInfoResponse;
import com.ticketgo.dto.response.FacebookUserInfoResponse;
import com.ticketgo.dto.response.LoginResponse;
import com.ticketgo.dto.response.RefreshTokenResponse;
import com.ticketgo.exception.AppException;
import com.ticketgo.model.*;
import com.ticketgo.repository.*;
import com.ticketgo.service.AccountService;
import com.ticketgo.service.AuthService;
import com.ticketgo.service.EmailService;
import com.ticketgo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final BusCompanyRepository busCompanyRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerficationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final AccountService accountService;

    @Override
    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Tên đăng nhập hoặc mật khẩu không hợp lệ", HttpStatus.UNAUTHORIZED));

        if(!passwordEncoder.matches(password, account.getPassword())) {
            throw new AppException("Tên đăng nhập hoặc mật khẩu không hợp lệ", HttpStatus.UNAUTHORIZED);
        }

        if(!account.isEnabled()) {
            throw new AppException("Tài khoản của bạn chưa được kích hoạt. Hãy kiểm tra email để kích hoạt tài khoản.", HttpStatus.UNAUTHORIZED);
        }

        return new LoginResponse(jwtUtil.generateAccessToken(account), jwtUtil.generateRefreshToken(account));
    }

    @Override
    public LoginResponse googleLogin(String accessToken) {
        final String googleUserInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<GoogleUserInfoResponse> response = restTemplate.exchange(googleUserInfoEndpoint, HttpMethod.GET,
                httpEntity, GoogleUserInfoResponse.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new AppException("Token Google không hợp lệ", HttpStatus.UNAUTHORIZED);
        }

        GoogleUserInfoResponse userResponse = response.getBody();

        if (accountRepository.existsByEmail(userResponse.getEmail())) {
            Account account = accountRepository.findByEmail(userResponse.getEmail())
                    .orElseThrow(() -> new AppException("Không tìm thấy tài khoản", HttpStatus.NOT_FOUND));

            if (!account.isEnabled()) {
                throw new AppException("Tài khoản của bạn chưa được kích hoạt", HttpStatus.UNAUTHORIZED);
            }

            return new LoginResponse(jwtUtil.generateAccessToken(account), jwtUtil.generateRefreshToken(account));
        } else {
            Account account = createGoogleCustomerAccount(userResponse);
            createCustomerForSocialLogin(account);

            return new LoginResponse(jwtUtil.generateAccessToken(account), jwtUtil.generateRefreshToken(account));
        }
    }

    @Override
    public LoginResponse facebookLogin(String accessToken) {
        final String facebookUserInfoEndpoint = "https://graph.facebook.com/me?fields=email,name";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<FacebookUserInfoResponse> response = restTemplate.exchange(facebookUserInfoEndpoint, HttpMethod.GET,
                httpEntity, FacebookUserInfoResponse.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new AppException("Token Facebook không hợp lệ", HttpStatus.UNAUTHORIZED);
        }

        FacebookUserInfoResponse userResponse = response.getBody();

        if (accountRepository.existsByEmail(userResponse.getEmail())) {
            Account account = accountRepository.findByEmail(userResponse.getEmail())
                    .orElseThrow(() -> new AppException("Không tìm thấy tài khoản", HttpStatus.NOT_FOUND));

            if (!account.isEnabled()) {
                account.setEnabled(true);
            }

            return new LoginResponse(jwtUtil.generateAccessToken(account), jwtUtil.generateRefreshToken(account));
        } else {
            Account account = createFacebookCustomerAccount(userResponse);
            createCustomerForSocialLogin(account);

            return new LoginResponse(jwtUtil.generateAccessToken(account), jwtUtil.generateRefreshToken(account));
        }
    }

    private Account createFacebookCustomerAccount(FacebookUserInfoResponse userResponse) {
        Role role = getRoleByName(PredefinedRole.CUSTOMER_ROLE);
        Account account = Account.builder()
                .email(userResponse.getEmail())
                .password("")
                .role(role)
                .enabled(true)
                .fullName(userResponse.getLastName() + userResponse.getLastName())
                .picture(userResponse.getPictureUrl())
                .build();
        return accountRepository.save(account);
    }


    private Account createGoogleCustomerAccount(GoogleUserInfoResponse userResponse) {
        Role role = getRoleByName(PredefinedRole.CUSTOMER_ROLE);
        Account account = Account.builder()
                .email(userResponse.getEmail())
                .password("")
                .role(role)
                .enabled(true)
                .fullName(userResponse.getName())
                .picture(userResponse.getPicture())
                .build();
        return accountRepository.save(account);
    }

    private void createCustomerForSocialLogin(Account account) {
        Customer customer = Customer.builder()
                .account(account)
                .address("")
                .build();
        customerRepository.save(customer);
    }

    @Override
    public RefreshTokenResponse refreshToken(String token) {
        if (!jwtUtil.isTokenValid(token)) {
            throw new AppException("Phiên đăng nhập đã hết hạn", HttpStatus.UNAUTHORIZED);
        }

        String username = jwtUtil.extractUsername(token);
        UserDetails userDetails = accountService.loadUserByUsername(username);
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);

        return new RefreshTokenResponse(newAccessToken);
    }

    @Override
    public Integer registerNewCustomer(CustomerRegistrationRequest request) {
        validateUserRequest(request);
        Account account = createAccount(request);
        Customer customer = createCustomer(request, account);

        generateAndSendVerificationToken(account.getEmail());

        return customer.getId();
    }

    @Override
    public Integer registerNewBusCompany(BusCompanyRegistrationRequest request) {
        validateCompanyRequest(request);
        Account account = createAccount(request);
        BusCompany busCompany = createBusCompany(request, account);

        generateAndSendVerificationToken(account.getEmail());

        return busCompany.getId();
    }

    private Account createAccount(CustomerRegistrationRequest request) {
        Role role = getRoleByName(PredefinedRole.CUSTOMER_ROLE);
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

    private Customer createCustomer(CustomerRegistrationRequest request, Account account) {
        Customer customer = Customer.builder()
                .account(account)
                .address(request.getAddress())
                .dateOfBirth(convertToDate(request.getDateOfBirth()))
                .build();
        return customerRepository.save(customer);
    }

    private Account createAccount(BusCompanyRegistrationRequest request) {
        Role role = getRoleByName(PredefinedRole.BUS_COMPANY_ROLE);
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
                .orElseThrow(() -> new AppException("Không tìm thấy tài khoản", HttpStatus.NOT_FOUND));

        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = new EmailVerificationToken(token, account);

        emailVerificationTokenRepository.save(verificationToken);
        emailService.sendVerificationEmail(email, token);
    }

    @Override
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new AppException("Link xác nhận không hợp lệ", HttpStatus.NOT_FOUND));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            // Token đã hết hạn, tạo và gửi một token mới
            Account account = verificationToken.getAccount();
            String newToken = UUID.randomUUID().toString();
            EmailVerificationToken newVerificationToken = new EmailVerificationToken(newToken, account);

            emailVerificationTokenRepository.save(newVerificationToken);
            emailService.sendVerificationEmail(account.getEmail(), newToken);
            emailVerificationTokenRepository.delete(verificationToken);

            throw new AppException("Link đã hết hạn. Một email xác nhận mới đã được gửi.", HttpStatus.GONE);
        }

        // Token hợp lệ, kích hoạt tài khoản
        Account account = verificationToken.getAccount();
        account.setEnabled(true);
        accountRepository.save(account);

        emailVerificationTokenRepository.delete(verificationToken);
    }

    private void validateUserRequest(CustomerRegistrationRequest request) {
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email đã tồn tại", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateCompanyRequest(BusCompanyRegistrationRequest request) {
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email đã tồn tại", HttpStatus.BAD_REQUEST);
        }

        if (busCompanyRepository.existsByBusinessLicense(request.getBusinessLicense())) {
            throw new AppException("Giấy phép kinh doanh đã tồn tại", HttpStatus.BAD_REQUEST);
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
