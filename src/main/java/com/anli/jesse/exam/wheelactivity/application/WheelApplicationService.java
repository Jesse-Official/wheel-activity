package com.anli.jesse.exam.wheelactivity.application;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anli.jesse.exam.wheelactivity.domain.model.DrawResult;
import com.anli.jesse.exam.wheelactivity.domain.model.UserDrawChance;
import com.anli.jesse.exam.wheelactivity.domain.model.WheelActivity;
import com.anli.jesse.exam.wheelactivity.domain.repository.UserDrawChanceRepository;
import com.anli.jesse.exam.wheelactivity.domain.repository.WheelActivityRepository;
import com.anli.jesse.exam.wheelactivity.domain.service.ActivityService;
import com.anli.jesse.exam.wheelactivity.interfaces.error.ActivityNotAvailableException;
import com.anli.jesse.exam.wheelactivity.interfaces.error.DrawTooFrequentException;
import com.anli.jesse.exam.wheelactivity.interfaces.error.InsufficientDrawingTimesException;

@Service
public class WheelApplicationService {

    private final ActivityService activityService;
    private final WheelActivityRepository wheelActivityRepository;
    private final UserDrawChanceRepository userDrawChanceRepository;
    private final RedissonClient redissonClient;

    public WheelApplicationService(
            ActivityService activityService, 
            WheelActivityRepository wheelActivityRepository,
            UserDrawChanceRepository userDrawChanceRepository,
            RedissonClient redissonClient) {
        this.activityService = activityService;
        this.wheelActivityRepository = wheelActivityRepository;
        this.userDrawChanceRepository = userDrawChanceRepository;
        this.redissonClient = redissonClient;
    }

    @Transactional
    public List<DrawResult> drawPrize(Integer userId, Integer activityId, int drawTimes) {
        String lockKey = "lock:draw:activity:" + activityId + ":user:" + userId;
        RLock lock = redissonClient.getLock(lockKey);
        boolean acquired = false;
        try {
            acquired = lock.tryLock(3, TimeUnit.SECONDS); // 嘗試獲取鎖，最多等待 3 秒
            if (acquired) {
                UserDrawChance userDrawChance = userDrawChanceRepository
                        .findByUserIdAndActivityId(userId, activityId)
                        .orElseThrow(() -> new InsufficientDrawingTimesException());
                userDrawChance.checkChances(drawTimes);

                WheelActivity wheelActivity = wheelActivityRepository.findById(activityId)
                        .orElseThrow(() -> new ActivityNotAvailableException());

                List<DrawResult> drawResults = activityService.draw(wheelActivity, drawTimes);

                userDrawChance.deductChances(drawTimes);
                userDrawChance.addAllDrawRecord(drawResults.stream().map(result->result.getResult()).toList());
                userDrawChanceRepository.save(userDrawChance);

                return drawResults;
            } else {
                throw new DrawTooFrequentException();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("獲取鎖被中斷");
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }
    }

}