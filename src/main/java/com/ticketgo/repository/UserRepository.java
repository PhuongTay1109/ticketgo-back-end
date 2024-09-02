package com.ticketgo.repository;

import com.ticketgo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByIdentityNo(String identityNo);
}
