package com.zph.course.common.request;

import com.zph.course.common.framework.xo.VO;
import lombok.Data;

/**
 * @author zhaopenghui
 */
@Data
public class LoginRequest extends VO {
    private String email;
    private String password;
}
