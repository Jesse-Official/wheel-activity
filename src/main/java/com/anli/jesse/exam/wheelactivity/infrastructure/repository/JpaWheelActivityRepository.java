package com.anli.jesse.exam.wheelactivity.infrastructure.repository;

import com.anli.jesse.exam.wheelactivity.domain.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaWheelActivityRepository extends JpaRepository<Activity, Integer> {
}
