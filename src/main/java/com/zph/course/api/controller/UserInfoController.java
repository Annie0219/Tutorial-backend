package com.zph.course.api.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zph.course.api.annotation.Session;
import com.zph.course.biz.service.user.impl.UserInfoServiceImpl;
import com.zph.course.common.constant.CourseConstant;
import com.zph.course.common.request.LoginRequest;
import com.zph.course.common.request.RegisterRequest;
import com.zph.course.common.vo.UserInfoVO;
import com.zph.course.data.entity.UserInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Objects;

@Controller
@RequestMapping("/user")
public class UserInfoController {
    @Resource
    private UserInfoServiceImpl userInfoService;

    @PostMapping("/login")
    public ResponseEntity<UserInfoVO> login(@RequestBody LoginRequest request, HttpServletRequest httpServletRequest) {
        UserInfo userInfo = userInfoService.login(request.getEmail(), request.getPassword());
        UserInfoVO userInfoVO = UserInfoVO.builder().build();
        if (Objects.isNull(userInfo)) {
            userInfoVO.setResult(false);
            return ResponseEntity.ok(userInfoVO);
        }
        HttpSession session = httpServletRequest.getSession(true);

        session.invalidate();
        BeanUtils.copyProperties(userInfo, userInfoVO);
        userInfoVO.setResult(true);
        httpServletRequest.getSession(true).setAttribute(CourseConstant.SESSION_KEY, userInfoVO);

        userInfo = userInfoService.getOne(new QueryWrapper<UserInfo>().lambda().eq(UserInfo::getEmail, userInfo.getEmail()), false);
        userInfo.setLastLogin(LocalDateTime.now());
        userInfoService.saveOrUpdate(userInfo);

        return ResponseEntity.ok(userInfoVO);
    }

    @GetMapping("/logout")
    @Session
    public ResponseEntity<HttpStatus> logout(HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession(false).removeAttribute(CourseConstant.SESSION_KEY);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/sign")
    public ResponseEntity<HttpStatus> register(@RequestBody RegisterRequest request) {
        userInfoService.register(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Session
    @GetMapping("/info")
    public ResponseEntity<UserInfoVO> getUserInfo(HttpServletRequest httpServletRequest) {
        UserInfoVO userInfoVO = userInfoService.getInfo(httpServletRequest);

        if (Objects.nonNull(userInfoVO)) {
            UserInfo user = userInfoService.getOne(new QueryWrapper<UserInfo>().lambda().eq(UserInfo::getEmail, userInfoVO.getEmail()), false);
            BeanUtils.copyProperties(user, userInfoVO);
            return ResponseEntity.ok(userInfoVO);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
