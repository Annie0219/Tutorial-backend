package com.zph.course.api.controller;


import com.google.common.base.Strings;
import com.zph.course.api.annotation.Session;
import com.zph.course.biz.service.course.impl.CourseMatchedServiceImpl;
import com.zph.course.biz.service.user.impl.UserInfoServiceImpl;
import com.zph.course.common.enumation.CourseStatus;
import com.zph.course.common.framework.exception.BusinessException;
import com.zph.course.common.request.PageRequest;
import com.zph.course.common.vo.CourseMatchedVO;
import com.zph.course.common.vo.PageResultVO;
import com.zph.course.common.vo.UserInfoVO;
import com.zph.course.data.entity.CourseMatched;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Objects;

@Controller
@RequestMapping("/course/matched")
public class CourseMatchedController {
    @Resource
    private UserInfoServiceImpl userInfoService;
    @Resource
    private CourseMatchedServiceImpl matchedService;

    @PostMapping("/list")
    @Session
    public ResponseEntity<PageResultVO> getMatchedCourseList(HttpServletRequest httpServletRequest,
                                                             @RequestBody PageRequest request) {
        UserInfoVO userInfoVO = userInfoService.getInfo(httpServletRequest);
        request.setUserId(userInfoVO.getId());
        PageResultVO pageResultVO = matchedService.getMatchedCourse(request);
        return ResponseEntity.ok(pageResultVO);
    }

    @GetMapping("/detail/{id}")
    @Session
    public ResponseEntity<CourseMatchedVO> getMatchedCourseDetail(HttpServletRequest httpServletRequest,
                                                                  @PathVariable Long id) {
        UserInfoVO userInfoVO = userInfoService.getInfo(httpServletRequest);
        CourseMatchedVO courseMatchedVO = matchedService.getCourseMatch(id, userInfoVO.getId());
        return ResponseEntity.ok(courseMatchedVO);
    }

    @PostMapping("/callback")
    @Session
    public ResponseEntity<HttpStatus> setCourseCallBack(HttpServletRequest httpServletRequest,
                                                        @RequestBody CourseMatchedVO callBack) {
        UserInfoVO userInfoVO = userInfoService.getInfo(httpServletRequest);
        if (Strings.isNullOrEmpty(callBack.getCallBack()) || Objects.isNull(callBack.getId())) {
            throw new BusinessException(400, "Insufficient parameters");
        }
        CourseMatched courseMatched = matchedService.getById(callBack.getId());
        if (!userInfoVO.getId().equals(courseMatched.getTeacherId())) {
            throw new BusinessException(403, "Insufficient permissions");
        }
        if (courseMatched.getEndTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException(400, "Course is not over");
        }
        courseMatched.setCallBack(callBack.getCallBack());
        courseMatched.setCourseState(CourseStatus.CLOSE.getStatus());
        matchedService.saveOrUpdate(courseMatched);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/confirm/{id}")
    @Session
    public ResponseEntity<HttpStatus> setCourseConfirm(HttpServletRequest httpServletRequest,
                                                       @PathVariable Long id) {
        UserInfoVO userInfoVO = userInfoService.getInfo(httpServletRequest);
        if (Objects.isNull(id)) {
            throw new BusinessException(400, "Insufficient parameters");
        }
        matchedService.updateMatchedStates(id, userInfoVO.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
