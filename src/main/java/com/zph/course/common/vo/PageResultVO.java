package com.zph.course.common.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageResultVO {
    private Long total;

    private Long current;

    private Long size;

    private Object records;
}
