package com.anli.jesse.exam.wheelactivity.domain.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.anli.jesse.exam.wheelactivity.domain.model.DrawResult;
import com.anli.jesse.exam.wheelactivity.domain.model.WheelActivity;
import com.anli.jesse.exam.wheelactivity.domain.repository.WheelActivityRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ActivityService {
    private final WheelActivityRepository wheelActivityRepository;

    public List<DrawResult> draw(WheelActivity wheelActivity, int times) {
        List<DrawResult> results = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            results.add(singleDraw(wheelActivity));
        }
        return results;
    }

    public DrawResult singleDraw(WheelActivity wheelActivity) {
        DrawResult drawResult = wheelActivity.singleDraw();
        if (drawResult.isWinning()) {
            boolean hasCount = wheelActivityRepository.decrementPrizeQuantity(wheelActivity.getId(),
                    drawResult.getPrizeId().get());
            if (hasCount) {
                return drawResult;
            } else {
                // 如果扣數量失敗，則返回未中獎的結果
                return DrawResult.notWinning(WheelActivity.NO_PRIZE_MESSAGE);
            }
        }
        return drawResult;
    }

    
}