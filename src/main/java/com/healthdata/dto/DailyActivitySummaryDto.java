package com.healthdata.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DailyActivitySummaryDto {
    private String daily; // yyyy-MM-dd
    private Integer steps;
    private Float calories;
    private Float distance;
    private String recordkey;
}
