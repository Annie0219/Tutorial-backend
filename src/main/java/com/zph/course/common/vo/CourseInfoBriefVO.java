package com.zph.course.common.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class CourseInfoBriefVO {
    private Integer id;
    private Integer title;
    private String courseName;
    private String grade;
    private Long timeCost;

    private LocalDateTime startTime;
    private Boolean isLoop;
    private List<String> week;
    private String courseState;
}
