package com.anli.jesse.exam.wheelactivity.interfaces.error;

public class InsufficientDrawingTimesException extends BusinessException {
    private static final int CODE = 1004;
    private static final String MESSAGE = "您的剩餘抽獎次數不足。";

    public InsufficientDrawingTimesException() {
        super(CODE, MESSAGE);
    }

    public InsufficientDrawingTimesException(String message) {
        super(CODE, message);
    }
}
