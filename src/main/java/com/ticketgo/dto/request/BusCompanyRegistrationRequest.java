package com.ticketgo.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusCompanyRegistrationRequest {

    @NotBlank(message = "Mật khẩu là bắt buộc")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$", message = "Mật khẩu phải chứa cả chữ và số")
    private String password;

    @NotBlank(message = "Email là bắt buộc")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Tên công ty là bắt buộc")
    @Size(max = 100, message = "Tên công ty không được vượt quá 100 ký tự")
    private String companyName;

    @NotBlank(message = "Số điện thoại là bắt buộc")
    @Pattern(regexp = "^\\d{10}$", message = "Số điện thoại phải có đúng 10 chữ số")
    private String phone;

    @NotBlank(message = "Địa chỉ là bắt buộc")
    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String address;

    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    private String description;

    @NotBlank(message = "Giấy phép kinh doanh là bắt buộc")
    @Size(min = 10, max = 13, message = "Giấy phép kinh doanh phải có từ 10 đến 13 ký tự")
    private String businessLicense;
}
