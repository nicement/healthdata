package com.healthdata.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MonthlyActivityDto {
    private String month;
    private Integer steps;
    private Double calories;
    private Double distance;
    private String recordkey;
}
