package com.ticketgo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "PasswordResetTokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    public PasswordResetToken() {
    }

    public PasswordResetToken(String token, Account account) {
        this.token = token;
        this.createdAt = LocalDateTime.now();
        // Set expiration to 1 hour for password reset tokens
        this.expiresAt = this.createdAt.plusHours(1);
        this.account = account;
    }
}

