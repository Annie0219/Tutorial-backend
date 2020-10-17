package com.zph.course.common.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CourseMatchedVO {
    private Integer id;
    private String courseName;
    private String studentName;
    private String teacherName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean studentConfirm;
    private Boolean teacherConfirm;
    private String callBack;
    @Builder.Default
    private Boolean isCallBack = false;
    private LocalDateTime createTime;
    private String courseState;
}
