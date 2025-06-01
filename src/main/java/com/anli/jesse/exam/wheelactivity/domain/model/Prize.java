package com.anli.jesse.exam.wheelactivity.domain.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(exclude = {"id"})
@ToString
@Entity
@Table(name = "prize")
public class Prize implements Serializable{
    /* 最大機率值，1表示0.000001 */
    public static final Integer MAX_PROBABILITY = 1000000;  
    /* 獎品ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /* 獎品名稱 */
    @Column(nullable = false)
    private String name;
    /* 獎品數量 */
    @Column(nullable = false)
    private int quantity;
    /* 中獎機率: 1表示0.000001 */
    @Column(nullable = false)
    private int probability;
    /* 獎品範圍結束 */
    @Column(name = "range_end", nullable = false)
    private Integer rangeEnd;
    /* 獎品範圍開始 */
    @Column(name = "range_start", nullable = false)
    private Integer rangeStart;

    public Prize() {

    }

    @JsonCreator
    public Prize(@JsonProperty("id")Integer id, @JsonProperty("name")String name,  @JsonProperty("quantity")Integer quantity,  @JsonProperty("probability")Integer probability,  @JsonProperty("rangeStart")Integer rangeStart,  @JsonProperty("rangeEnd")Integer rangeEnd) {
        this.id = id;
         this.name = name;
        this.quantity = quantity;
        this.probability = probability;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
    }

    public static Prize createPrize(Integer id, String name, Integer quantity, Integer probability) {
        Prize  prize= new Prize();
        prize.id = id;
        prize.initName(name);
        prize.resetQuantity(quantity);
        prize.resetProbability(probability);
        return prize;
    }

    public void resetQuantity(Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("獎品數量不合法：" + quantity);
        }
        this.quantity = quantity;
    }

    private void initName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("獎品名稱不能為空");
        }
        this.name = name;
    }


    public void resetProbability(Integer probability) {
        if (probability == null || probability < 0 || probability > MAX_PROBABILITY) {
            throw new IllegalArgumentException("獎品機率不合法：" + probability);
        }
        this.probability = probability;
    }

    public Integer getProbability() {
        return quantity > 0 ? probability : 0; // 庫存為零時機率為 0
    }

    public void resetRangeStart(Integer cumulative) {
        if (cumulative < 0 || cumulative > MAX_PROBABILITY) {
            throw new IllegalArgumentException("獎品範圍開始值不合法：" + cumulative);
        }
        this.rangeStart = cumulative;
    }

    public void resetRangeEnd(Integer cumulative) {
        if (cumulative < 0 || cumulative > MAX_PROBABILITY) {
            throw new IllegalArgumentException("獎品範圍結束值不合法：" + cumulative);
        }
        this.rangeEnd = cumulative;
    }

}
