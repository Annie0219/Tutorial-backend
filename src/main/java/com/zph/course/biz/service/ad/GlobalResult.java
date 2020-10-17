package com.zph.course.biz.service.ad;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GlobalResult {
    // 响应业务状态
    private Integer status;

    // 响应消息
    private String msg;

    // 响应中的数据
    private Object data;
}
