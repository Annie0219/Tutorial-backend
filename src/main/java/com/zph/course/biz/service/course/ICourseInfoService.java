package com.zph.course.biz.service.course;

import com.zph.course.common.enumation.CourseStatus;
import com.zph.course.common.request.CourseInfoRequest;
import com.zph.course.common.request.PageRequest;
import com.zph.course.common.vo.CourseInfoVO;
import com.zph.course.common.vo.PageResultVO;
import com.zph.course.data.entity.CourseInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zph.course.data.entity.UserInfo;


public interface ICourseInfoService extends IService<CourseInfo> {

    CourseInfoVO getCourseInfo(Long id);

    void createCourseInfo(CourseInfoRequest request);

    PageResultVO getCourseInfoList(PageRequest pageRequest);

    void matchCourseInfo(CourseInfo courseInfo);

    void setFeedback(Long courseId, String feedBack);

    void changeCourseStatus(Long courseId, CourseStatus status);
}