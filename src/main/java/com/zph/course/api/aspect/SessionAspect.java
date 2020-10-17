package com.zph.course.api.aspect;

import com.zph.course.common.constant.CourseConstant;
import com.zph.course.common.framework.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@Aspect
@Component
@Slf4j
public class SessionAspect {


    @Order(1)
    @Around("@annotation(com.zph.course.api.annotation.Session)")
    public Object checkSession(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        Object hsrObj = args[0];
        if (!(hsrObj instanceof HttpServletRequest)) {
            log.error("Annotated method doesn't have HttpServletRequest for first param!!");
            throw new BusinessException(500, "Network error!");
        }
        HttpServletRequest httpServletRequest = (HttpServletRequest) hsrObj;

        HttpSession session = httpServletRequest.getSession(false);
        if (null == session) {
            throw new BusinessException(401, "unthorized!!");
        }
        if (null == session.getAttribute(CourseConstant.SESSION_KEY)) {
            throw new BusinessException(401, "unthorized!!");
        }
        return pjp.proceed();
    }
}
