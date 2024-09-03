package com.ticketgo.mapper;

import com.ticketgo.dto.response.CustomerData;
import com.ticketgo.model.Customer;

public class CustomerMapper {
    public static CustomerData toCustomerData(Customer customer) {
        return CustomerData.builder()
                .userId(customer.getId())
                .email(customer.getAccount().getEmail())
                .fullName(customer.getAccount().getFullName())
                .phone(customer.getAccount().getPhone())
                .dateOfBirth(customer.getDateOfBirth().toString())
                .identityNo(customer.getIdentityNo())
                .address(customer.getAddress())
                .build();
    }
}
