package com.zph.course.biz.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zph.course.common.request.RegisterRequest;
import com.zph.course.common.vo.UserInfoVO;
import com.zph.course.data.entity.UserInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author evan
 * @since 2020-04-07
 */
public interface IUserInfoService extends IService<UserInfo> {
    UserInfo login(String email, String password);

    UserInfoVO register(RegisterRequest request);

    UserInfoVO getInfo(HttpServletRequest httpServletRequest);

}
