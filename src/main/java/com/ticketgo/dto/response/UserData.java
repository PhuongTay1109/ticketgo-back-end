package com.ticketgo.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class UserData {
    private Integer userId;
    private String email;
    private String fullName;
    private String phone;
    private String dateOfBirth;
    private String identityNo;
    private String address;
}
