package com.zph.course.data.entity;

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
 * @since 2020-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
public class CourseMatched implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 课程名
     */
    private String courseName;

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 教师id
     */
    private Long teacherId;

    /**
     * 学生课程id
     */
    private Long studentCourseId;

    /**
     * 教师课程id
     */
    private Long teacherCourseId;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 学生已确认
     */
    private Boolean studentConfirm;

    /**
     * 教师已确认
     */
    private Boolean teacherConfirm;

    /**
     * 反馈
     */
    private String callBack;

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

    private String courseState;


}
