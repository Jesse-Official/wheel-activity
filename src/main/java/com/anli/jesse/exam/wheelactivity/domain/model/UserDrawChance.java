package com.anli.jesse.exam.wheelactivity.domain.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import com.anli.jesse.exam.wheelactivity.interfaces.error.InsufficientDrawingTimesException;
import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;

@Getter
@Entity
@Table(name = "user_draw_chance")
public class UserDrawChance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 用戶ID
    @Column(nullable = false)
    private Integer userId;
    // 活動ID
    @Column(nullable = false)
    private Integer activityId;
    // 抽獎次數
    @Column(nullable = false)
    private int chances;
    // 抽獎記錄，存儲每次抽獎的結果
    @OneToMany(mappedBy = "userDrawChance", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DrawRecord> drawRecords = new ArrayList<>();

    public UserDrawChance() {}

    @JsonCreator
    public UserDrawChance(Integer userId, Integer activityId, int chances, List<String> drawRecords) {
        this.userId = userId;
        this.activityId = activityId;
        this.chances = chances;
        addAllDrawRecord(drawRecords);
    }

     public static UserDrawChance createUserDrawChance(Integer userId, Integer activityId, int chances, List<String> drawRecords) {
        if (userId == null || activityId == null || chances < 0) {
            throw new IllegalArgumentException("用戶ID、活動ID和抽獎次數不能為空或負數");
        }
        return new UserDrawChance(userId, activityId, chances, drawRecords);
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
        this.drawRecords.add(new DrawRecord(this, result));
    }

    public void addAllDrawRecord(List<String> results) {
        results.forEach(r -> this.drawRecords.add(new DrawRecord(this, r)));
    }

    public List<String> getDrawRecords() {
        List<String> results = new ArrayList<>();
        for (DrawRecord record : drawRecords) {
            results.add(record.getResult());
        }
        return results;
    }

    public Long getId() { return id; }
    public Integer getUserId() { return userId; }
    public Integer getActivityId() { return activityId; }
    public int getChances() { return chances; }
}