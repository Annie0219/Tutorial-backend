package com.zph.course.biz.service.course.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Strings;
import com.zph.course.biz.service.email.impl.EmailServiceImpl;
import com.zph.course.common.enumation.CourseStatus;
import com.zph.course.common.enumation.RoleType;
import com.zph.course.common.framework.exception.BusinessException;
import com.zph.course.common.request.CourseCallBackRequest;
import com.zph.course.common.request.CourseRequest;
import com.zph.course.common.request.PageRequest;
import com.zph.course.common.vo.*;
import com.zph.course.data.entity.*;
import com.zph.course.data.mapper.CourseMapper;
import com.zph.course.biz.service.course.ICourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zph.course.data.mapper.UserInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {
    @Resource
    private CourseMapper courseMapper;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private EmailServiceImpl emailService;

    @Override
    public CourseVO getCourseInfo(Long id) {
        Course course = courseMapper.selectById(id);
        if (Objects.isNull(course)) {
            throw new BusinessException(100, "not exist");
        }
        if (CourseStatus.WAITING_FOR_MATCH.getStatus().equals(course.getCourseState())) {
            throw new BusinessException(100, "not match");
        }
        CourseVO courseVO = CourseVO.builder().build();
        BeanUtils.copyProperties(course, courseVO);

        Course otherCourse = courseMapper.selectById(course.getMatchCourseId());
        UserInfo courseUser = userInfoMapper.selectById(course.getUserId());
        UserInfo otherUser = userInfoMapper.selectById(otherCourse.getUserId());

        if (RoleType.STUDENT.getRole().toUpperCase().equals(courseUser.getRole().toUpperCase())) {
            courseVO.setStudentName(courseUser.getName());
            courseVO.setTeacherName(otherUser.getName());
        } else {
            courseVO.setStudentName(otherUser.getName());
            courseVO.setTeacherName(courseUser.getName());
        }
        return courseVO;
    }

    @Override
    public void createCourse(CourseRequest request) {
        if (Objects.isNull(request)) {
            return;
        }
        Course course = Course.builder().build();
        BeanUtils.copyProperties(request, course);

        /*
         * MONDAY
         * TUESDAY
         * WEDNESDAY
         * THURSDAY
         * FRIDAY
         * SATURDAY
         * SUNDAY
         */
        if (request.getIsWeek()) {
            if (request.getWeek().isEmpty()) {
                throw new BusinessException(1003, "week is empty");
            }
            List<LocalDate> startDateList = new ArrayList<>();
            LocalDate weekDay = request.getWeekStartDateTime().toLocalDate();
            for (; weekDay.isBefore(request.getWeekEndDateTime().toLocalDate()); weekDay = weekDay.plusDays(1)) {
                String week = weekDay.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                if (request.getWeek().contains(week.toUpperCase())) {
                    startDateList.add(weekDay);
                }
            }
            course.setStartDate(request.getWeekStartDateTime().toLocalDate());
            course.setEndDate(request.getWeekEndDateTime().toLocalDate());
            course.setStartTime(request.getWeekStartDateTime().toLocalTime());
            course.setEndTime(request.getWeekStartDateTime().plusMinutes(request.getTimeCost()).toLocalTime());

            course.setWeek(JSONObject.toJSONString(request.getWeek()));
            course.setStartDateList(JSONObject.toJSONString(startDateList));
        } else {
            course.setStartDate(request.getStartDateTime().toLocalDate());
            course.setEndDate(request.getStartDateTime().plusMinutes(request.getTimeCost()).toLocalDate());
            course.setStartTime(request.getStartDateTime().toLocalTime());
            course.setEndTime(request.getStartDateTime().plusMinutes(request.getTimeCost()).toLocalTime());
            List<LocalDate> startDateList = new ArrayList<>();
            startDateList.add(course.getStartDate());
            course.setStartDateList(JSONObject.toJSONString(startDateList));
        }
        courseMapper.insert(course);
        matchCourseInfo(course);
    }

    @Override
    public PageResultVO getCourseInfoList(PageRequest request) {
        List<String> statueList = new ArrayList<>();
        if (Strings.isNullOrEmpty(request.getStatus())) {
            statueList.add(CourseStatus.READY.getStatus());
            statueList.add(CourseStatus.WAITING_FOR_MATCH.getStatus());
            statueList.add(CourseStatus.CLOSE.getStatus());
            statueList.add(CourseStatus.NO_FEEDBACK.getStatus());
            statueList.add(CourseStatus.UNCONFIRMED.getStatus());
        } else if ("undone".equals(request.getStatus())) {
            statueList.add(CourseStatus.READY.getStatus());
            statueList.add(CourseStatus.NO_FEEDBACK.getStatus());
            statueList.add(CourseStatus.UNCONFIRMED.getStatus());
        } else if ("close".equals(request.getStatus())) {
            statueList.add(CourseStatus.CLOSE.getStatus());
        }

        IPage<Course> result = courseMapper.selectPage(new Page<>(request.getCurrent(), request.getSize()),
                new QueryWrapper<Course>().lambda()
                        .eq(Course::getUserId, request.getUserId())
                        .eq(Course::getState, true)
                        .in(Course::getCourseState, statueList)
                        .orderByDesc(Course::getCreateTime));
        ;

        List<CourseBriefVO> briefVOList = new ArrayList<>();
        result.getRecords().forEach(c -> {
            CourseBriefVO courseBriefVO = CourseBriefVO.builder().build();
            BeanUtils.copyProperties(c, courseBriefVO);
            if (c.getIsWeek()) {
                courseBriefVO.setWeek(JSONObject.parseArray(c.getWeek(), String.class));
            }
            briefVOList.add(courseBriefVO);
        });
        briefVOList.sort(Comparator.comparing(CourseBriefVO::getCreateTime).reversed());
        return PageResultVO.builder()
                .current(result.getCurrent())
                .size(result.getSize())
                .total(result.getTotal())
                .records(briefVOList)
                .build();
    }

    @Override
    public void matchCourseInfo(Course course) {
        List<Course> courseList;
        if (RoleType.STUDENT.getRole().toUpperCase().equals(course.getUserRole().toUpperCase())) {
            courseList = courseMapper.selectList(new QueryWrapper<Course>().lambda()
                    .ne(Course::getId, course.getId())
                    .ne(Course::getUserId, course.getUserId())
                    .ne(Course::getUserRole, course.getUserRole())
                    .eq(Course::getState, true)
                    .eq(Course::getIsCopy, false)
                    .eq(Course::getCourseState, CourseStatus.WAITING_FOR_MATCH.getStatus())
                    .eq(Course::getTimeCost, course.getTimeCost())
                    .ge(Course::getGrade, course.getGrade())
                    .ge(Course::getStartDate, course.getStartDate())
                    .le(Course::getEndDate, course.getEndDate()));
        } else {
            courseList = courseMapper.selectList(new QueryWrapper<Course>().lambda()
                    .ne(Course::getId, course.getId())
                    .ne(Course::getUserId, course.getUserId())
                    .ne(Course::getUserRole, course.getUserRole())
                    .eq(Course::getState, true)
                    .eq(Course::getIsCopy, false)
                    .eq(Course::getCourseState, CourseStatus.WAITING_FOR_MATCH.getStatus())
                    .eq(Course::getTimeCost, course.getTimeCost())
                    .le(Course::getGrade, course.getGrade())
                    .ge(Course::getStartDate, course.getStartDate())
                    .le(Course::getEndDate, course.getEndDate()));
        }

        if (courseList.isEmpty()) {
            return;
        }
        List<LocalDate> courseStartDateList = JSONObject.parseArray(course.getStartDateList(), LocalDate.class);

        courseStartDateList.forEach(courseLocalDate -> {
            for (Course readyCourse : courseList) {
                List<LocalDate> ls = JSONObject.parseArray(readyCourse.getStartDateList(), LocalDate.class);
                if (ls.contains(courseLocalDate)) {
                    // 日期匹配，下面开始匹配时间
                    if (course.getStartTime().compareTo(readyCourse.getStartTime()) == 0) {
                        // 匹配成功
                        Course masterCourse = Course.builder().build();
                        BeanUtils.copyProperties(course, masterCourse);
                        masterCourse.setCourseStartDatetime(courseLocalDate.atTime(course.getStartTime()));
                        masterCourse.setCourseEndDatetime(courseLocalDate.atTime(course.getStartTime()).plusMinutes(course.getTimeCost()));
                        masterCourse.setIsCopy(true);
                        masterCourse.setCourseState(CourseStatus.READY.getStatus());
                        masterCourse.setFatherId(course.getId());
                        masterCourse.setStartDateList(null);
                        courseMapper.insert(masterCourse);

                        Course salveCourse = Course.builder().build();
                        BeanUtils.copyProperties(readyCourse, salveCourse);
                        salveCourse.setCourseStartDatetime(courseLocalDate.atTime(readyCourse.getStartTime()));
                        salveCourse.setCourseEndDatetime(courseLocalDate.atTime(readyCourse.getStartTime()).plusMinutes(readyCourse.getTimeCost()));
                        salveCourse.setIsCopy(true);
                        salveCourse.setCourseState(CourseStatus.READY.getStatus());
                        salveCourse.setFatherId(readyCourse.getId());
                        salveCourse.setStartDateList(null);
                        courseMapper.insert(salveCourse);

                        masterCourse.setMatchCourseId(salveCourse.getId());
                        salveCourse.setMatchCourseId(masterCourse.getId());
                        courseMapper.updateById(masterCourse);
                        courseMapper.updateById(salveCourse);
                        //  发送邮件提醒
                        emailService.sendCourseMatchedEmail(userInfoMapper.selectById(masterCourse.getUserId()), masterCourse);
                        emailService.sendCourseMatchedEmail(userInfoMapper.selectById(salveCourse.getUserId()), salveCourse);
                        // 关闭不循环匹配的课程
                        if (!course.getIsWeek()) {
                            course.setState(false);
                            courseMapper.updateById(course);
                        } else {
                            List<LocalDate> copyDateList = JSONObject.parseArray(course.getStartDateList(), LocalDate.class);
                            copyDateList.remove(courseLocalDate);
                            course.setStartDateList(JSONObject.toJSONString(copyDateList));
                            courseMapper.updateById(course);
                        }
                        if (!readyCourse.getIsWeek()) {
                            readyCourse.setState(false);
                            courseMapper.updateById(readyCourse);
                        } else {
                            List<LocalDate> copyDateList = JSONObject.parseArray(readyCourse.getStartDateList(), LocalDate.class);
                            copyDateList.remove(courseLocalDate);
                            readyCourse.setStartDateList(JSONObject.toJSONString(copyDateList));
                            courseMapper.updateById(readyCourse);
                        }
                        break;
                    }
                }
            }

        });

    }

    @Override
    public void setFeedback(CourseCallBackRequest request) {
        if (Strings.isNullOrEmpty(request.getCallBack())) {
            throw new BusinessException(500, "feedback is null");
        }
        Course course = courseMapper.selectById(request.getCourseId());
        if (Objects.isNull(course)) {
            throw new BusinessException(500, "course not exist");
        }
        UserInfo teacher = userInfoMapper.selectById(request.getUserId());
        if (!RoleType.TEACHER.getRole().toUpperCase().equals(course.getUserRole().toUpperCase()) || !teacher.getId().equals(course.getUserId())) {
            throw new BusinessException(403, "Insufficient permissions");
        }
        if (course.getCourseEndDatetime().isAfter(LocalDateTime.now())) {
            throw new BusinessException(400, "Course is not over");
        }
        Course matchCourse = courseMapper.selectById(course.getMatchCourseId());
        course.setCallBack(request.getCallBack());
        course.setIsCallBack(true);
        matchCourse.setCallBack(request.getCallBack());
        matchCourse.setIsCallBack(true);
        courseMapper.updateById(course);
        courseMapper.updateById(matchCourse);
    }

    @Override
    public void changeCourseStatus(Long courseId, CourseStatus status) {
        Course course = courseMapper.selectById(courseId);
        if (Objects.nonNull(course)) {
            course.setCourseState(status.getStatus());
        } else {
            throw new BusinessException(100, "not exist");
        }
    }

    @Override
    public void confirmCourse(Long courseId) {
        Course course = courseMapper.selectById(courseId);
        Course otherCourse = courseMapper.selectById(course.getMatchCourseId());
        if (CourseStatus.CLOSE.getStatus().equals(course.getCourseState())) {
            throw new BusinessException(400, "Course closed");
        }
        if (course.getCourseEndDatetime().isAfter(LocalDateTime.now())) {
            throw new BusinessException(400, "Course is not over");
        }
        if (RoleType.STUDENT.getRole().toUpperCase().equals(course.getUserRole().toUpperCase())) {
            course.setStudentConfirm(true);
            otherCourse.setStudentConfirm(true);
        } else {
            course.setTeacherConfirm(true);
            otherCourse.setTeacherConfirm(true);
        }
        if (course.getStudentConfirm() && course.getTeacherConfirm()) {
            course.setCourseState(CourseStatus.CLOSE.getStatus());
            otherCourse.setCourseState(CourseStatus.CLOSE.getStatus());
            UserInfo courseUser = userInfoMapper.selectById(course.getUserId());
            courseUser.setCourseHour(courseUser.getCourseHour() + course.getTimeCost());
            UserInfo otherCourseUser = userInfoMapper.selectById(otherCourse.getUserId());
            otherCourseUser.setCourseHour(otherCourseUser.getCourseHour() + otherCourse.getTimeCost());

            userInfoMapper.updateById(courseUser);
            userInfoMapper.updateById(otherCourseUser);
        }
        courseMapper.updateById(otherCourse);
        courseMapper.updateById(course);
    }

    @Override
    public void callBackTask() {
        List<Course> courseMatchedList = courseMapper.selectList(new QueryWrapper<Course>().lambda()
                .eq(Course::getState, true)
                .ne(Course::getCallBack, null)
                .eq(Course::getUserRole, RoleType.TEACHER.getRole())
                .ge(Course::getCourseEndDatetime, LocalDateTime.now()));

        courseMatchedList.forEach(courseMatched -> {
            UserInfo teacher = userInfoMapper.selectById(courseMatched.getUserId());
            emailService.sendCourseCallBackEmail(teacher, courseMatched);
        });
    }

    @Override
    public void remindTask() {
        // 一天前
        List<Course> courseMatchedOneDayList = courseMapper.selectList(new QueryWrapper<Course>().lambda()
                .eq(Course::getState, true)
                .le(Course::getCourseStartDatetime, LocalDateTime.now().plusDays(1).plusSeconds(30))
                .ge(Course::getCourseStartDatetime, LocalDateTime.now().plusDays(1).minusSeconds(30)));

        if (!courseMatchedOneDayList.isEmpty()) {
            log.info("找到了" + courseMatchedOneDayList.size() + "门课，即将在1天后开课");
            log.info(courseMatchedOneDayList.stream().map(Course::getCourseName).collect(Collectors.toList()).toString());
            Map<Long, UserInfo> userMap = getUserInfoMap(courseMatchedOneDayList);
            courseMatchedOneDayList.forEach(courseMatched -> {
                emailService.sendCourseRemindEmail(userMap.get(courseMatched.getUserId()), courseMatched, "24h");
            });
        }


        // 15分钟前
        List<Course> courseMatchedMinList = courseMapper.selectList(new QueryWrapper<Course>().lambda()
                .eq(Course::getState, 1)
                .le(Course::getCourseStartDatetime, LocalDateTime.now().plusMinutes(15).plusSeconds(30))
                .ge(Course::getCourseStartDatetime, LocalDateTime.now().plusMinutes(15).minusSeconds(30)));
        if (!courseMatchedMinList.isEmpty()) {
            log.info("找到了" + courseMatchedMinList.size() + "门课，即将在15分钟后开课");
            log.info(courseMatchedMinList.stream().map(Course::getCourseName).collect(Collectors.toList()).toString());
            Map<Long, UserInfo> userMap = getUserInfoMap(courseMatchedMinList);
            courseMatchedMinList.forEach(courseMatched -> {
                emailService.sendCourseRemindEmail(userMap.get(courseMatched.getUserId()), courseMatched, "15min");
            });
        }
        // 课程结束时
        List<Course> courseMatchedEndList = courseMapper.selectList(new QueryWrapper<Course>().lambda()
                .eq(Course::getState, true)
                .le(Course::getCourseEndDatetime, LocalDateTime.now().plusSeconds(30))
                .ge(Course::getCourseEndDatetime, LocalDateTime.now().minusSeconds(30)));
        if (!courseMatchedEndList.isEmpty()) {
            log.info("找到了" + courseMatchedEndList.size() + "门课，已经结束");
            log.info(courseMatchedEndList.stream().map(Course::getCourseName).collect(Collectors.toList()).toString());
            Map<Long, UserInfo> userMap = getUserInfoMap(courseMatchedEndList);

            courseMatchedEndList.forEach(courseMatched -> {
                courseMatched.setCourseState(CourseStatus.UNCONFIRMED.getStatus());
                emailService.sendCourseCallBackEmail(userMap.get(courseMatched.getUserId()), courseMatched);
                emailService.sendCourseEndCheckEmail(userMap.get(courseMatched.getUserId()), courseMatched);
                courseMapper.updateById(courseMatched);
            });
        }
    }

    private Map<Long, UserInfo> getUserInfoMap(List<Course> courseList) {
        List<Long> userIds = courseList.stream().map(Course::getUserId).collect(Collectors.toList());
        return userInfoMapper.selectBatchIds(userIds).stream().collect(Collectors.toMap(UserInfo::getId, Function.identity()));
    }
}
