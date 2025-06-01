package com.anli.jesse.exam.wheelactivity.domain.repository;

import java.util.Optional;

import com.anli.jesse.exam.wheelactivity.domain.model.UserDrawChance;

public interface UserDrawChanceRepository {
    void save(UserDrawChance userDrawChance);
    Optional<UserDrawChance> findByUserIdAndActivityId(Integer userId, Integer activityId);
}