package com.anli.jesse.exam.wheelactivity.interfaces.response;


import lombok.Data;

@Data
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    // 成功響應，只包含資料
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(0000);
        response.setMessage("Success");
        response.setData(data);
        return response;
    }

    // 成功響應，包含資料和消息
    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(0000);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    // 失敗響應，包含錯誤消息
    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    // 失敗響應，包含錯誤消息和資料 (有時也需要)
    public static <T> ApiResponse<T> error(int code, String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    // 預設建構子
    public ApiResponse() {
    }

    // 帶有所有參數的建構子 (可選)
    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}