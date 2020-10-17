package com.zph.course.common.enumation;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author zhaopenghui
 */

@AllArgsConstructor
@NoArgsConstructor
public enum FromAliasEnu {
    // 助理
    ASSISTANT("assistant"),
    // 无需回复
    NO_REPLY("noReply"),
    // 注册
    VERIFY("verify"),
    // 管理中心
    CENTER("center");

    private String alias;

    public String getAlias() {
        return alias;
    }
}
