package com.zph.course.common.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CourseRequest {
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
     * 课程时长(min)
     */
    private Integer timeCost;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户角色
     */
    private String userRole;

    /** =========================== 按周循环 =========================== */

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
    private LocalDateTime weekStartDateTime;

    /**
     * 课程结束日期
     */
    private LocalDateTime weekEndDateTime;


    /** =========================== 不周循环 =========================== */
    /**
     * 课程开始日期
     */
    private LocalDateTime startDateTime;
}
