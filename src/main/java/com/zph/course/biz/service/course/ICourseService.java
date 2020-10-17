package com.zph.course.biz.service.course;

import com.zph.course.common.enumation.CourseStatus;
import com.zph.course.common.request.CourseCallBackRequest;
import com.zph.course.common.request.CourseRequest;
import com.zph.course.common.request.PageRequest;
import com.zph.course.common.vo.CourseVO;
import com.zph.course.common.vo.PageResultVO;
import com.zph.course.data.entity.Course;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ICourseService extends IService<Course> {

    CourseVO getCourseInfo(Long id);

    void createCourse(CourseRequest request);

    PageResultVO getCourseInfoList(PageRequest pageRequest);

    void matchCourseInfo(Course course);

    void setFeedback(CourseCallBackRequest request);

    void changeCourseStatus(Long courseId, CourseStatus status);

    void confirmCourse(Long courseId);

    void callBackTask();

    void remindTask();
}
