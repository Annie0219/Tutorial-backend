package com.zph.course.biz.service.email.impl;

import com.google.common.base.Strings;
import com.zph.course.biz.service.email.EmailService;
import com.zph.course.common.bo.SendEmailPara;
import com.zph.course.common.cache.EmailCodeCache;
import com.zph.course.common.constant.EmailHtmlBodyConstant;
import com.zph.course.common.enumation.*;
import com.zph.course.common.framework.exception.BusinessException;
import com.zph.course.data.entity.Course;
import com.zph.course.data.entity.CourseMatched;
import com.zph.course.data.entity.CourseRaw;
import com.zph.course.data.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    @Resource
    private AliYunEmailServiceImpl aliYunEmailService;

    @Override
    public void sendCheckEmail(String nickName, String email) {
        String code = RandomStringUtils.random(4, "0123456789");
        if (!"null".equals(EmailCodeCache.getKey(email))) {
            throw new BusinessException(111, "Requests are too frequent, Please try again in 2 minutes");
        }
        aliYunEmailService.sendEmail(SendEmailPara.builder()
                .accountName(AccountNameEnu.COURSE.getAccountName())
                .fromAlias(FromAliasEnu.VERIFY.getAlias())
                .htmlBody(EmailHtmlBodyConstant.verificationTemplate(nickName, code))
                .subject(SubjectEnu.REGISTER.getSubject())
                .tagName(TagNameEnu.REGISTER.getTagName())
                .toAddress(email)
                .build());
        EmailCodeCache.setKey(email, code);
    }

    @Override
    public boolean checkEmail(String email, String code) {
        if (Strings.isNullOrEmpty(code)) {
            return false;
        }
        return code.equals(EmailCodeCache.getKey(email));
    }

    /**
     * 课程结束后发
     *
     * @param user
     * @param courseRaw
     */
    @Override
    public void sendCourseEndCheckEmail(UserInfo user, Course courseRaw) {

        SendEmailPara sendEmailPara = SendEmailPara.builder()
                .accountName(AccountNameEnu.COURSE.getAccountName())
                .fromAlias(FromAliasEnu.ASSISTANT.getAlias())
                .htmlBody(EmailHtmlBodyConstant.courseCheckTemplate(
                        user.getName(), courseRaw.getCourseName(), dateTimeFormat(courseRaw.getCourseStartDatetime()), "https://api.kafkascat.com/course/confirm/" + courseRaw.getId())
                )
                .subject(SubjectEnu.REGISTER.getSubject())
                .tagName(TagNameEnu.REGISTER.getTagName())
                .toAddress(user.getEmail()).build();
        aliYunEmailService.sendEmail(sendEmailPara);
        log.info("sendCourseCallBackEmail" + sendEmailPara);
    }

    @Override
    public void sendCourseMatchedEmail(UserInfo user, Course courseRaw) {
        SendEmailPara sendEmailPara = SendEmailPara.builder()
                .accountName(AccountNameEnu.COURSE.getAccountName())
                .fromAlias(FromAliasEnu.ASSISTANT.getAlias())
                .htmlBody(EmailHtmlBodyConstant.courseMatchSuccessTemplate(
                        user.getName(), courseRaw.getCourseName())
                )
                .subject(SubjectEnu.REGISTER.getSubject())
                .tagName(TagNameEnu.REGISTER.getTagName())
                .toAddress(user.getEmail()).build();
        aliYunEmailService.sendEmail(sendEmailPara);
        log.info("sendCourseMatchedEmail" + sendEmailPara);
    }

    @Override
    public void sendCourseCallBackEmail(UserInfo user, Course courseRaw) {

        SendEmailPara sendEmailPara = SendEmailPara.builder()
                .accountName(AccountNameEnu.COURSE.getAccountName())
                .fromAlias(FromAliasEnu.ASSISTANT.getAlias())
                .subject(SubjectEnu.REMIND.getSubject())
                .tagName(TagNameEnu.COURSE.getTagName())
                .toAddress(user.getEmail()).build();

        if (RoleType.TEACHER.getRole().equals(user.getRole())) {
            sendEmailPara.setHtmlBody(EmailHtmlBodyConstant.courseCallBackTeacherTemplate(
                    user.getName(), courseRaw.getCourseName()));
        } else if (RoleType.STUDENT.getRole().equals(user.getRole())) {
            sendEmailPara.setHtmlBody(EmailHtmlBodyConstant.courseCallBackStudentTemplate(
                    user.getName(), courseRaw.getCourseName()));
        }
        aliYunEmailService.sendEmail(sendEmailPara);
        log.info("sendCourseCallBackEmail" + sendEmailPara);
    }

    @Override
    public void sendCourseRemindEmail(UserInfo user, Course courseMatched, String time) {
        SendEmailPara sendEmailPara = SendEmailPara.builder()
                .accountName(AccountNameEnu.COURSE.getAccountName())
                .fromAlias(FromAliasEnu.ASSISTANT.getAlias())
                .htmlBody(EmailHtmlBodyConstant.courseRemindTemplate(
                        user.getName(), courseMatched.getCourseName(), time, dateTimeFormat(courseMatched.getCourseStartDatetime()))
                )
                .subject(SubjectEnu.REMIND.getSubject())
                .tagName(TagNameEnu.COURSE.getTagName())
                .toAddress(user.getEmail()).build();
        aliYunEmailService.sendEmail(sendEmailPara);
        log.info("sendCourseCallBackEmail" + sendEmailPara);
    }


    private String dateTimeFormat(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
