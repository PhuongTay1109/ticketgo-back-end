package com.ticketgo.repository;

import com.ticketgo.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    boolean existsByIdentityNo(String identityNo);
}
