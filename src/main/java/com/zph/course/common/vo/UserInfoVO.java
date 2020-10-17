package com.zph.course.common.vo;

import com.zph.course.common.framework.xo.VO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
public class UserInfoVO extends VO {
    private Long id;
    private String email;
    private String name;
    private Integer age;
    private String gender;
    private String avatar;
    private String role;
    private Boolean result;
    private Long courseHour;
}
