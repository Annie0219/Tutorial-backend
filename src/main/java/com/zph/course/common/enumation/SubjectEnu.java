package com.zph.course.common.enumation;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author zhaopenghui
 */

@AllArgsConstructor
@NoArgsConstructor
public enum SubjectEnu {
    // 注册
    REGISTER("register"),
    // 注册
    REMIND("remind"),
    // 登录
    SIGN_UP("sign up");

    private String subject;

    public String getSubject() {
        return subject;
    }
}
