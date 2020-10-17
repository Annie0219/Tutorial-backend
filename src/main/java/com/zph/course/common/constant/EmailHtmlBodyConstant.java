package com.zph.course.common.constant;

/**
 * @author zhaopenghui
 */
public class EmailHtmlBodyConstant {

    public static String verificationTemplate(String userName, String code) {
        return String.format("Hey %s! ,Verification Code: %s", userName, code);
    }
    /*
    Hey %s!
    To complete the sign in, enter the verification code on the unrecognized device.
    Verification code: %s
    Thanks
     */

    public static String courseCheckTemplate(String userName, String courseName, String timeRange, String url) {
        return String.format(
                "Hey %s! \n课程已经结束，请登录course网站内查看具体信息\n点击下方链接确认课程已经结束\n课程名：%s 课程时间：%s\n %s",
                userName, courseName, timeRange, url);
    }

    public static String courseCallBackTeacherTemplate(String userName, String courseName) {
        return String.format("Hey %s! \n您的课程 %s 已经结束，请填写课程反馈", userName, courseName);
    }

    public static String courseRemindTemplate(String userName, String courseName, String time, String timeRange) {
        return String.format("Hey %s! \n您的课程 %s 还有 %s 就开始上课了，请做好准备\n 上课时间 %s",
                userName, courseName, time, timeRange);
    }

    public static String courseCallBackStudentTemplate(String userName, String courseName) {
        return String.format("Hey %s! \n您的课程 %s 已经结束，稍后可以查看教师反馈", userName, courseName);
    }

    public static String courseMatchSuccessTemplate(String userName, String courseName) {
        return String.format("Hey %s! \n您的课程 %s 匹配成功，请登录https://course.kafkascat.com查看具体信息", userName, courseName);
    }
}
