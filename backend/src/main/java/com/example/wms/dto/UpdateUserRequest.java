package com.example.wms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改用户请求，密码留空表示不修改。
 */
@Data
public class UpdateUserRequest {

    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名不能超过 50 个字符")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Pattern(regexp = "ADMIN|USER", message = "角色只能是 ADMIN 或 USER")
    private String role;

    private Integer status;

    /** 可选，填写则重置密码 */
    @Size(min = 6, max = 32, message = "密码长度需在 6 到 32 个字符之间")
    private String password;
}
