package com.zph.course.common.enumation;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum CourseStatus {
    // 待匹配
    WAITING_FOR_MATCH("waiting"),
    // 待反馈
    NO_FEEDBACK("pending feedback"),
    // 待上课
    READY("ready"),
    // 结束
    CLOSE("close"),
    // 未确认
    UNCONFIRMED("unconfirmed");

    private String status;

    public String getStatus() {
        return status;
    }
}
