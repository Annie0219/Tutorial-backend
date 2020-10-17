package com.zph.course.common.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Builder
@Data
public class CourseBriefVO {
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
     * 周
     */
    private List<String> week;

    /**
     * 是否按周循环
     */
    private Boolean isWeek;

    /**
     * 课程开始日期
     */
    private LocalDate startDate;

    /**
     * 课程结束日期
     */
    private LocalDate endDate;

    /**
     * 课程开始时间
     */
    private LocalTime startTime;

    /**
     * 课程状态
     */
    private String courseState;

    /**
     * 实际课程开始时间
     */
    private LocalDateTime courseStartDatetime;

    /**
     * 实际课程结束时间
     */
    private LocalDateTime courseEndDatetime;

    private LocalDateTime createTime;

    private Boolean isCallBack;

    /**
     * 学生结束确认
     */
    private Boolean studentConfirm;

    /**
     * 老师结束确认
     */
    private Boolean teacherConfirm;
}
