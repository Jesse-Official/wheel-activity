package com.anli.jesse.exam.wheelactivity.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.anli.jesse.exam.wheelactivity.domain.model.Prize;
import com.anli.jesse.exam.wheelactivity.domain.model.UserDrawChance;
import com.anli.jesse.exam.wheelactivity.domain.model.WheelActivity;
import com.anli.jesse.exam.wheelactivity.domain.repository.UserDrawChanceRepository;
import com.anli.jesse.exam.wheelactivity.domain.repository.WheelActivityRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PrizeInitializer implements ApplicationRunner {

    final RedisTemplate<String, Object> redisTemplate;
    final WheelActivityRepository wheelActivityRepository;
    final UserDrawChanceRepository userDrawChanceRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Prize> prizeList = new ArrayList<>();
        prizeList.add(Prize.createPrize(1, "iPhone", 1, 100000));
        prizeList.add(Prize.createPrize(2, "AirPod", 2, 200000));
        prizeList.add(Prize.createPrize(3, "AirTag", 3, 200000));

        var activity = WheelActivity.createWheelActivity(1, "電商轉盤抽獎活動");
        activity.addAllPrize(prizeList);

        wheelActivityRepository.save(activity);

        wheelActivityRepository.findById(activity.getId())
                .ifPresent(value -> log.info("活動已存在於 Redis: " + value));
        Optional.ofNullable(redisTemplate.opsForHash().entries("wheel:" + activity.getId() + ":prizeInventory"))
                .ifPresent(value -> log.info("活動已存在於 Redis: " + value));

        Integer userId = 1000; // 假設用戶ID為1000
        userDrawChanceRepository.save(
                UserDrawChance.createUserDrawChance(userId, activity.getId(), 10, new ArrayList<>()));


    }

}
