package com.zph.course.data.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author evan
 * @since 2020-05-18
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
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
     * 用户id
     */
    private Long userId;

    /**
     * 用户角色
     */
    private String userRole;

    /**
     * 课程时长
     */
    private Integer timeCost;

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
     * 课程结束时间
     */
    private LocalTime endTime;

    /**
     * 课程开始时间列表
     */
    private String startDateList;

    /**
     * 周
     */
    private String week;

    /**
     * 是否按周循环
     */
    private Boolean isWeek;

    /**
     * 反馈
     */
    private String callBack;

    /**
     * 学生结束确认
     */
    private Boolean studentConfirm;

    /**
     * 老师结束确认
     */
    private Boolean teacherConfirm;

    /**
     * 课程状态
     */
    private String courseState;

    /**
     * 匹配到的课程id
     */
    private Integer matchCourseId;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 状态
     */
    private Boolean state;

    /**
     * 是否为副本
     */
    private Boolean isCopy;

    /**
     * 实际课程开始时间
     */
    private LocalDateTime courseStartDatetime;

    /**
     * 实际课程结束时间
     */
    private LocalDateTime courseEndDatetime;

    /**
     * 父级id
     */
    private Integer fatherId;

    private Boolean isCallBack;

}
