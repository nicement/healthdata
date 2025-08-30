package com.healthdata.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Kafka를 통해 전송될 건강 데이터 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaHealthData {
    /**
     * 사용자 ID
     */
    private Long userId;
    /**
     * 활동 시작 시간 (UTC Instant)
     */
    private Instant fromTime;
    /**
     * 활동 종료 시간 (UTC Instant)
     */
    private Instant toTime;
    /**
     * 걸음 수
     */
    private Float steps;
    /**
     * 소모 칼로리
     */
    private Float calories;
    /**
     * 이동 거리
     */
    private Float distance;
    /**
     * 레코드 키
     */
    private String recordkey;
}
