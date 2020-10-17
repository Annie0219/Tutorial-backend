package com.zph.course.biz.service.ad.impl;

import com.zph.course.data.entity.AdCount;
import com.zph.course.data.mapper.AdCountMapper;
import com.zph.course.biz.service.ad.IAdCountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class AdCountServiceImpl extends ServiceImpl<AdCountMapper, AdCount> implements IAdCountService {

}
