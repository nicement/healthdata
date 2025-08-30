package com.healthdata.repository;

import com.healthdata.dto.MonthlyActivityDto;
import com.healthdata.dto.DailyActivitySummaryDto;
import com.healthdata.entity.Activity;
import com.healthdata.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

/**
 * Activity 엔티티에 대한 데이터 접근을 처리하는 리포지토리 인터페이스.
 * Spring Data JPA의 JpaRepository를 상속받아 기본적인 CRUD 기능을 제공하며,
 * 사용자 정의 쿼리 메소드를 포함합니다.
 */
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByUserAndFromTimeBetween(User user, Instant startDateTime, Instant endDateTime);

    @Query("SELECT new com.healthdata.dto.DailyActivitySummaryDto(" +
           "CAST(DATE_FORMAT(CONVERT_TZ(d.fromTime, '+00:00', :userTimezoneOffset), '%Y-%m-%d') AS string) as daily, " +
           "CAST(SUM(d.steps) AS INTEGER) as steps, " +
           "CAST(SUM(d.calories) AS float) as calories, " +
           "CAST(SUM(d.distance) AS float) as distance, " +
           "d.recordkey as recordkey) " +
           "FROM Activity d WHERE d.user = :user AND d.fromTime BETWEEN :startInstant AND :endInstant " +
           "GROUP BY CAST(DATE_FORMAT(CONVERT_TZ(d.fromTime, '+00:00', :userTimezoneOffset), '%Y-%m-%d') AS string), d.recordkey")
    List<DailyActivitySummaryDto> findDailyActivitySummariesByUser(@Param("user") User user, @Param("startInstant") Instant startInstant, @Param("endInstant") Instant endInstant, @Param("userTimezoneOffset") String userTimezoneOffset);


    @Query("SELECT new com.healthdata.dto.MonthlyActivityDto(" +
           "CAST(DATE_FORMAT(CONVERT_TZ(d.fromTime, '+00:00', :userTimezoneOffset), '%Y-%m') AS string) as month, " +
           "CAST(SUM(d.steps) AS INTEGER) as steps, " +
           "CAST(SUM(d.calories) AS double) as calories, " +
           "SUM(d.distance) as distance, " +
           "d.recordkey as recordkey) " +
           "FROM Activity d WHERE d.user = :user AND d.fromTime BETWEEN :startDateTime AND :endDateTime " +
           "GROUP BY CAST(DATE_FORMAT(CONVERT_TZ(d.fromTime, '+00:00', :userTimezoneOffset), '%Y-%m') AS string), d.recordkey")
    List<MonthlyActivityDto> findMonthlyActivitiesByUser(@Param("user") User user, @Param("startDateTime") Instant startDateTime, @Param("endDateTime") Instant endDateTime, @Param("userTimezoneOffset") String userTimezoneOffset);

}