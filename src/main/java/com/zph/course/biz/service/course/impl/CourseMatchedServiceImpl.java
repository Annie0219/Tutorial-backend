package com.zph.course.biz.service.course.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Strings;
import com.zph.course.common.enumation.CourseStatus;
import com.zph.course.common.framework.exception.BusinessException;
import com.zph.course.common.request.PageRequest;
import com.zph.course.common.vo.CourseMatchedVO;
import com.zph.course.common.vo.PageResultVO;
import com.zph.course.data.entity.CourseMatched;
import com.zph.course.data.entity.CourseRaw;
import com.zph.course.data.entity.UserInfo;
import com.zph.course.data.mapper.CourseMatchedMapper;
import com.zph.course.biz.service.course.ICourseMatchedService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zph.course.data.mapper.UserInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
public class CourseMatchedServiceImpl extends ServiceImpl<CourseMatchedMapper, CourseMatched> implements ICourseMatchedService {

    @Resource
    private CourseMatchedMapper courseMatchedMapper;
    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public void createMatchedCourse(UserInfo student, UserInfo teacher, CourseRaw courseRaw) {
        CourseMatched courseMatched = CourseMatched.builder().build();
        BeanUtils.copyProperties(courseRaw, courseMatched);

        courseMatched.setStudentId(student.getId());
        courseMatched.setTeacherId(teacher.getId());

        courseMatched.setStartTime(courseRaw.getStartTime().atDate(courseRaw.getStartDate()));
        courseMatched.setEndTime(courseRaw.getEndTime().atDate(courseRaw.getEndDate()));

        courseMatched.setCourseState(CourseStatus.UNCONFIRMED.getStatus());
        courseMatchedMapper.insert(courseMatched);
    }

    @Override
    public CourseMatchedVO getCourseMatch(Long id, Long userId) {
        if (Objects.isNull(id)) {
            throw new BusinessException(500, "course id cant be null");
        }
        CourseMatched matched = courseMatchedMapper.selectById(id);
        if (Objects.isNull(matched)) {
            throw new BusinessException(500, "does not exist");
        }
        if (Objects.nonNull(userId)) {
            if (!userId.equals(matched.getStudentId()) && !userId.equals(matched.getTeacherId())) {
                throw new BusinessException(500, "Insufficient permissions");
            }
        }
        CourseMatchedVO courseMatchedVO = CourseMatchedVO.builder().build();
        BeanUtils.copyProperties(matched, courseMatchedVO);
        if (!Strings.isNullOrEmpty(matched.getCallBack())) {
            courseMatchedVO.setIsCallBack(true);
        }
        UserInfo student = userInfoMapper.selectById(matched.getStudentId());
        UserInfo teacher = userInfoMapper.selectById(matched.getTeacherId());
        courseMatchedVO.setStudentName(student.getName());
        courseMatchedVO.setTeacherName(teacher.getName());
        return courseMatchedVO;
    }

    @Override
    public void updateMatchedStates(Long matchesId, Long userId) {
        CourseMatched courseMatched = courseMatchedMapper.selectById(matchesId);
        if (Objects.isNull(courseMatched)) {
            throw new BusinessException(500, "cant find this course");
        }
        if (userId.equals(courseMatched.getStudentId())) {
            courseMatched.setStudentConfirm(true);
        } else if (userId.equals(courseMatched.getTeacherId())) {
            courseMatched.setTeacherConfirm(true);
        }
        if (courseMatched.getStudentConfirm() && courseMatched.getTeacherConfirm()) {
            courseMatched.setCourseState(CourseStatus.READY.getStatus());
        }
        courseMatchedMapper.updateById(courseMatched);
    }

    @Override
    public PageResultVO getMatchedCourse(PageRequest request) {

        IPage<CourseMatched> result = courseMatchedMapper.selectPage(new Page<>(request.getCurrent(), request.getSize()),
                new QueryWrapper<CourseMatched>().lambda()
                        .eq(CourseMatched::getStudentId, request.getUserId()).or()
                        .eq(CourseMatched::getTeacherId, request.getUserId()));
        List<CourseMatchedVO> courseMatchedVOList = new ArrayList<>();
        if (result.getRecords().isEmpty()) {
            return PageResultVO.builder()
                    .current(result.getCurrent())
                    .size(result.getSize())
                    .total(result.getTotal())
                    .records(courseMatchedVOList)
                    .build();
        }

        List<Long> studentId = result.getRecords().stream().map(CourseMatched::getStudentId).collect(Collectors.toList());
        List<Long> teacherId = result.getRecords().stream().map(CourseMatched::getTeacherId).collect(Collectors.toList());
        Map<Long, String> studentNameMap = userInfoMapper.selectBatchIds(studentId).stream().collect(Collectors.toMap(UserInfo::getId, UserInfo::getName));
        Map<Long, String> teacherNameMap = userInfoMapper.selectBatchIds(teacherId).stream().collect(Collectors.toMap(UserInfo::getId, UserInfo::getName));


        result.getRecords().forEach(re -> {
            CourseMatchedVO courseMatchedVO = CourseMatchedVO.builder().build();
            BeanUtils.copyProperties(re, courseMatchedVO);
            courseMatchedVO.setStudentName(studentNameMap.getOrDefault(re.getStudentId(), "student"));
            courseMatchedVO.setTeacherName(teacherNameMap.getOrDefault(re.getTeacherId(), "teacher"));
            courseMatchedVOList.add(courseMatchedVO);
        });

        courseMatchedVOList.sort(Comparator.comparing(CourseMatchedVO::getCreateTime).reversed());

        return PageResultVO.builder()
                .current(result.getCurrent())
                .size(result.getSize())
                .total(result.getTotal())
                .records(courseMatchedVOList)
                .build();
    }

