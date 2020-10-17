package com.zph.course.common.vo;

import com.zph.course.common.framework.xo.VO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class CourseInfoVO extends VO {
    private Integer id;
    private Integer title;
    private String courseName;
    private String grade;
    private Long timeCost;
    private Integer userId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private List<String> week;
    private LocalDate weekRangeStart;
    private LocalDate weekRangeEnd;

    private String callBack;

    private Boolean studentConfirm;
    private Boolean teacherConfirm;

    private Integer courseState;
    private Boolean isCopy;
    private Boolean isLoop;

    private LocalDateTime createTime;
}
