package com.zph.course.common.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
public class RawCourseBriefVO {
    private Long id;
    private String title;
    private String courseName;
    private String courseType;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String courseState;
    private LocalDateTime createTime;
}
