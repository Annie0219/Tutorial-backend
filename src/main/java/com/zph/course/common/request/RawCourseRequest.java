package com.zph.course.common.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class RawCourseRequest {
    private Long userId;
    private String title;
    private String courseName;
    private String courseType;
    private List<LocalDateTime> dateRange;
    private List<LocalDateTime> timeRange;
}
