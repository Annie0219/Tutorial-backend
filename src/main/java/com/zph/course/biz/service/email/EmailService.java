package com.zph.course.biz.service.email;

import com.zph.course.data.entity.Course;
import com.zph.course.data.entity.CourseMatched;
import com.zph.course.data.entity.CourseRaw;
import com.zph.course.data.entity.UserInfo;

/**
 * @author zhaopenghui
 */
public interface EmailService {

    /**
     * 发送带有验证码的邮件
     *
     * @param email
     */
    void sendCheckEmail(String nickName, String email);

    /**
     * 校验验证码
     *
     * @param email
     * @param code
     * @return
     */
    boolean checkEmail(String email, String code);

    /**
     * course end remind
     *
     * @param user
     * @param courseRaw
     */
    void sendCourseEndCheckEmail(UserInfo user, Course courseRaw);

    /**
     * course matched remind
     *
     * @param user
     * @param courseRaw
     */
    void sendCourseMatchedEmail(UserInfo user, Course courseRaw);

    /**
     * write feedback remind
     *
     * @param user
     * @param courseRaw
     */
    void sendCourseCallBackEmail(UserInfo user, Course courseRaw);

    /**
     * course status remind
     *
     * @param user
     * @param courseRaw
     * @param time
     */
    void sendCourseRemindEmail(UserInfo user, Course courseRaw, String time);

}
