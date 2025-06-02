package com.anli.jesse.exam.wheelactivity.domain.model;

import jakarta.persistence.*;


import java.util.List;

@Entity
@Table(name = "activity")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id; 

    @Column(nullable = false)
    private String name;

    @Column(name = "type", nullable = true)
    private String type;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "activity_id")
    private List<Prize> prizes;

    @Column(name = "no_prize_probability")
    private Integer noPrizeProbability;

    public Activity() {
    }

    public Activity(String name, List<Prize> prizes) {
        this.name = name;
        this.prizes = prizes;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Prize> getPrizes() {
        return prizes;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrizes(List<Prize> prizes) {
        this.prizes = prizes;
    }

    public Integer getNoPrizeProbability() {
        return noPrizeProbability;
    }

    public void setNoPrizeProbability(Integer noPrizeProbability) {
        this.noPrizeProbability = noPrizeProbability;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

        @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Activity)) return false;
        Activity activity = (Activity) o;
        return getId() != null && getId().equals(activity.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode(); 
    }
}
