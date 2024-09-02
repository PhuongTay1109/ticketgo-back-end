package com.ticketgo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegistrationResponse {
    private Integer userId;
    private Integer busCompanyId;
    public RegistrationResponse(Integer userId, Integer busCompanyId) {
        this.userId = userId;
        this.busCompanyId = busCompanyId;
    }
}
