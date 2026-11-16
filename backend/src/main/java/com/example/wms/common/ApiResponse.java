package com.example.wms.common;

import lombok.Data;

/**
 * 统一响应结构：code 为 0 表示成功，其余为业务错误码。
 */
@Data
public class ApiResponse<T> {

    public static final int SUCCESS = 0;

    private int code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(T data) {
        ApiResponse<T> body = new ApiResponse<>();
        body.setCode(SUCCESS);
        body.setMessage("ok");
        body.setData(data);
        return body;
    }

    public static ApiResponse<Void> ok() {
        return ok(null);
    }

    public static <T> ApiResponse<T> fail(int code, String message) {
        ApiResponse<T> body = new ApiResponse<>();
        body.setCode(code);
        body.setMessage(message);
        return body;
    }
}
