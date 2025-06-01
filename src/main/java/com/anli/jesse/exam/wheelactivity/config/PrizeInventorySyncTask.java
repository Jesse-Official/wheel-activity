package com.anli.jesse.exam.wheelactivity.config;

import com.anli.jesse.exam.wheelactivity.domain.repository.WheelActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PrizeInventorySyncTask {
    private final WheelActivityRepository wheelActivityRepository;

    // 假設活動ID列表已知或可從其他來源獲取，這裡以1為例
    private final List<Integer> activityIds = List.of(1); // TODO: 動態獲取所有活動ID

    /*  
      同步PrizeInventory回到實體上，每10秒執行一次
      以 Scheduled 設定為例，正式環境應採用分布式任務方案
    */
    @Scheduled(fixedRate = 10000) 
    @Transactional
    public void syncPrizeInventoryToWheelActivityJob() {
        for (Integer activityId : activityIds) {
             wheelActivityRepository.syncPrizeInventoryToWheelActivity(activityId);
            log.info("同步活動 {} 的獎品庫存至 WheelActivity", activityId);
        }
    }
}
