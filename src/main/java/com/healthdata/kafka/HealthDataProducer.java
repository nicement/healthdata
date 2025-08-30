package com.healthdata.kafka;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Kafka Producer 서비스
 * 건강 데이터를 Kafka 토픽으로 전송합니다.
 */
@Service
@RequiredArgsConstructor
public class HealthDataProducer {

    private static final Logger log = LoggerFactory.getLogger(HealthDataProducer.class);
    private static final String HEALTH_DATA_TOPIC = "health-data-topic"; // 메시지를 보낼 토픽 이름
    private static final String HEALTH_DATA_BATCH_TOPIC = "health-data-batch-topic"; // 배치 메시지를 보낼 토픽 이름

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final MessageSource messageSource;

    /**
     * Kafka 토픽으로 건강 데이터를 전송합니다.
     * @param data 전송할 건강 데이터
     */
    public void send(KafkaHealthData data) {
        this.kafkaTemplate.send(HEALTH_DATA_TOPIC, data);
        log.info(messageSource.getMessage("kafka.produced.message", new Object[]{HEALTH_DATA_TOPIC, data.getUserId()}, LocaleContextHolder.getLocale()));
    }

    /**
     * Kafka 토픽으로 배치 건강 데이터를 전송합니다.
     * @param data 전송할 배치 건강 데이터
     */
    public void send(KafkaBatchHealthData data) {
        this.kafkaTemplate.send(HEALTH_DATA_BATCH_TOPIC, data);
        log.info(messageSource.getMessage("kafka.produced.message", new Object[]{HEALTH_DATA_BATCH_TOPIC, data.getUserId()}, LocaleContextHolder.getLocale()));
    }
}
