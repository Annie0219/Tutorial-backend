package com.zph.course.common.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CourseVO {
    private Integer id;
    /**
     * 发布贴标题
     */
    private String title;

    /**
     * 课程名
     */
    private String courseName;

    /**
     * 年级
     */
    private String grade;

    /**
     * 课程时长
     */
    private Integer timeCost;

    /**
     * 学生结束确认
     */
    private Boolean studentConfirm;

    /**
     * 老师结束确认
     */
    private Boolean teacherConfirm;

    /**
     * 实际课程开始时间
     */
    private LocalDateTime courseStartDatetime;

    /**
     * 实际课程结束时间
     */
    private LocalDateTime courseEndDatetime;

    /**
     * 课程状态
     */
    private String courseState;

    /**
     * 反馈
     */
    private String callBack;

    private String studentName;
    private String teacherName;
}
