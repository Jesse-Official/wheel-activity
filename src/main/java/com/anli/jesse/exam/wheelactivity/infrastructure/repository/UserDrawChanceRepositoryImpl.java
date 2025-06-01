package com.anli.jesse.exam.wheelactivity.infrastructure.repository;

import com.anli.jesse.exam.wheelactivity.domain.model.UserDrawChance;
import com.anli.jesse.exam.wheelactivity.domain.repository.UserDrawChanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDrawChanceRepositoryImpl implements UserDrawChanceRepository {

    private final JpaUserDrawChanceRepository jpaRepo;

    @Override
    public void save(UserDrawChance userDrawChance) {
        jpaRepo.save(userDrawChance);
    }

    @Override
    public Optional<UserDrawChance> findByUserIdAndActivityId(Integer userId, Integer activityId) {
        return jpaRepo.findByUserIdAndActivityId(userId, activityId);
    }
}