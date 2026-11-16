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
 * 仓库主数据。
 */
@Getter
@Setter
@Entity
@Table(name = "warehouse")
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 仓库编码，唯一 */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String address;

    /** 负责人 */
    @Column(length = 50)
    private String manager;

    @Column(length = 20)
    private String phone;

    /** 总容量（可用托位数） */
    @Column(nullable = false)
    private Integer capacity = 0;

    /** 已用容量（可用托位数） */
    @Column(nullable = false)
    private Integer usedCapacity = 0;

    /** 1 启用 / 0 停用 */
    @Column(nullable = false)
    private Integer status = 1;

    @Column(length = 500)
    private String remark;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
