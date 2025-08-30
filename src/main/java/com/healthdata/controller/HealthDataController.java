package com.healthdata.controller;

import com.healthdata.dto.DailyActivitySummaryDto;
import com.healthdata.dto.HealthDataRequest;
import com.healthdata.dto.MonthlyActivityDto;
import com.healthdata.service.HealthDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement; // New import
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

/**
 * 건강 데이터 관련 API를 처리하는 컨트롤러.
 * 일일 활동 기록 생성 및 조회, 월별 활동 기록 조회 기능을 제공합니다.
 */
@Tag(name = "Health Data API", description = "건강 데이터 관련 API")
@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class HealthDataController {

    private final HealthDataService healthDataService;

    

    @Operation(summary = "건강 데이터 저장", description = "건강 데이터를 저장합니다.")
    @ApiResponse(responseCode = "200", description = "건강 데이터 저장 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<Void> saveHealthDataBatch(@org.springframework.web.bind.annotation.RequestBody HealthDataRequest request) {
        healthDataService.saveHealthDataBatch(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "일일 활동 기록 조회", description = "특정 월의 일일 활동 기록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "일일 활동 기록 조회 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 날짜 형식")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/daily")
    public ResponseEntity<List<DailyActivitySummaryDto>> getDailyActivities(
            @Parameter(
                    description = "조회할 연월 (YYYY-MM)",
                    examples = @ExampleObject(name = "연월 예시", value = "2023-10")
            )
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        List<DailyActivitySummaryDto> activities = healthDataService.getDailyActivities(yearMonth);
        return ResponseEntity.ok(activities);
    }

    /**
     * 특정 연도의 월별 활동 기록을 조회합니다.
     *
     * @param year 조회할 연도 (YYYY 형식)
     * @return 해당 연도의 월별 활동 기록 목록
     */
    @Operation(summary = "월별 활동 기록 조회", description = "특정 연도의 월별 활동 기록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "월별 활동 기록 조회 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 연도 형식")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlyActivityDto>> getMonthlyActivities(
            @Parameter(
                    description = "조회할 연도 (YYYY)",
                    examples = @ExampleObject(name = "연도 예시", value = "2023")
            )
            @RequestParam @DateTimeFormat(pattern = "yyyy") Integer year) {
        List<MonthlyActivityDto> activities = healthDataService.getMonthlyActivities(year);
        return ResponseEntity.ok(activities);
    }
}
