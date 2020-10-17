package com.zph.course.config.task;

import com.zph.course.biz.service.course.impl.CourseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class TestTask {

    @Resource
    private CourseServiceImpl courseMatchedService;

    @Scheduled(cron = "0 0 0 1 * *")
    public void callbackJob() {
        courseMatchedService.callBackTask();
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void remindJob() {
        courseMatchedService.remindTask();
    }
}
