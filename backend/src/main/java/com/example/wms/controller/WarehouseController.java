package com.example.wms.controller;

import com.example.wms.common.ApiResponse;
import com.example.wms.common.PageResult;
import com.example.wms.domain.Warehouse;
import com.example.wms.dto.StatusRequest;
import com.example.wms.dto.WarehouseSaveRequest;
import com.example.wms.dto.WarehouseStatsVO;
import com.example.wms.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 仓库管理接口：查询对所有登录用户开放，写操作仅管理员可执行。
 */
@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    public ApiResponse<PageResult<Warehouse>> page(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(required = false) String keyword,
                                                   @RequestParam(required = false) Integer status) {
        return ApiResponse.ok(warehouseService.page(page, size, keyword, status));
    }

    @GetMapping("/stats")
    public ApiResponse<WarehouseStatsVO> stats() {
        return ApiResponse.ok(warehouseService.stats());
    }

    @GetMapping("/{id}")
    public ApiResponse<Warehouse> detail(@PathVariable Long id) {
        return ApiResponse.ok(warehouseService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Warehouse> create(@Valid @RequestBody WarehouseSaveRequest request) {
        return ApiResponse.ok(warehouseService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Warehouse> update(@PathVariable Long id,
                                         @Valid @RequestBody WarehouseSaveRequest request) {
        return ApiResponse.ok(warehouseService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Warehouse> changeStatus(@PathVariable Long id,
                                               @Valid @RequestBody StatusRequest request) {
        return ApiResponse.ok(warehouseService.changeStatus(id, request.getStatus()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        warehouseService.delete(id);
        return ApiResponse.ok();
    }
}
