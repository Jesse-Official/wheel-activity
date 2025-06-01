package com.anli.jesse.exam.wheelactivity.domain.model;

import java.util.Optional;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class DrawResult {
    private final String result;
    private final Optional<Integer> prizeId;

    public static DrawResult notWinning(String result) {
        return new DrawResult(result, Optional.empty());
    }

    public static DrawResult winning(Prize prize) {
        return new DrawResult(prize.getName(), Optional.ofNullable(prize.getId()));
    }

    public boolean isWinning() {
        return prizeId.isPresent();
    }
}