    @Override
    public void callBackTask() {
        List<CourseMatched> courseMatchedList = courseMatchedMapper.selectList(new QueryWrapper<CourseMatched>().lambda()
                .eq(CourseMatched::getState, 1)
                .ne(CourseMatched::getCallBack, null)
                .ge(CourseMatched::getEndTime, LocalDateTime.now()));

        courseMatchedList.forEach(courseMatched -> {
            UserInfo teacher = userInfoMapper.selectById(courseMatched.getTeacherId());
        });
    }

    @Override
    public void remindTask() {
        // 一天前
        List<CourseMatched> courseMatchedOneDayList = courseMatchedMapper.selectList(new QueryWrapper<CourseMatched>().lambda()
                .eq(CourseMatched::getState, 1)
                .le(CourseMatched::getStartTime, LocalDateTime.now().plusDays(1).plusSeconds(30))
                .ge(CourseMatched::getStartTime, LocalDateTime.now().plusDays(1).minusSeconds(30)));

        if (!courseMatchedOneDayList.isEmpty()) {
            List<Long> studentId = courseMatchedOneDayList.stream().map(CourseMatched::getStudentId).collect(Collectors.toList());
            List<Long> teacherId = courseMatchedOneDayList.stream().map(CourseMatched::getTeacherId).collect(Collectors.toList());
            Map<Long, UserInfo> studentNameMap = userInfoMapper.selectBatchIds(studentId).stream().collect(Collectors.toMap(UserInfo::getId, Function.identity()));
            Map<Long, UserInfo> teacherNameMap = userInfoMapper.selectBatchIds(teacherId).stream().collect(Collectors.toMap(UserInfo::getId, Function.identity()));

            courseMatchedOneDayList.forEach(courseMatched -> {
            });
        }


        List<CourseMatched> courseMatchedMinList = courseMatchedMapper.selectList(new QueryWrapper<CourseMatched>().lambda()
                .eq(CourseMatched::getState, 1)
                .le(CourseMatched::getStartTime, LocalDateTime.now().plusMinutes(15).plusSeconds(30))
                .ge(CourseMatched::getStartTime, LocalDateTime.now().plusMinutes(15).minusSeconds(30)));
        if (!courseMatchedMinList.isEmpty()) {
            List<Long> studentId = courseMatchedMinList.stream().map(CourseMatched::getStudentId).collect(Collectors.toList());
            List<Long> teacherId = courseMatchedMinList.stream().map(CourseMatched::getTeacherId).collect(Collectors.toList());
            Map<Long, UserInfo> studentNameMap = userInfoMapper.selectBatchIds(studentId).stream().collect(Collectors.toMap(UserInfo::getId, Function.identity()));
            Map<Long, UserInfo> teacherNameMap = userInfoMapper.selectBatchIds(teacherId).stream().collect(Collectors.toMap(UserInfo::getId, Function.identity()));
            courseMatchedMinList.forEach(courseMatched -> {
            });
        }
        List<CourseMatched> courseMatchedEndList = courseMatchedMapper.selectList(new QueryWrapper<CourseMatched>().lambda()
                .eq(CourseMatched::getState, 1)
                .le(CourseMatched::getEndTime, LocalDateTime.now().plusSeconds(30))
                .ge(CourseMatched::getEndTime, LocalDateTime.now().minusSeconds(30)));
        if (!courseMatchedEndList.isEmpty()) {
            List<Long> studentId = courseMatchedEndList.stream().map(CourseMatched::getStudentId).collect(Collectors.toList());
            List<Long> teacherId = courseMatchedEndList.stream().map(CourseMatched::getTeacherId).collect(Collectors.toList());
            Map<Long, UserInfo> studentNameMap = userInfoMapper.selectBatchIds(studentId).stream().collect(Collectors.toMap(UserInfo::getId, Function.identity()));
            Map<Long, UserInfo> teacherNameMap = userInfoMapper.selectBatchIds(teacherId).stream().collect(Collectors.toMap(UserInfo::getId, Function.identity()));
            courseMatchedEndList.forEach(courseMatched -> {
                courseMatched.setCourseState(CourseStatus.NO_FEEDBACK.getStatus());
            });
        }

    }

    @Override
    public void endCourseTask() {

    }
}
