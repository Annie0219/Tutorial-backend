package com.zph.course.common.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class CourseMatchedBriefVO {
    private String courseName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean studentConfirm;
    private Boolean teacherConfirm;
    private Boolean isCallBack;
    private LocalDateTime createTime;
}
