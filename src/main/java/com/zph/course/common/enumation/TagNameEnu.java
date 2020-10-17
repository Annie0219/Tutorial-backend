package com.zph.course.common.enumation;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author zhaopenghui
 */

@AllArgsConstructor
@NoArgsConstructor
public enum TagNameEnu {
    // 注册
    REGISTER("register"),
    // 注册
    COURSE("course"),
    // 登录
    SIGN_UP("signUp");

    private String tagName;

    public String getTagName() {
        return tagName;
    }
}
