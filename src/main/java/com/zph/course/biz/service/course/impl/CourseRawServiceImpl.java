package com.zph.course.biz.service.course.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zph.course.biz.service.email.impl.EmailServiceImpl;
import com.zph.course.common.enumation.CourseStatus;
import com.zph.course.common.enumation.RoleType;
import com.zph.course.common.framework.exception.BusinessException;
import com.zph.course.common.request.PageRequest;
import com.zph.course.common.request.RawCourseRequest;
import com.zph.course.common.vo.PageResultVO;
import com.zph.course.data.entity.CourseRaw;
import com.zph.course.data.entity.UserInfo;
import com.zph.course.data.mapper.CourseRawMapper;
import com.zph.course.biz.service.course.ICourseRawService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zph.course.data.mapper.UserInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


@Service
@Slf4j
public class CourseRawServiceImpl extends ServiceImpl<CourseRawMapper, CourseRaw> implements ICourseRawService {

    @Resource
    private CourseRawMapper courseRawMapper;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private CourseMatchedServiceImpl courseMatchedService;
    @Resource
    private EmailServiceImpl emailService;

    @Override
    public void createRawCourse(RawCourseRequest request) {
        if (Objects.isNull(request.getUserId())) {
            throw new BusinessException(500, "userId cant not be null");
        }
        CourseRaw courseRaw = CourseRaw.builder().build();
        BeanUtils.copyProperties(request, courseRaw);

        courseRaw.setStartDate(request.getDateRange().get(0).minusHours(16).toLocalDate());
        courseRaw.setEndDate(request.getDateRange().get(1).minusHours(16).toLocalDate());
        courseRaw.setStartTime(request.getTimeRange().get(0).minusHours(16).toLocalTime());
        courseRaw.setEndTime(request.getTimeRange().get(1).minusHours(16).toLocalTime());
        UserInfo userInfo = userInfoMapper.selectById(request.getUserId());

        courseRaw.setUserId(userInfo.getId());
        courseRaw.setRole(userInfo.getRole());
        courseRaw.setCourseState(CourseStatus.WAITING_FOR_MATCH.getStatus());
        courseRawMapper.insert(courseRaw);

        if (RoleType.STUDENT.getRole().equals(userInfo.getRole())) {
            doCourseMatch(courseRaw, RoleType.TEACHER);
        } else if (RoleType.TEACHER.getRole().equals(userInfo.getRole())) {
            doCourseMatch(courseRaw, RoleType.STUDENT);
        }

    }

    @Override
    public PageResultVO getRawCourseList(PageRequest request) {

        IPage<CourseRaw> result = courseRawMapper.selectPage(new Page<>(request.getCurrent(), request.getSize()),
                new QueryWrapper<CourseRaw>().lambda()
                        .eq(CourseRaw::getState, true)
                        .eq(CourseRaw::getUserId, request.getUserId())
                        .orderByDesc(CourseRaw::getCreateTime));

        List<CourseRaw> courseList = result.getRecords();
        courseList.sort(Comparator.comparing(CourseRaw::getCreateTime));
        return PageResultVO.builder()
                .current(result.getCurrent())
                .size(result.getSize())
                .total(result.getTotal())
                .records(courseList)
                .build();
    }

    @Override
    public void doCourseMatch(CourseRaw courseRaw, RoleType roleType) {
        List<CourseRaw> courseRawList = courseRawMapper.selectList(new QueryWrapper<CourseRaw>().lambda()
                .ne(CourseRaw::getId, courseRaw.getId())
                .eq(CourseRaw::getState, true)
                .eq(CourseRaw::getRole, roleType.getRole())
                .eq(CourseRaw::getCourseState, CourseStatus.WAITING_FOR_MATCH.getStatus())
                .le(CourseRaw::getStartDate, courseRaw.getStartDate())
                .ge(CourseRaw::getEndDate, courseRaw.getEndDate())
        );
        if (courseRawList.isEmpty()) {
            log.info("无匹配 - " + courseRaw.getId() + " " + courseRaw.getCourseName());
            return;
        }

        for (CourseRaw dateMatch : courseRawList) {
            if (dateMatch.getCourseName().equals(courseRaw.getCourseName())
                    && (dateMatch.getStartTime().isBefore(courseRaw.getStartTime()) || dateMatch.getStartTime().equals(courseRaw.getStartTime()))
                    && (dateMatch.getEndTime().isAfter(courseRaw.getEndTime())) || dateMatch.getEndTime().equals(courseRaw.getEndTime())) {
                UserInfo master = userInfoMapper.selectById(courseRaw.getUserId());
                UserInfo slave = userInfoMapper.selectById(dateMatch.getUserId());
                if (RoleType.STUDENT.getRole().equals(master.getRole())) {
                    courseMatchedService.createMatchedCourse(master, slave, courseRaw);
                } else {
                    courseMatchedService.createMatchedCourse(slave, master, dateMatch);
                }
                courseRaw.setState(false);
                dateMatch.setState(false);
                courseRawMapper.updateById(courseRaw);
                courseRawMapper.updateById(dateMatch);
                log.info("匹配完成");
                return;
            }
        }
        ;
        log.info("无匹配 - " + courseRaw.getId() + " " + courseRaw.getCourseName());
    }
}
