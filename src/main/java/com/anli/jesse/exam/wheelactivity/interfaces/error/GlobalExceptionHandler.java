package com.anli.jesse.exam.wheelactivity.interfaces.error;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.anli.jesse.exam.wheelactivity.interfaces.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 應該要有一處紀錄全部errorcode的地方，這樣可以統一管理錯誤碼，這裏簡化處理
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException ex) {
        ApiResponse<?> errorResponse = ApiResponse.error(ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        ApiResponse<Map<String, String>> response = ApiResponse.error(1006, "請求參數驗證失敗", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<?>> handleNoSuchElementException(NoSuchElementException ex) {
        // 處理找不到資源的異常
        ApiResponse<?> errorResponse = ApiResponse.error(1005, "找不到資源");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {
        // 處理其他未被特定處理的異常
        // 在生產環境中，可能不希望將詳細的錯誤訊息直接返回給客戶端
        ex.printStackTrace();
        ApiResponse<?> errorResponse = ApiResponse.error(9999, "系統發生錯誤，請稍後再試。");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
