package com.zph.course.common.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageRequest {
    private Long userId;
    private String status;
    private Long current;
    private Long size;
}
