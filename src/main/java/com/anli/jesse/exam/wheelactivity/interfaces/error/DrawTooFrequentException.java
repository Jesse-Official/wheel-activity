package com.anli.jesse.exam.wheelactivity.interfaces.error;

public class DrawTooFrequentException extends BusinessException {
    private static final int CODE = 1001;
    private static final String MESSAGE = "抽獎太頻繁，請稍後再試。";

    public DrawTooFrequentException() {
        super(CODE, MESSAGE);
    }
}
