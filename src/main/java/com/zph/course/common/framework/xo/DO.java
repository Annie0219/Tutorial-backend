package com.zph.course.common.framework.xo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: liuzhj
 * @date: 2018/12/3
 * @time: 12:13
 */
@Data
public class DO implements Serializable {

    private Integer id;
    private Date createTime;
    private Date updateTime;
    private Boolean status;
}
