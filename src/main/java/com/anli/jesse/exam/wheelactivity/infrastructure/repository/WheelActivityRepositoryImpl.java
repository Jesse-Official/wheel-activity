package com.anli.jesse.exam.wheelactivity.infrastructure.repository;

import com.anli.jesse.exam.wheelactivity.domain.model.Activity;
import com.anli.jesse.exam.wheelactivity.domain.model.Prize;
import com.anli.jesse.exam.wheelactivity.domain.model.WheelActivity;
import com.anli.jesse.exam.wheelactivity.domain.repository.WheelActivityRepository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class WheelActivityRepositoryImpl implements WheelActivityRepository {
    private final JpaWheelActivityRepository jpaRepo;
    private final HashOperations<String, String, Integer> hashOps;
    private final RedisTemplate<String, Object> redisTemplate;

    public WheelActivityRepositoryImpl(JpaWheelActivityRepository jpaRepo,
            RedisTemplate<String, Object> redisTemplate) {
        this.jpaRepo = jpaRepo;
        this.redisTemplate = redisTemplate;
        this.hashOps = redisTemplate.opsForHash();
    }

    @Override
    @CacheEvict(value = "wheelActivity", key = "#activity.id")
    public void save(WheelActivity activity) {
        Activity entity =jpaRepo.save(toEntity(activity));
        if(activity.getId() == null) {
            activity.setId(entity.getId());
            cachePrizeInventoryMap(activity, entity);
        } 

    }

    public void cachePrizeInventoryMap(WheelActivity activity, Activity entity) {
        String prizeInventoryKey = buildPrizeInventoryKey(activity.getId());
        Map<String, Integer> prizeInventoryMap = activity.getPrizes().stream()
                .collect(Collectors.toMap(prize -> buildPrizeKey(prize.getId()), prize -> prize.getQuantity()));
        hashOps.putAll(prizeInventoryKey, prizeInventoryMap);
    }

    private Activity toEntity(WheelActivity wheelActivity) {
        Activity entity = new Activity();
        entity.setId(wheelActivity.getId());
        entity.setName(wheelActivity.getName());
        entity.setType(wheelActivity.getType());
        entity.setPrizes(wheelActivity.getPrizes());
        entity.setNoPrizeProbability(wheelActivity.getNoPrizeProbability());
        return entity;
    }

    @Override
    @Cacheable(value = "wheelActivity", key = "#activityId")
    public Optional<WheelActivity> findById(Integer activityId) {
        return jpaRepo.findById(activityId)
                .map(entity -> {
                    List<Prize> prizes =  new ArrayList<>(entity.getPrizes());
                    return new WheelActivity(
                            entity.getId(),
                            entity.getName(),
                            entity.getType(),
                            prizes,
                            entity.getNoPrizeProbability()
                    );
                });
    }

    private String buildPrizeInventoryKey(Integer activityId) {
        return "wheel:" + activityId + ":prizeInventory";
    }

    private String buildPrizeKey(Integer prizeId) {
        return "prize:" + prizeId;
    }

    @Override
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

    @Override
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
        if(!redisInventory.isEmpty() &&redisInventory.entrySet().stream().map(Map.Entry::getValue).allMatch(q -> q == 0)) {
            // 如果所有獎品庫存都為0，則刪除該活動的獎品庫存記錄
            redisTemplate.delete(prizeInventoryKey);
        }
    }
}
