package com.anli.jesse.exam.wheelactivity.domain.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@ToString
@Slf4j
public class WheelActivity implements Serializable {
    public final static String NO_PRIZE_MESSAGE = "銘謝惠顧";
    private final Integer id;
    private final String name;
    private final List<Prize> prizes;
    private Integer noPrizeProbability;

    private WheelActivity(Integer id,String name) {
        this.id = id;
        this.name = name;
        this.prizes = new ArrayList<>();
        this.noPrizeProbability = Prize.MAX_PROBABILITY; // 初始時無獎品機率為100%
    }

    @JsonCreator
    public WheelActivity(@JsonProperty("id") Integer id,@JsonProperty("name")String name, @JsonProperty("prizes")List<Prize> prizes, @JsonProperty("noPrizeProbability")Integer noPrizeProbability) {
        this.id = id;
        this.name = name;
        this.prizes = prizes != null ? prizes : new ArrayList<>();
        this.noPrizeProbability = noPrizeProbability != null ? noPrizeProbability : Prize.MAX_PROBABILITY;
    }

    public static WheelActivity createWheelActivity(Integer id,String name) {
        if (id == null || name == null || name.isEmpty()) {
            throw new IllegalArgumentException("活動ID和名稱不能為空");
        }
        return new WheelActivity(id, name);
    }

    @JsonIgnore
    public void addPrize(Prize prize) {
        if (prize.getProbability() < 0 || prize.getProbability() > Prize.MAX_PROBABILITY) {
            throw new IllegalArgumentException("無效的獎品機率");
        }
        prizes.add(prize);
        updateProbabilityRange();
    }

    @JsonIgnore
    public void addAllPrize(List<Prize> prizes) {
        if (prizes == null ) {
            throw new IllegalArgumentException("獎品列表不能為空值");
        }
        this.prizes.addAll(prizes);
        for (Prize p : prizes) {
            if (p.getProbability() < 0 || p.getProbability() > Prize.MAX_PROBABILITY) {
                throw new IllegalArgumentException("無效的獎品機率: " + p.getProbability());
            }
        }
        updateProbabilityRange();
    }

    private void updateProbabilityRange() {
        Integer cumulative = 0;
        for (Prize prize : prizes) {
            prize.resetRangeStart(cumulative);
            cumulative += prize.getProbability();
            prize.resetRangeEnd(cumulative);
        }
        if (cumulative > Prize.MAX_PROBABILITY) {
            throw new IllegalStateException("總機率超過100%");
        }
        noPrizeProbability = Prize.MAX_PROBABILITY - cumulative;

    }

    @JsonIgnore
    public DrawResult singleDraw() {
        boolean allPrizesDepleted = prizes.stream().allMatch(p -> p.getQuantity() == 0);
        if (allPrizesDepleted) {
            return DrawResult.notWinning(NO_PRIZE_MESSAGE);
        }

        int rand = ThreadLocalRandom.current().nextInt(Prize.MAX_PROBABILITY + 1);
        for (Prize prize : prizes) {
            log.info("抽獎隨機數: {},獎品名稱: {} , 獎品範圍: [{}, {}], 機率: {}, 數量: {}, 中獎: {}",
    rand, prize.getName(),prize.getRangeStart(), prize.getRangeEnd(), prize.getProbability(), prize.getQuantity()
            ,prize.getQuantity() > 0 && prize.getProbability() > 0 && rand >= prize.getRangeStart()
                            && rand < prize.getRangeEnd());

            if (prize.getQuantity() > 0 && prize.getProbability() > 0 && rand >= prize.getRangeStart()
                    && rand < prize.getRangeEnd()) {
                return DrawResult.winning(prize);
            }
        }
        return DrawResult.notWinning(NO_PRIZE_MESSAGE);
    }

    public List<Prize> getPrizes(){
        return new ArrayList<>(prizes);
    }
}