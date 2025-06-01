package com.anli.jesse.exam.wheelactivity.infrastructure.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.stereotype.Repository;

import com.anli.jesse.exam.wheelactivity.domain.model.UserDrawChance;
import com.anli.jesse.exam.wheelactivity.domain.repository.UserDrawChanceRepository;

import java.util.*;

@Repository
@Profile("redis-store")
public class RedisUserDrawChanceRepository implements UserDrawChanceRepository {
    private final HashOperations<String, String, Object> hashOps;
    private final ListOperations<String, Object> listOps;

    public RedisUserDrawChanceRepository(RedisTemplate<String, Object> redisTemplate) {
        this.hashOps = redisTemplate.opsForHash();
        this.listOps = redisTemplate.opsForList();
    }

    @Override
    public void save(UserDrawChance userDrawChance) {
        //userDrawChance 暫時存在redis中
        String userKey = buildUserDrawChanceKey(userDrawChance.getActivityId(), userDrawChance.getUserId());
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userDrawChance.getUserId());
        map.put("activityId", userDrawChance.getActivityId());
        map.put("chances", userDrawChance.getChances());
        hashOps.putAll(userKey, map);

        for (String record : userDrawChance.getDrawRecords()) {
            listOps.leftPush(userKey + ":records", record);
        }
    }

    private String buildUserDrawChanceKey(Integer activityId, Integer userId) {
        return "wheel:" + activityId + ":user:" + userId;
    }

    @Override
    public Optional<UserDrawChance> findByUserIdAndActivityId(Integer userId, Integer activityId) {
        String userKey = buildUserDrawChanceKey(activityId, userId);
        Object chancesObj = hashOps.get(userKey, "chances");
        if (chancesObj == null) {
            return Optional.of(new UserDrawChance(userId, activityId, 0, List.of()));
        }
        int chances = chancesObj != null ? ((Number) chancesObj).intValue() : 0;
        List<Object> records = listOps.range(userKey + ":records", 0, -1);
        List<String> recordStrings = records == null ? new ArrayList<>() :
            new ArrayList<>(records.stream().map(String::valueOf).toList());
        UserDrawChance userDrawChance = new UserDrawChance(userId, activityId, chances, recordStrings);

        return Optional.of(userDrawChance);
    }
}