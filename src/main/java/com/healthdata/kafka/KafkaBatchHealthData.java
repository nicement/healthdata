package com.healthdata.kafka;

import com.healthdata.dto.HealthDataRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Kafka를 통해 배치 건강 데이터를 전송하기 위한 DTO 클래스.
 * 이 클래스는 데이터를 전송하는 사용자의 ID와 실제 건강 데이터 요청을 포함합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaBatchHealthData {
    private Long userId;
    private HealthDataRequest healthDataRequest;
}
