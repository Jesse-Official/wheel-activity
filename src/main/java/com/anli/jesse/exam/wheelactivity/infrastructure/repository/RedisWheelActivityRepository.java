package com.anli.jesse.exam.wheelactivity.infrastructure.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

import com.anli.jesse.exam.wheelactivity.domain.model.Prize;
import com.anli.jesse.exam.wheelactivity.domain.model.WheelActivity;
import com.anli.jesse.exam.wheelactivity.domain.repository.WheelActivityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class RedisWheelActivityRepository implements WheelActivityRepository {
    private final StringRedisTemplate stringRedisTemplate;
    private final HashOperations<String, String, Integer> hashOps;
    private final ObjectMapper objectMapper;

    public RedisWheelActivityRepository(
            StringRedisTemplate stringRedisTemplate,
            RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
        this.hashOps = redisTemplate.opsForHash();
    }

    private String buildWheelActivityKey(Integer activityId) {
        return "wheel:" + activityId;
    }

    private String buildPrizeInventoryKey(Integer activityId) {
        return "wheel:" + activityId + ":prizeInventory";
    }

    private String buildPrizeKey(Integer prizeId) {
        return "prize:" + prizeId;
    }

    @Override
    public void save(WheelActivity activity) {
        try {
            // 暫時先以redis當作資料庫，將活動資訊序列化存儲
            String key = buildWheelActivityKey(activity.getId());
            String activityJson = objectMapper.writeValueAsString(activity);
            stringRedisTemplate.opsForValue().set(key, activityJson);

            // 為了即時性另外保存獎品數量到 Redis
            String prizeInventoryKey = buildPrizeInventoryKey(activity.getId());
            Map<String, Integer> prizeInventoryMap = activity.getPrizes().stream()
                    .collect(Collectors.toMap(prize -> buildPrizeKey(prize.getId()), prize -> prize.getQuantity()));
            hashOps.putAll(prizeInventoryKey, prizeInventoryMap);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save WheelActivity to Redis", e);
        }
    }

    @Override
    public Optional<WheelActivity> findById(Integer activityId) {
        try {
            String key = buildWheelActivityKey(activityId);
            String activityJson = stringRedisTemplate.opsForValue().get(key);
            if (activityJson == null) {
                return Optional.empty();
            }
            WheelActivity wheelActivity = objectMapper.readValue(activityJson, WheelActivity.class);

            return Optional.ofNullable(wheelActivity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load WheelActivity from Redis", e);
        }
    }

    // 原子扣減庫存並檢查庫存
    public boolean decrementPrizeQuantity(Integer activityId, Integer prizeId) {
        String prizeInventoryKey = buildPrizeInventoryKey(activityId);
        String prizeIdKey = buildPrizeKey(prizeId);
        Integer count = hashOps.get(prizeInventoryKey, prizeIdKey);
        if (count == null || count <= 0) {
            return false;
        }
        Long newQuantity = hashOps.increment(prizeInventoryKey, prizeIdKey, -1L);
        if (newQuantity < 0) {
            // 回滾庫存
            hashOps.increment(prizeInventoryKey, prizeIdKey, 1L);
            return false;
        }
        return true;
    }

    public void syncPrizeInventoryToWheelActivity(Integer activityId) {
        Optional<WheelActivity> opt = findById(activityId);
        if (opt.isEmpty())
            return;
        WheelActivity activity = opt.get();
        String prizeInventoryKey = buildPrizeInventoryKey(activityId);
        Map<String, Integer> redisInventory = hashOps.entries(prizeInventoryKey);
        if (redisInventory == null || redisInventory.isEmpty())
            return;

        for (Prize prize : activity.getPrizes()) {
            String redisKey = "prize:" + prize.getId();
            if (redisInventory.containsKey(redisKey)) {
                prize.setQuantity(redisInventory.get(redisKey));
            }
        }
        save(activity);

    }

}