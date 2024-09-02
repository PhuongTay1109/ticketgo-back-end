package com.ticketgo.service.impl;

import com.ticketgo.exception.AppException;
import com.ticketgo.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;

    @Async
    @Override
    public CompletableFuture<Void> sendVerificationEmail(String to, String token) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom("contact@ticketgo.com", "Ticket Go Support");
            helper.setTo(to);

            String subject = "Please confirm your account";
            String link = "http://localhost:3000/verify-email?token=" + token;

            String content = "<p>Hello,</p>" +
                    "<p>Thank you for registering with Ticket Go.</p>"
                    + "<p>Please click the link below to confirm your account:</p>" +
                    "<p><a href=\"" + link + "\">Confirm my account</a></p>" + "<br>"
                    + "<p>If you did not register for this account, please ignore this email.</p>";

            helper.setSubject(subject);
            helper.setText(content, true);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new AppException("Email", "Failed to send email", "Server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        javaMailSender.send(message);
        return CompletableFuture.completedFuture(null);

    }
}
