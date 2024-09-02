package com.ticketgo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
    private int status;
    private String message;
    private Object data;
    private Map<String, String> errors;  // Trường mới để chứa lỗi chi tiết
}
