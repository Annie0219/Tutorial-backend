package com.zph.course.biz.service.course.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zph.course.common.enumation.CourseStatus;
import com.zph.course.common.framework.exception.BusinessException;
import com.zph.course.common.request.CourseInfoRequest;
import com.zph.course.common.request.PageRequest;
import com.zph.course.common.vo.CourseInfoBriefVO;
import com.zph.course.common.vo.CourseInfoVO;
import com.zph.course.common.vo.PageResultVO;
import com.zph.course.data.entity.CourseInfo;
import com.zph.course.data.entity.CourseMatched;
import com.zph.course.data.entity.CourseRaw;
import com.zph.course.data.entity.UserInfo;
import com.zph.course.data.mapper.CourseInfoMapper;
import com.zph.course.biz.service.course.ICourseInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zph.course.data.mapper.UserInfoMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;


@Service
public class CourseInfoServiceImpl extends ServiceImpl<CourseInfoMapper, CourseInfo> implements ICourseInfoService {
    @Resource
    private CourseInfoMapper courseInfoMapper;
    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public CourseInfoVO getCourseInfo(Long id) {
        CourseInfo courseInfo = courseInfoMapper.selectById(id);
        if (Objects.nonNull(courseInfo)) {
            CourseInfoVO courseInfoVO = CourseInfoVO.builder().build();
            BeanUtils.copyProperties(courseInfo, courseInfoVO);
            return courseInfoVO;
        } else {
            throw new BusinessException(100, "not exist");
        }
    }

    @Override
    public void createCourseInfo(CourseInfoRequest request) {
        CourseInfo courseInfo = CourseInfo.builder().build();
        BeanUtils.copyProperties(request, courseInfo);
        courseInfo.setWeek(JSONObject.toJSONString(request.getWeek()));
        // week to date
        List<LocalDate> weekToDate = new ArrayList<>();
        if (request.getIsLoop()) {
            LocalDate weekDay = request.getWeekRangeStart();
            for (; weekDay.isAfter(request.getWeekRangeEnd()); weekDay = weekDay.plusDays(1)) {
                String week = weekDay.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                if (request.getWeek().contains(week)) {
                    weekToDate.add(weekDay);
                }
            }
            courseInfo.setLoopTime(JSONObject.toJSONString(weekToDate));
        }
        courseInfo.setCourseState(CourseStatus.WAITING_FOR_MATCH.getStatus());
        courseInfo.setEndTime(courseInfo.getStartTime().plusMinutes(courseInfo.getTimeCost()));
        courseInfoMapper.insert(courseInfo);
        matchCourseInfo(courseInfo);
    }

    @Override
    public PageResultVO getCourseInfoList(PageRequest request) {
        IPage<CourseInfo> result;
        if (Objects.nonNull(request.getStatus())) {
            result = courseInfoMapper.selectPage(new Page<>(request.getCurrent(), request.getSize()),
                    new QueryWrapper<CourseInfo>().lambda()
                            .eq(CourseInfo::getUserId, request.getUserId())
                            .eq(CourseInfo::getState, true)
                            .eq(CourseInfo::getCourseState, request.getStatus()));
        } else {
            result = courseInfoMapper.selectPage(new Page<>(request.getCurrent(), request.getSize()),
                    new QueryWrapper<CourseInfo>().lambda()
                            .eq(CourseInfo::getUserId, request.getUserId())
                            .eq(CourseInfo::getState, true));
        }
        List<CourseInfoBriefVO> briefVOList = new ArrayList<>();
        result.getRecords().forEach(c -> {
            CourseInfoBriefVO briefVO = CourseInfoBriefVO.builder().build();
            BeanUtils.copyProperties(c, briefVO);
            briefVOList.add(briefVO);
        });

        return PageResultVO.builder()
                .current(result.getCurrent())
                .size(result.getSize())
                .total(result.getTotal())
                .records(briefVOList)
                .build();
    }

