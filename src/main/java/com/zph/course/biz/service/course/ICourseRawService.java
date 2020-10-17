package com.zph.course.biz.service.course;

import com.zph.course.common.enumation.RoleType;
import com.zph.course.common.request.PageRequest;
import com.zph.course.common.request.RawCourseRequest;
import com.zph.course.common.vo.PageResultVO;
import com.zph.course.data.entity.CourseRaw;
import com.baomidou.mybatisplus.extension.service.IService;


public interface ICourseRawService extends IService<CourseRaw> {

    void createRawCourse(RawCourseRequest request);

    PageResultVO getRawCourseList(PageRequest request);

    void doCourseMatch(CourseRaw courseRaw, RoleType roleType);
}
