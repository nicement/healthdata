package com.healthdata.dto;

import com.healthdata.entity.Activity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class ActivityDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private LocalDateTime fromTime;
        private LocalDateTime toTime;
        private Float steps;
        private Float calories;
        private Float distance;
        private String recordkey;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private LocalDateTime fromTime;
        private LocalDateTime toTime;
        private Float steps;
        private Float calories;
        private Float distance;
        private String recordkey;

        public static Response from(Activity activity) {
            // Instant를 KST LocalDateTime으로 변환
            ZoneId kstZone = ZoneId.of("Asia/Seoul");
            return new Response(
                LocalDateTime.ofInstant(activity.getFromTime(), kstZone),
                LocalDateTime.ofInstant(activity.getToTime(), kstZone),
                activity.getSteps(),
                activity.getCalories(),
                activity.getDistance(),
                activity.getRecordkey()
            );
        }
    }
}