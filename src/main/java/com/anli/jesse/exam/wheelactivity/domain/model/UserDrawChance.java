package com.anli.jesse.exam.wheelactivity.domain.model;

import java.util.ArrayList;
import java.util.List;

import com.anli.jesse.exam.wheelactivity.interfaces.error.InsufficientDrawingTimesException;
import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;

@Getter
public class UserDrawChance {
    // 用戶ID
    private final Integer userId;
    // 活動ID
    private final Integer activityId;
    // 抽獎次數
    private int chances;
    // 抽獎記錄，存儲每次抽獎的結果
    private final List<String> drawRecords;

    @JsonCreator
    public UserDrawChance(Integer userId, Integer activityId, int chances, List<String> drawRecords) {
        this.userId = userId;
        this.activityId = activityId;
        this.chances = chances;
        this.drawRecords = drawRecords;
    }

     public static UserDrawChance createUserDrawChance(Integer userId, Integer activityId, int chances,List<String> drawRecords) {
        if (userId == null || activityId == null || chances < 0) {
            throw new IllegalArgumentException("用戶ID、活動ID和抽獎次數不能為空或負數");
        }
        return new UserDrawChance(userId, activityId, chances, drawRecords != null ? drawRecords : new ArrayList<>());
    }

    public void deductChances(int times) {
        checkChances(times);
        chances -= times;
    }

    public void checkChances(int times) {
        if (times > chances || times <= 0) {
            throw new InsufficientDrawingTimesException();
        }
    }

    public void addDrawRecord(String result) {
        drawRecords.add(result);
    }

    public void addAllDrawRecord(List<String> results) {
        drawRecords.addAll(results);
    }

    public List<String> getDrawRecords() {
        return new ArrayList<>(drawRecords);
    }


}