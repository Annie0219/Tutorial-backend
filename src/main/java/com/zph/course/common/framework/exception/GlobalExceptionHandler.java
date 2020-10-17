package com.zph.course.common.framework.exception;

import com.zph.course.data.result.BusinessResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author: liuzhj
 * @date: 2018/12/15
 * @time: 11:06
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final int UNAUTH_STATUS_CODE = 401;

    @ExceptionHandler({Exception.class})
    public ResponseEntity interceptException(Exception e) {
        if (e instanceof BusinessException) {
            BusinessException be = (BusinessException) e;
            BusinessResult ret = new BusinessResult(be.getCode(), be.getErrMsg());

            // Unauthorized
            if (be.getCode().compareTo(UNAUTH_STATUS_CODE) == 0) {
                return new ResponseEntity(ret, HttpStatus.UNAUTHORIZED);
            }

            log.error("Exception occurred:", be);
            return new ResponseEntity(ret, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            log.error("Exception occurred:", e);
            BusinessResult ret = new BusinessResult(BusinessException.UNKNOWN_ERR_CODE, BusinessException.UNKNOWN_ERR_MSG);
            return new ResponseEntity(ret, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}