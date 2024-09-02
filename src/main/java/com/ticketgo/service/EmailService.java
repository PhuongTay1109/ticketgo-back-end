package com.ticketgo.service;

import java.util.concurrent.CompletableFuture;

public interface EmailService {
    CompletableFuture<Void> sendVerificationEmail(String to, String token);
}
