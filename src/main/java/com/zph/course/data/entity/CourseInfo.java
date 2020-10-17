package com.zph.course.data.entity;

import java.time.LocalDate;
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
 * @since 2020-05-14
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CourseInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 发布贴标题
     */
    private Integer title;

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
    private Long timeCost;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户角色
     */
    private Integer userRole;

    /**
     * 课程开始时间
     */
    private LocalDateTime startTime;

    /**
     * 课程结束时间
     */
    private LocalDateTime endTime;

    /**
     * 循环开始时间
     */
    private String loopTime;

    /**
     * 周
     */
    private String week;

    /**
     * 周开始
     */
    private LocalDate weekRangeStart;

    /**
     * 周结束
     */
    private LocalDate weekRangeEnd;

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
     * 自动创建
     */
    private Boolean isCopy;

    /**
     * 是否循环
     */
    private Boolean isLoop;

}
