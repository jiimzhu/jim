package com.example.wms.controller;

import com.example.wms.common.ApiResponse;
import com.example.wms.common.PageResult;
import com.example.wms.dto.CreateUserRequest;
import com.example.wms.dto.StatusRequest;
import com.example.wms.dto.UpdateUserRequest;
import com.example.wms.dto.UserVO;
import com.example.wms.service.UserService;
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
 * 用户管理接口，仅管理员可访问。
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<PageResult<UserVO>> page(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) Integer status) {
        return ApiResponse.ok(userService.page(page, size, keyword, status));
    }

    @PostMapping
    public ApiResponse<UserVO> create(@Valid @RequestBody CreateUserRequest request) {
        return ApiResponse.ok(userService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<UserVO> update(@PathVariable Long id,
                                      @Valid @RequestBody UpdateUserRequest request) {
        return ApiResponse.ok(userService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<UserVO> changeStatus(@PathVariable Long id,
                                            @Valid @RequestBody StatusRequest request) {
        return ApiResponse.ok(userService.changeStatus(id, request.getStatus()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ApiResponse.ok();
    }
}
