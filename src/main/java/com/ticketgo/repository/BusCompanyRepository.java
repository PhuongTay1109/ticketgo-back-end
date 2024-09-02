package com.ticketgo.repository;

import com.ticketgo.model.BusCompany;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusCompanyRepository extends JpaRepository<BusCompany, Integer> {
    boolean existsByBusinessLicense(String businessLicense);
}
