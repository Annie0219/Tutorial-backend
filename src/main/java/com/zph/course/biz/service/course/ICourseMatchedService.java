package com.zph.course.biz.service.course;

import com.zph.course.common.request.PageRequest;
import com.zph.course.common.vo.CourseMatchedVO;
import com.zph.course.common.vo.PageResultVO;
import com.zph.course.data.entity.CourseMatched;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zph.course.data.entity.CourseRaw;
import com.zph.course.data.entity.UserInfo;


public interface ICourseMatchedService extends IService<CourseMatched> {

    void createMatchedCourse(UserInfo student, UserInfo teacher, CourseRaw courseRaw);

    CourseMatchedVO getCourseMatch(Long id, Long userId);

    void updateMatchedStates(Long matchesId, Long userId);

    PageResultVO getMatchedCourse(PageRequest request);

    void callBackTask();

    void remindTask();

    void endCourseTask();
}
