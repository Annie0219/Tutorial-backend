package com.zph.course.data.result;

import lombok.Data;

@Data
public class BusinessResult<T> {


    public static final Integer SUCCESS = 200;

    private Integer code;

    private String errmsg;

    public BusinessResult(Integer code, String errmsg) {
        this.code = code;
        this.errmsg = errmsg;
    }
}
