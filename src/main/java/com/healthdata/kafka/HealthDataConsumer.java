package com.healthdata.kafka;

import com.healthdata.dto.HealthDataRequest;
import com.healthdata.entity.Activity;
import com.healthdata.repository.ActivityRepository;
import com.healthdata.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Kafka Consumer 서비스
 * Kafka 토픽으로부터 건강 데이터를 수신하여 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class HealthDataConsumer {

    private static final Logger log = LoggerFactory.getLogger(HealthDataConsumer.class);
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    // 공통 포맷터 정의
    private static final DateTimeFormatter ISO_OFFSET_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
    private static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * health-data-topic 토픽에서 메시지를 수신합니다.
     * @param data 수신한 건강 데이터
     */
    @KafkaListener(topics = "health-data-topic", groupId = "healthdata-group")
    public void consume(KafkaHealthData data) {
        log.info(messageSource.getMessage("kafka.consumed.message", new Object[]{data.getUserId()}, LocaleContextHolder.getLocale()));

        userRepository.findById(data.getUserId()).ifPresentOrElse(user -> {
            // KafkaHealthData에서 Instant를 직접 사용
            Activity activity = Activity.builder()
                    .fromTime(data.getFromTime())
                    .toTime(data.getToTime())
                    .steps(data.getSteps())
                    .calories(data.getCalories())
                    .distance(data.getDistance())
                    .recordkey(data.getRecordkey())
                    .user(user)
                    .build();

            activityRepository.save(activity); // 데이터베이스에 저장
            log.info(messageSource.getMessage("kafka.activity.saved", new Object[]{data.getUserId()}, LocaleContextHolder.getLocale()));
        }, () -> {
            // 사용자를 찾지 못한 경우 에러 로그
            log.error(messageSource.getMessage("kafka.user.notfound", new Object[]{data.getUserId()}, LocaleContextHolder.getLocale()));
        });
    }

    /**
     * health-data-batch-topic 토픽에서 배치 메시지를 수신합니다.
     * @param data 수신한 배치 건강 데이터
     */
    @KafkaListener(topics = "health-data-batch-topic", groupId = "healthdata-group")
    public void consumeBatch(KafkaBatchHealthData data) {
        log.info(messageSource.getMessage("kafka.consumed.batch.message", new Object[]{data.getUserId()}, LocaleContextHolder.getLocale()));

        userRepository.findById(data.getUserId()).ifPresentOrElse(user -> {
            HealthDataRequest healthDataRequest = data.getHealthDataRequest();

            List<Activity> activities = healthDataRequest.getData().getEntries().stream()
                    .map(entry -> {
                        // 날짜-시간 문자열을 LocalDateTime으로 파싱하기 위해 헬퍼 메서드 사용
                        LocalDateTime fromLocalDateTime = parseDateTimeString(entry.getPeriod().getFrom());
                        LocalDateTime toLocalDateTime = parseDateTimeString(entry.getPeriod().getTo());

                        // Activity 엔티티를 빌드하기 전에 LocalDateTime을 Instant로 변환 (KST 가정)
                        ZoneId kstZone = ZoneId.of("Asia/Seoul");
                        Instant fromTimeInstant = fromLocalDateTime.atZone(kstZone).toInstant();
                        Instant toTimeInstant = toLocalDateTime.atZone(kstZone).toInstant();

                        return Activity.builder()
                                .fromTime(fromTimeInstant)
                                .toTime(toTimeInstant)
                                .steps(entry.getSteps())
                                .calories(entry.getCalories().getValue())
                                .distance(entry.getDistance().getValue())
                                .recordkey(healthDataRequest.getRecordkey())
                                .user(user)
                                .build();
                    })
                    .collect(Collectors.toList());

            activityRepository.saveAll(activities); // 데이터베이스에 저장
            log.info(messageSource.getMessage("kafka.activity.batch.saved", new Object[]{data.getUserId()}, LocaleContextHolder.getLocale()));
        }, () -> {
            // 사용자를 찾지 못한 경우 에러 로그
            log.error(messageSource.getMessage("kafka.user.notfound", new Object[]{data.getUserId()}, LocaleContextHolder.getLocale()));
        });
    }

    /**
     * 다양한 형식의 날짜-시간 문자열을 LocalDateTime으로 파싱합니다.
     * 지원 형식: "yyyy-MM-dd'T'HH:mm:ssZ" (ISO_OFFSET_DATE_TIME) 또는 "yyyy-MM-dd HH:mm:ss"
     * @param dateTimeString 파싱할 날짜-시간 문자열
     * @return 파싱된 LocalDateTime 객체
     * @throws DateTimeParseException 지원되지 않는 형식일 경우
     */
    private LocalDateTime parseDateTimeString(String dateTimeString) {
        try {
            // ISO_OFFSET_DATE_TIME_FORMATTER로 파싱 시도
            return OffsetDateTime.parse(dateTimeString, ISO_OFFSET_DATE_TIME_FORMATTER).toLocalDateTime();
        } catch (DateTimeParseException e) {
            // 첫 번째 시도가 실패하면 LOCAL_DATE_TIME_FORMATTER로 파싱 시도
            try {
                return LocalDateTime.parse(dateTimeString, LOCAL_DATE_TIME_FORMATTER);
            } catch (DateTimeParseException ex) {
                log.error("Failed to parse date-time string: {}", dateTimeString, ex);
                // 두 시도 모두 실패하면 다시 예외 발생
                throw new DateTimeParseException("지원되지 않는 날짜-시간 형식: " + dateTimeString, dateTimeString, 0, ex);
            }
        }
    }
}
