package com.zph.course.common.framework.exception;

import lombok.Data;
import lombok.ToString;


/**
 * @author zhaopenghui
 */
@Data
@ToString
public class BusinessException extends RuntimeException {


    private Integer code;
    private String errMsg;

    public static final Integer UNKNOWN_ERR_CODE = 101010;
    public static final String UNKNOWN_ERR_MSG = "the network is dancing, please wait a moment";

    public BusinessException(Integer code, String errMsg) {
        super(errMsg);
        this.code = code;
        this.errMsg = errMsg;
    }
}
