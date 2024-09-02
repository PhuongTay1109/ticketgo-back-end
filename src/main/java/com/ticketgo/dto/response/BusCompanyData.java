package com.ticketgo.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class BusCompanyData {
    private Integer busCompanyId;
    private String email;
    private String companyName;
    private String phone;
    private String address;
    private String description;
    private String businessLicense;
}
