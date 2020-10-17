package com.zph.course.biz.service.email.impl;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.dm.model.v20151123.SingleSendMailResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.zph.course.biz.service.email.AliYunEmailService;
import com.zph.course.common.bo.SendEmailPara;
import com.zph.course.common.framework.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
@Slf4j
public class AliYunEmailServiceImpl implements AliYunEmailService {

    @Override
    public boolean sendEmail(SendEmailPara para) {

        IClientProfile profile = DefaultProfile
                .getProfile("",
                        "",
                        "");
        IAcsClient client = new DefaultAcsClient(profile);
        SingleSendMailRequest request = new SingleSendMailRequest();
        try {
            request.setAccountName(para.getAccountName());
            request.setFromAlias(para.getFromAlias());
            request.setAddressType(1);
            request.setTagName(para.getTagName());
            request.setReplyToAddress(true);
            request.setToAddress(para.getToAddress());
            request.setSubject(para.getSubject());
            request.setHtmlBody(para.getHtmlBody());

            request.setMethod(MethodType.POST);
            SingleSendMailResponse httpResponse = client.getAcsResponse(request);
            if (Objects.nonNull(httpResponse)) {
                return true;
            }
        } catch (ClientException e) {
            //捕获错误异常码
            e.printStackTrace();
            throw new BusinessException(111, e.getMessage());
        }
        return false;
    }
}
