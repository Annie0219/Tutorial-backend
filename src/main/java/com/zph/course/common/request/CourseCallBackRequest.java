package com.zph.course.common.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseCallBackRequest {
    private String callBack;
    private Long userId;
    private Long courseId;
}
