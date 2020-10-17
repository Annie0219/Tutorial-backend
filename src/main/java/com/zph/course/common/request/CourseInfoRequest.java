package com.zph.course.common.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CourseInfoRequest {
    private Integer title;
    private String courseName;
    private String grade;
    private Long timeCost;

    private Integer userId;
    private Integer userRole;

    private LocalDateTime startTime;

    private Boolean isLoop;
    private List<String> week;
    private LocalDate weekRangeStart;
    private LocalDate weekRangeEnd;

}
