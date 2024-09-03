package com.ticketgo.service.impl;

import com.ticketgo.exception.AppException;
import com.ticketgo.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final JavaMailSender javaMailSender;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Async
    @Override
    public CompletableFuture<Void> sendVerificationEmail(String to, String token) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        try {
            helper.setFrom("contact@ticketgo.com", "Hỗ trợ Ticket Go");
            helper.setTo(to);

            String subject = "Vui lòng xác nhận tài khoản của bạn";
            String link = frontendUrl + "/verify-email?token=" + token;

            String content = "<p>Xin chào,</p>" +
                    "<p>Cảm ơn bạn đã đăng ký sử dụng dịch vụ của Ticket Go.</p>"
                    + "<p>Vui lòng nhấn vào liên kết bên dưới để xác nhận tài khoản của bạn:</p>"
                    + "<p><a href=\"" + link + "\">Xác nhận tài khoản của tôi</a></p>" + "<br>"
                    + "<p>Nếu bạn không thực hiện đăng ký này, vui lòng bỏ qua email này.</p>";

            helper.setSubject(subject);
            helper.setText(content, true);

        } catch (MessagingException | UnsupportedEncodingException e) {
            logger.error("Gửi email thất bại", e);
            throw new AppException("Không thể gửi email", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        javaMailSender.send(message);
        logger.info("Đã gửi email xác nhận đến {}", to);
        return CompletableFuture.completedFuture(null);
    }
}
