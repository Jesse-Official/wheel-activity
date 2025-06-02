package com.anli.jesse.exam.wheelactivity.interfaces.error;

public class ActivityNotAvailableException extends BusinessException {
    private static final String CODE = "1002";
    private static final String MESSAGE = "目前沒有可用的抽獎活動。";

    public ActivityNotAvailableException() {
        super(CODE, MESSAGE);
    }
}
