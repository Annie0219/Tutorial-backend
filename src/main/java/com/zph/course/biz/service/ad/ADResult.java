package com.zph.course.biz.service.ad;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ADResult {
    private double score;
    private String text;
    private Integer count;
}
