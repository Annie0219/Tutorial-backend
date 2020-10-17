package com.zph.course.common.bo;

import lombok.Builder;
import lombok.Data;

/**
 * @author zhaopenghui
 */
@Data
@Builder
public class SendEmailPara {
    @Builder.Default
    public String accountName = "";
    @Builder.Default
    public String fromAlias = "";
    public String tagName;
    public String toAddress;
    public String subject;
    public String htmlBody;
}
