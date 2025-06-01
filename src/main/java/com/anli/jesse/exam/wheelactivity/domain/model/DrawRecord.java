package com.anli.jesse.exam.wheelactivity.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "user_draw_record")
@NoArgsConstructor
public class DrawRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_draw_chance_id", nullable = false)
    private UserDrawChance userDrawChance;

    @Column(nullable = false)
    private String result;

    public DrawRecord(UserDrawChance userDrawChance, String result) {
        this.userDrawChance = userDrawChance;
        this.result = result;
    }
}
