package com.healthdata.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "activity", indexes = {
    @Index(name = "idx_activity_user_from_time", columnList = "user_id, from_time"),
    @Index(name = "idx_activity_recordkey", columnList = "recordkey")
})
public class Activity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant fromTime;
    @Column(nullable = false)
    private Instant toTime;

    @Column(nullable = false)
    private Float steps;

    @Column(nullable = false)
    private Float calories;

    @Column(nullable = false)
    private Float distance;

    @Column(nullable = false)
    private String recordkey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Activity(Instant fromTime, Instant toTime, Float steps, Float calories, Float distance, String recordkey, User user) {
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.steps = steps;
        this.calories = calories;
        this.distance = distance;
        this.recordkey = recordkey;
        this.user = user;
    }
}