package com.ticketgo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "email_verification_tokens")
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    // Constructors, getters, and setters
    public EmailVerificationToken() {
    }

    public EmailVerificationToken(String token, Account account) {
        this.token = token;
        this.createdAt = LocalDateTime.now();
        // Automatically set the expiration time to 1 day from the creation time
        this.expiresAt = this.createdAt.plusDays(1);
        this.account = account;
    }
}

