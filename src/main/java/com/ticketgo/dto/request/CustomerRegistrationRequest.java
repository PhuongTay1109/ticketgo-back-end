package com.ticketgo.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRegistrationRequest {

    @NotBlank(message = "Mật khẩu là bắt buộc")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$", message = "Mật khẩu phải chứa cả chữ và số")
    private String password;

    @NotBlank(message = "Email là bắt buộc")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Họ và tên là bắt buộc")
    private String fullName;

    @NotBlank(message = "Số điện thoại là bắt buộc")
    @Pattern(regexp = "^\\d{10}$", message = "Số điện thoại phải có đúng 10 chữ số")
    private String phone;

    @NotBlank(message = "Số căn cước công dân là bắt buộc")
    @Pattern(regexp = "^\\d{12}$", message = "Số căn cước công dân phải có đúng 12 chữ số")
    private String identityNo;

    @NotNull(message = "Ngày sinh là bắt buộc")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Ngày sinh phải theo định dạng YYYY-MM-DD")
    private String dateOfBirth;

    @NotBlank(message = "Địa chỉ là bắt buộc")
    private String address;
}
