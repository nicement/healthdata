package com.healthdata.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class HealthDataRequest {
    private String recordkey;
    private HealthData data;

    @Getter
    @Setter
    public static class HealthData {
        private List<DailyActivityEntry> entries;
    }

    @Getter
    @Setter
    public static class DailyActivityEntry {
        private Period period;
        private Distance distance;
        private Calories calories;
        private Float steps;
    }

    @Getter
    @Setter
    public static class Period {
        private String from; // "2024-11-15 00:00:00" 예시
        private String to;   // "2024-11-15 00:10:00"
    }

    @Getter
    @Setter
    public static class Distance {
        private String unit; // "km" 예시
        private Float value;
    }

    @Getter
    @Setter
    public static class Calories {
        private String unit; // "kcal" 예시
        private Float value;
    }
}