    @Override
    public void matchCourseInfo(CourseInfo courseInfo) {
        List<CourseInfo> courseInfos;
        if (courseInfo.getIsLoop()) {
            courseInfos = loopMatch(courseInfo);
        } else {
            courseInfos = notLoopMatch(courseInfo);
        }
    }

    private List<CourseInfo> loopMatch(CourseInfo courseInfo) {

        List<CourseInfo> courseInfos = courseInfoMapper.selectList(new QueryWrapper<CourseInfo>().lambda()
                .ne(CourseInfo::getId, courseInfo.getId())
                .ne(CourseInfo::getUserId, courseInfo.getUserId())
                .eq(CourseInfo::getIsLoop, false)
                .eq(CourseInfo::getState, true)
                .eq(CourseInfo::getCourseState, CourseStatus.WAITING_FOR_MATCH.getStatus())
                .ge(CourseInfo::getWeekRangeStart, courseInfo.getStartTime())
                .le(CourseInfo::getWeekRangeEnd, courseInfo.getStartTime()));

        courseInfos.addAll(courseInfoMapper.selectList(new QueryWrapper<CourseInfo>().lambda()
                .ne(CourseInfo::getId, courseInfo.getId())
                .ne(CourseInfo::getUserId, courseInfo.getUserId())
                .eq(CourseInfo::getIsLoop, true)
                .eq(CourseInfo::getState, true)
                .eq(CourseInfo::getCourseState, CourseStatus.WAITING_FOR_MATCH.getStatus())
                .le(CourseInfo::getWeekRangeStart, courseInfo.getWeekRangeStart())
                .ge(CourseInfo::getWeekRangeEnd, courseInfo.getWeekRangeEnd())));
        return courseInfos;
    }

    private List<CourseInfo> notLoopMatch(CourseInfo courseInfo) {
        List<CourseInfo> courseInfos = courseInfoMapper.selectList(new QueryWrapper<CourseInfo>().lambda()
                .ne(CourseInfo::getId, courseInfo.getId())
                .ne(CourseInfo::getUserId, courseInfo.getUserId())
                .eq(CourseInfo::getIsLoop, false)
                .eq(CourseInfo::getState, true)
                .eq(CourseInfo::getCourseState, CourseStatus.WAITING_FOR_MATCH.getStatus())
                .eq(CourseInfo::getStartTime, courseInfo.getStartTime()));

        courseInfos.addAll(courseInfoMapper.selectList(new QueryWrapper<CourseInfo>().lambda()
                .ne(CourseInfo::getId, courseInfo.getId())
                .ne(CourseInfo::getUserId, courseInfo.getUserId())
                .eq(CourseInfo::getIsLoop, true)
                .eq(CourseInfo::getState, true)
                .eq(CourseInfo::getCourseState, CourseStatus.WAITING_FOR_MATCH.getStatus())
                .le(CourseInfo::getStartTime, courseInfo.getWeekRangeStart())
                .ge(CourseInfo::getEndTime, courseInfo.getWeekRangeEnd())));

        return courseInfos;

    }

    @Override
    public void setFeedback(Long courseId, String feedBack) {
        CourseInfo courseInfo = courseInfoMapper.selectById(courseId);
        if (Objects.nonNull(courseInfo)) {
            CourseInfo matchCourseInfo = courseInfoMapper.selectById(courseInfo.getMatchCourseId());
            courseInfo.setCallBack(feedBack);
            matchCourseInfo.setCallBack(feedBack);

            courseInfoMapper.updateById(courseInfo);
            courseInfoMapper.updateById(matchCourseInfo);
        }
    }

    @Override
    public void changeCourseStatus(Long courseId, CourseStatus status) {
        CourseInfo courseInfo = courseInfoMapper.selectById(courseId);
        if (Objects.nonNull(courseInfo)) {
            courseInfo.setCourseState(status.getStatus());
        } else {
            throw new BusinessException(100, "not exist");
        }
    }
}
