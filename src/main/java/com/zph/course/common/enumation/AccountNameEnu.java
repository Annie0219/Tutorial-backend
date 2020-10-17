package com.zph.course.common.enumation;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author zhaopenghui
 */

@AllArgsConstructor
@NoArgsConstructor
public enum AccountNameEnu {
    /**
     * 邮箱发送地址
     */
    COURSE("");
    private String accountName;

    public String getAccountName() {
        return accountName;
    }
}
