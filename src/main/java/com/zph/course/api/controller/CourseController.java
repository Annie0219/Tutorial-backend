package com.zph.course.api.controller;


import com.google.common.base.Strings;
import com.zph.course.api.annotation.Session;
import com.zph.course.biz.service.course.impl.CourseServiceImpl;
import com.zph.course.biz.service.user.impl.UserInfoServiceImpl;
import com.zph.course.common.enumation.CourseStatus;
import com.zph.course.common.framework.exception.BusinessException;
import com.zph.course.common.request.CourseCallBackRequest;
import com.zph.course.common.request.CourseRequest;
import com.zph.course.common.request.PageRequest;
import com.zph.course.common.vo.CourseMatchedVO;
import com.zph.course.common.vo.CourseVO;
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

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author evan
 * @since 2020-05-18
 */
@Controller
@RequestMapping("/course")
public class CourseController {
    @Resource
    private UserInfoServiceImpl userInfoService;
    @Resource
    private CourseServiceImpl courseService;

    /**
     * {
     * "title":"test title",
     * "courseName":"Java",
     * "grade":"1",
     * "timeCost":60,
     * "week":["MONDAY","THURSDAY"
     * ],
     * "isWeek":true,
     * "weekStartDateTime":"2020-04-23T10:29:52.408Z",
     * "weekEndDateTime":"2020-05-16T10:29:53.488Z",
     * "startDateTime":""
     * }
     *
     * @param httpServletRequest
     * @param request
     * @return
     */
    @Session
    @PostMapping("/post")
    public ResponseEntity<HttpStatus> saveCourse(HttpServletRequest httpServletRequest,
                                                 @RequestBody CourseRequest request) {
        UserInfoVO userInfoVO = userInfoService.getInfo(httpServletRequest);
        if (Objects.isNull(userInfoVO)) {
            throw new BusinessException(403, "Insufficient permissions, Please log in");
        }
        request.setUserId(userInfoVO.getId());
        request.setUserRole(userInfoVO.getRole());
        courseService.createCourse(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Session
    @PostMapping("/list")
    public ResponseEntity<PageResultVO> courseList(HttpServletRequest httpServletRequest,
                                                   @RequestBody PageRequest request) {
        UserInfoVO userInfoVO = userInfoService.getInfo(httpServletRequest);
        if (Objects.isNull(userInfoVO)) {
            throw new BusinessException(403, "Insufficient permissions, Please log in");
        }
        request.setUserId(userInfoVO.getId());
        PageResultVO pageResultVO = courseService.getCourseInfoList(request);
        return ResponseEntity.ok(pageResultVO);
    }

    @GetMapping("/{id}")
    @Session
    public ResponseEntity<CourseVO> courseInfo(HttpServletRequest httpServletRequest,
                                               @PathVariable Long id) {
        UserInfoVO userInfoVO = userInfoService.getInfo(httpServletRequest);
        if (Objects.isNull(userInfoVO)) {
            throw new BusinessException(403, "Insufficient permissions, Please log in");
        }
        if (Objects.isNull(id)) {
            throw new BusinessException(400, "Insufficient parameters");
        }
        CourseVO courseVO = courseService.getCourseInfo(id);
        return ResponseEntity.ok(courseVO);
    }

    @Session
    @PostMapping("/callback")
    public ResponseEntity<HttpStatus> courseCallBack(HttpServletRequest httpServletRequest,
                                                     @RequestBody CourseCallBackRequest callBack) {
        UserInfoVO userInfoVO = userInfoService.getInfo(httpServletRequest);
        if (Objects.isNull(userInfoVO)) {
            throw new BusinessException(403, "Insufficient permissions, Please log in");
        }
        if (Strings.isNullOrEmpty(callBack.getCallBack()) || Objects.isNull(callBack.getCourseId())) {
            throw new BusinessException(400, "Insufficient parameters");
        }
        callBack.setUserId(userInfoVO.getId());
        courseService.setFeedback(callBack);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/confirm/{id}")
    public ResponseEntity<HttpStatus> courseConfirm(@PathVariable Long id) {
        if (Objects.isNull(id)) {
            throw new BusinessException(400, "Insufficient parameters");
        }
        courseService.confirmCourse(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
