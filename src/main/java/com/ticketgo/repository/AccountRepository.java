package com.ticketgo.repository;

import com.ticketgo.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    Optional<Account> findByEmail(String email);
}
