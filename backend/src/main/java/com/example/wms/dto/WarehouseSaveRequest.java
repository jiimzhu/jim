package com.example.wms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增 / 修改仓库请求，修改时仓库编码不可变更。
 */
@Data
public class WarehouseSaveRequest {

    @NotBlank(message = "仓库编码不能为空")
    @Size(max = 50, message = "仓库编码不能超过 50 个字符")
    @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "仓库编码只能包含字母、数字和短横线")
    private String code;

    @NotBlank(message = "仓库名称不能为空")
    @Size(max = 100, message = "仓库名称不能超过 100 个字符")
    private String name;

    @Size(max = 255, message = "地址不能超过 255 个字符")
    private String address;

    @Size(max = 50, message = "负责人姓名不能超过 50 个字符")
    private String manager;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotNull(message = "总容量不能为空")
    @Min(value = 0, message = "总容量不能为负数")
    private Integer capacity;

    @Min(value = 0, message = "已用容量不能为负数")
    private Integer usedCapacity = 0;

    private Integer status = 1;

    @Size(max = 500, message = "备注不能超过 500 个字符")
    private String remark;
}
