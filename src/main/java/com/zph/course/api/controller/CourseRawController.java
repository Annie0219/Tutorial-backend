package com.zph.course.api.controller;


import com.zph.course.api.annotation.Session;
import com.zph.course.biz.service.course.impl.CourseRawServiceImpl;
import com.zph.course.biz.service.user.impl.UserInfoServiceImpl;
import com.zph.course.common.constant.CourseConstant;
import com.zph.course.common.framework.exception.BusinessException;
import com.zph.course.common.request.PageRequest;
import com.zph.course.common.request.RawCourseRequest;
import com.zph.course.common.request.RegisterRequest;
import com.zph.course.common.vo.PageResultVO;
import com.zph.course.common.vo.UserInfoVO;
import com.zph.course.data.entity.UserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;


@Controller
@RequestMapping("/course/raw")
public class CourseRawController {

    @Resource
    private CourseRawServiceImpl courseRawService;
    @Resource
    private UserInfoServiceImpl userInfoService;

    @PostMapping("/post")
    @Session
    public ResponseEntity<HttpStatus> saveRawCourse(HttpServletRequest httpServletRequest,
                                                    @RequestBody RawCourseRequest request) {
        UserInfoVO userInfoVO = userInfoService.getInfo(httpServletRequest);
        if (Objects.isNull(userInfoVO)) {
            throw new BusinessException(403, "Insufficient permissions, Please log in");
        }
        request.setUserId(userInfoVO.getId());
        courseRawService.createRawCourse(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/list")
    @Session
    public ResponseEntity<PageResultVO> getReaCourseList(HttpServletRequest httpServletRequest, @RequestBody PageRequest request) {

        UserInfoVO userInfoVO = userInfoService.getInfo(httpServletRequest);
        if (Objects.isNull(userInfoVO)) {
            throw new BusinessException(403, "Insufficient permissions, Please log in");
        }
        request.setUserId(userInfoVO.getId());
        PageResultVO pageResultVO = courseRawService.getRawCourseList(request);
        return ResponseEntity.ok(pageResultVO);
    }

}
