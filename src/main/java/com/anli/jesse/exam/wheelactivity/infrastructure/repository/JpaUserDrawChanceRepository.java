package com.anli.jesse.exam.wheelactivity.infrastructure.repository;

import com.anli.jesse.exam.wheelactivity.domain.model.UserDrawChance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JpaUserDrawChanceRepository extends JpaRepository<UserDrawChance, Long> {
    Optional<UserDrawChance> findByUserIdAndActivityId(Integer userId, Integer activityId);
}
