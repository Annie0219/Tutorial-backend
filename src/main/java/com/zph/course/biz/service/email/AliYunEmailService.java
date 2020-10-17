package com.zph.course.biz.service.email;

import com.zph.course.common.bo.SendEmailPara;

/**
 * @author zhaopenghui
 */
public interface AliYunEmailService {

    boolean sendEmail(SendEmailPara para);
}
