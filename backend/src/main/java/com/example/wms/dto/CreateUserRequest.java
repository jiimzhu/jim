package com.example.wms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增用户请求。
 */
@Data
public class CreateUserRequest {

    @NotBlank(message = "登录账号不能为空")
    @Size(min = 3, max = 20, message = "登录账号长度需在 3 到 20 个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "登录账号只能包含字母、数字和下划线")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度需在 6 到 32 个字符之间")
    private String password;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名不能超过 50 个字符")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Pattern(regexp = "ADMIN|USER", message = "角色只能是 ADMIN 或 USER")
    private String role = "USER";

    private Integer status = 1;
}
