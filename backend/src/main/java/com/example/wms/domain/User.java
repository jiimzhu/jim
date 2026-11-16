package com.example.wms.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 系统用户。
 * 表名使用 sys_user，避免与 H2 / MySQL 的保留字 user 冲突。
 */
@Getter
@Setter
@Entity
@Table(name = "sys_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 登录账号，唯一 */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** BCrypt 加密后的密码 */
    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    /** ADMIN 管理员 / USER 普通用户 */
    @Column(nullable = false, length = 20)
    private String role = Role.USER;

    /** 1 启用 / 0 禁用 */
    @Column(nullable = false)
    private Integer status = 1;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public static final class Role {
        public static final String ADMIN = "ADMIN";
        public static final String USER = "USER";

        private Role() {
        }
    }
}
