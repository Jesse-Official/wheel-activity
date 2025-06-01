package com.anli.jesse.exam.wheelactivity.domain.repository;

import java.util.Optional;

import com.anli.jesse.exam.wheelactivity.domain.model.WheelActivity;


public interface WheelActivityRepository {
    void save(WheelActivity activity);
    Optional<WheelActivity> findById(Integer activityId);
    boolean decrementPrizeQuantity(Integer activityId, Integer prizeId);
    void syncPrizeInventoryToWheelActivity(Integer activityId);
}