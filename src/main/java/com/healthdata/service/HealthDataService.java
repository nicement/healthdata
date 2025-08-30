package com.healthdata.service;

import com.healthdata.dto.ActivityDto;
import com.healthdata.dto.DailyActivitySummaryDto;
import com.healthdata.dto.HealthDataRequest;
import com.healthdata.dto.MonthlyActivityDto;
import com.healthdata.entity.User;
import com.healthdata.kafka.KafkaHealthData;
import com.healthdata.kafka.KafkaBatchHealthData;
import com.healthdata.kafka.HealthDataProducer;
import com.healthdata.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

/**
 * 건강 데이터 관련 비즈니스 로직을 처리하는 서비스 클래스.
 * 일일 활동 기록 생성을 Kafka에 위임하고, 활동 기록을 조회하는 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class HealthDataService {

    private final ActivityRepository activityRepository;
    private final UserService userService;
    private final HealthDataProducer healthDataProducer;

    /**
     * 새로운 일일 활동 기록 생성을 요청합니다.
     * 요청 데이터를 Kafka 토픽으로 전송합니다.
     *
     * @param request 일일 활동 기록 생성 요청 DTO
     */
    public void createDailyActivity(ActivityDto.CreateRequest request) {
        // 현재 인증된 사용자 정보 조회
        User currentUser = userService.getCurrentUser();
        ZoneId userZone = ZoneId.of(currentUser.getTimezoneId()); // 사용자 시간대 가져오기

        // LocalDateTime을 Instant로 변환 (사용자 시간대 가정)
        Instant fromTimeInstant = request.getFromTime().atZone(userZone).toInstant();
        Instant toTimeInstant = request.getToTime().atZone(userZone).toInstant();

        // Kafka로 보낼 메시지 생성
        KafkaHealthData kafkaHealthData = new KafkaHealthData(
            currentUser.getId(),
            fromTimeInstant,
            toTimeInstant,
            request.getSteps(),
            request.getCalories(),
            request.getDistance(),
            request.getRecordkey()
        );
        // 프로듀서를 통해 메시지 전송
        healthDataProducer.send(kafkaHealthData);
    }

    /**
     * 특정 월의 일일 활동 기록을 조회합니다.
     * 현재 인증된 사용자의 기록만 조회합니다。
     *
     * @param yearMonth 조회할 연월
     * @return 해당 월의 일일 활동 기록 응답 DTO 목록
     */
    public List<DailyActivitySummaryDto> getDailyActivities(YearMonth yearMonth) {
        // 현재 인증된 사용자 정보 조회
        User currentUser = userService.getCurrentUser();
        ZoneId userZone = ZoneId.of(currentUser.getTimezoneId()); // 사용자 시간대 가져오기

        // 해당 월의 시작일과 종료일 계산 (사용자 시간대 기준)
        LocalDateTime startUserDateTime = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endUserDateTime = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        // 사용자 LocalDateTime을 쿼리용 UTC Instant로 변환
        Instant startInstant = startUserDateTime.atZone(userZone).toInstant();
        Instant endInstant = endUserDateTime.atZone(userZone).toInstant();

        // 사용자 시간대 오프셋 문자열 가져오기 (예: "+09:00")
        String userTimezoneOffset = userZone.getRules().getOffset(Instant.now()).getId();

        // 사용자 및 날짜 범위 기준으로 활동 기록 조회
        return activityRepository.findDailyActivitySummariesByUser(currentUser, startInstant, endInstant, userTimezoneOffset);
    }

    /**
     * 특정 연도의 월별 활동 기록을 조회합니다。
     *
     * @param year 조회할 연도
     * @return 해당 연도의 월별 활동 기록 응답 DTO 목록
     */
    public List<MonthlyActivityDto> getMonthlyActivities(Integer year) {
        // 현재 인증된 사용자 정보 조회
        User currentUser = userService.getCurrentUser();
        ZoneId userZone = ZoneId.of(currentUser.getTimezoneId()); // 사용자 시간대 가져오기

        // 해당 연도의 시작일과 종료일 계산 (사용자 시간대 기준)
        LocalDateTime startUserDateTime = LocalDate.of(year, 1, 1).atStartOfDay();
        LocalDateTime endUserDateTime = LocalDate.of(year, 12, 31).atTime(23, 59, 59);

        // 사용자 LocalDateTime을 쿼리용 UTC Instant로 변환
        Instant startInstant = startUserDateTime.atZone(userZone).toInstant();
        Instant endInstant = endUserDateTime.atZone(userZone).toInstant();

        // 사용자 시간대 오프셋 문자열 가져오기 (예: "+09:00")
        String userTimezoneOffset = userZone.getRules().getOffset(Instant.now()).getId();

        // 사용자, 시작일, 종료일 기준으로 월별 활동 기록 조회
        return activityRepository.findMonthlyActivitiesByUser(currentUser, startInstant, endInstant, userTimezoneOffset);
    }

    public void saveHealthDataBatch(HealthDataRequest request) {
        User currentUser = userService.getCurrentUser();
        // Kafka로 보낼 메시지 생성
        KafkaBatchHealthData kafkaBatchHealthData = new KafkaBatchHealthData(currentUser.getId(), request);
        // 프로듀서를 통해 메시지 전송
        healthDataProducer.send(kafkaBatchHealthData);
    }
}