package com.zph.course.api.controller;


import com.zph.course.biz.service.email.impl.EmailServiceImpl;
import com.zph.course.common.request.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
@RequestMapping("/email")
public class EmailController {

    @Resource
    private EmailServiceImpl emailService;

    @PostMapping("/verify")
    public ResponseEntity sendVerification(@RequestBody RegisterRequest request) {
        emailService.sendCheckEmail(request.getName(), request.getEmail());
        return new ResponseEntity(HttpStatus.OK);
    }

}
