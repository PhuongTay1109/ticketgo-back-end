package com.ticketgo.mapper;

import com.ticketgo.dto.response.UserData;
import com.ticketgo.model.User;

public class UserMapper {
    public static UserData toUserRegistrationData(User user) {
        return UserData.builder()
                .userId(user.getId())
                .email(user.getAccount().getEmail())
                .fullName(user.getAccount().getFullName())
                .phone(user.getAccount().getPhone())
                .dateOfBirth(user.getDateOfBirth().toString())  // Assuming dateOfBirth is a java.sql.Date
                .identityNo(user.getIdentityNo())
                .address(user.getAddress())
                .build();
    }
}
