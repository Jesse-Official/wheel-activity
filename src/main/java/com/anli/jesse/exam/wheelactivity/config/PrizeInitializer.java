package com.anli.jesse.exam.wheelactivity.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.anli.jesse.exam.wheelactivity.domain.model.Prize;
import com.anli.jesse.exam.wheelactivity.domain.model.UserDrawChance;
import com.anli.jesse.exam.wheelactivity.domain.model.WheelActivity;
import com.anli.jesse.exam.wheelactivity.domain.repository.UserDrawChanceRepository;
import com.anli.jesse.exam.wheelactivity.domain.repository.WheelActivityRepository;
import com.anli.jesse.exam.wheelactivity.infrastructure.repository.JpaWheelActivityRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PrizeInitializer implements ApplicationRunner {

    final RedisTemplate<String, Object> redisTemplate;
    final WheelActivityRepository wheelActivityRepository;
    final UserDrawChanceRepository userDrawChanceRepository;
    final JpaWheelActivityRepository jpaWheelActivityRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        log.info("初始化獎品資料...");
        List<Prize> prizeList = new ArrayList<>();

        prizeList.add(Prize.createPrize(null,"Iphone", 1, 200000));
        prizeList.add(Prize.createPrize(null,"AirPod", 2, 200000));
        prizeList.add(Prize.createPrize(null,"AirTag", 3, 200000));

        var wheelActivity = WheelActivity.createWheelActivity(null, "電商轉盤抽獎活動");
        wheelActivity.addAllPrize(prizeList);

        wheelActivityRepository.save(wheelActivity);

        wheelActivityRepository.findById(wheelActivity.getId())
                .ifPresent(value -> log.info("活動已存在於 Redis: " + value));

        Optional.ofNullable(redisTemplate.opsForHash().entries("wheel:" + wheelActivity.getId() + ":prizeInventory"))
                .ifPresent(value -> log.info("活動已存在於 Redis: " + value));

        Integer userId = 1000; // 假設用戶ID為1000
        userDrawChanceRepository.save(
                UserDrawChance.createUserDrawChance(userId, wheelActivity.getId(), 1000, new ArrayList<>()));


    }

}
