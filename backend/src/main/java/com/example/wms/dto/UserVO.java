package com.example.wms.dto;

import com.example.wms.domain.User;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户视图对象，不包含密码。
 */
@Data
public class UserVO {

    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String role;
    private Integer status;
    private LocalDateTime createdAt;

    public static UserVO from(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
        vo.setCreatedAt(user.getCreatedAt());
        return vo;
    }
}
