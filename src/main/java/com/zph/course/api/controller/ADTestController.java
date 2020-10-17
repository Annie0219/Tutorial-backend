package com.zph.course.api.controller;

import com.zph.course.biz.service.ad.AiService;
import com.zph.course.biz.service.ad.GlobalResult;
import com.zph.course.biz.service.ad.impl.AdCountServiceImpl;
import com.zph.course.data.entity.AdCount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("v1/ad")
public class ADTestController {
    @Resource
    private AiService aiService;
    @Resource
    private AdCountServiceImpl adCountService;

    @PostMapping("speech/file")
    @ResponseBody
    public GlobalResult upload(@RequestParam(value = "file") String base64String) {
        return aiService.getSpeechByBase64(base64String);
    }

    @PostMapping("test")
    @ResponseBody
    public GlobalResult adTest(@RequestParam(value = "text") String text) {
        return aiService.aDPrediction(text);
    }

    @GetMapping("num")
    @ResponseBody
    public GlobalResult addVisitor() {
        AdCount num = adCountService.getById(1);
        num.setUserNum(num.getUserNum() + 1);
        adCountService.saveOrUpdate(num);
        return GlobalResult.builder().status(200).data(num.getUserNum()).build();
    }
}
