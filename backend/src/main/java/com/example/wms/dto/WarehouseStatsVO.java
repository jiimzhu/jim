package com.example.wms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 仓库总览统计。
 */
@Data
@AllArgsConstructor
public class WarehouseStatsVO {

    private long total;
    private long enabled;
    private long disabled;
    private long totalCapacity;
    private long usedCapacity;

    /** 使用率，0 到 100 的百分比数值 */
    private double usageRate;
}
