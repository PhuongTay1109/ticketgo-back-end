package com.ticketgo.config;

import java.util.Properties;
import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
@EnableAsync
public class MailConfig {

    // get properties from application.yml
    private final Environment env;

    @Bean
    JavaMailSender javaMailSender() {

        JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
        javaMailSenderImpl.setHost("smtp.gmail.com");
        javaMailSenderImpl.setPort(587);
        javaMailSenderImpl.setUsername(env.getProperty("email.username"));
        javaMailSenderImpl.setPassword(env.getProperty("email.password"));

        Properties props = javaMailSenderImpl.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return javaMailSenderImpl;
    }

    // Config asynchronous
    @Bean
    Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); //  executor will maintain at least 2 threads in the pool, even if they are idle.
        executor.setMaxPoolSize(2); // This means the pool will not grow beyond 2 threads, even under heavy load.
        executor.setQueueCapacity(500); // This means that up to 500 tasks can be queued before new tasks are rejected when all core threads are busy.
        executor.setThreadNamePrefix("email-");
        executor.initialize();
        return executor;
    }
}
