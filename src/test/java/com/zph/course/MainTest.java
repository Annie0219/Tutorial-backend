package com.zph.course;

import com.zph.course.biz.service.course.impl.CourseMatchedServiceImpl;
import com.zph.course.biz.service.course.impl.CourseRawServiceImpl;
import com.zph.course.common.enumation.RoleType;
import com.zph.course.data.entity.CourseRaw;
import com.zph.course.data.mapper.CourseRawMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MainTest {

    @Resource
    private CourseMatchedServiceImpl courseMatchedService;
    @Resource
    CourseRawServiceImpl courseRawService;
    @Resource
    private CourseRawMapper courseRawMapper;

    @Test
    public void doCourseMatchTest() {
        CourseRaw courseRaw = courseRawMapper.selectById(21);
        courseRawService.doCourseMatch(courseRaw, RoleType.TEACHER);
    }

    @Test
    public void remindTaskTest() {
        courseMatchedService.remindTask();
    }
}
