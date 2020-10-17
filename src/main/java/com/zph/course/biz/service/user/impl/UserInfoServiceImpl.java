package com.zph.course.biz.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zph.course.biz.service.email.impl.EmailServiceImpl;
import com.zph.course.biz.service.user.IUserInfoService;
import com.zph.course.common.constant.AvatarUrl;
import com.zph.course.common.constant.CourseConstant;
import com.zph.course.common.enumation.RoleType;
import com.zph.course.common.framework.exception.BusinessException;
import com.zph.course.common.request.RegisterRequest;
import com.zph.course.common.vo.UserInfoVO;
import com.zph.course.data.entity.UserInfo;
import com.zph.course.data.mapper.UserInfoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Random;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author evan
 * @since 2020-04-07
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private EmailServiceImpl emailService;

    @Override
    public UserInfo login(String email, String password) {
        return userInfoMapper.selectOne(new QueryWrapper<UserInfo>().lambda()
                .eq(UserInfo::getEmail, email)
                .eq(UserInfo::getPassword, password));
    }

    @Override
    public UserInfoVO register(RegisterRequest request) {
        if (Objects.isNull(request.getEmail())
                || Objects.isNull(request.getName())
                || Objects.isNull(request.getGender())
                || Objects.isNull(request.getPassword())
                || Objects.isNull(request.getRole())
                || Objects.isNull(request.getVerification())) {
            throw new BusinessException(500, "Missing parameter");
        }
        if (emailService.checkEmail(request.getEmail(), request.getVerification())) {
            UserInfo userInfo = userInfoMapper.selectOne(new QueryWrapper<UserInfo>().lambda().eq(UserInfo::getEmail, request.getEmail()));
            if (Objects.nonNull(userInfo)) {
                throw new BusinessException(500, "This mailbox is already used");
            }

            UserInfo user = UserInfo.builder()
                    .email(request.getEmail())
                    .password(request.getPassword())
                    .name(request.getName())
                    .gender(request.getGender())
                    .role(request.getRole())
                    .build();
            if (RoleType.GUARDIAN.getRole().toLowerCase().equals(user.getRole().toLowerCase())) {
                user.setRole(RoleType.STUDENT.getRole());
                user.setIsGuardian(true);
            }
            String[] sm = {"m1", "m2", "m3", "m4", "m5", "m6", "m7"};
            String[] sw = {"w1", "w2", "w3", "w4", "w5", "w6"};
            String tm = "tm1";
            String tw = "tw1";

            if ("Male".equals(user.getGender())) {
                if (RoleType.STUDENT.getRole().toLowerCase().equals(user.getRole().toLowerCase())) {
                    user.setAvatar(String.format(AvatarUrl.imageUrl, sm[new Random().nextInt(7) + 1]));
                } else if (RoleType.TEACHER.getRole().toLowerCase().equals(user.getRole().toLowerCase())) {
                    user.setAvatar(String.format(AvatarUrl.imageUrl, tm));
                }
            } else if ("Female".equals(user.getGender())) {
                if (RoleType.STUDENT.getRole().toLowerCase().equals(user.getRole().toLowerCase())) {
                    user.setAvatar(String.format(AvatarUrl.imageUrl, sw[new Random().nextInt(7) + 1]));
                } else if (RoleType.TEACHER.getRole().toLowerCase().equals(user.getRole().toLowerCase())) {
                    user.setAvatar(String.format(AvatarUrl.imageUrl, tw));
                }
            }
            userInfoMapper.insert(user);

            return UserInfoVO.builder()
                    .email(request.getEmail())
                    .gender(request.getGender())
                    .role(request.getRole())
                    .name(request.getName())
                    .build();
        } else {
            throw new BusinessException(111, "Verification Code Error");
        }
    }

    @Override
    public UserInfoVO getInfo(HttpServletRequest httpServletRequest) {
        UserInfoVO userInfoVO = (UserInfoVO) httpServletRequest.getSession(false).getAttribute(CourseConstant.SESSION_KEY);
        if (Objects.isNull(userInfoVO)) {
            throw new BusinessException(403, "Insufficient permissions, Please log in");
        }
        return userInfoVO;
    }
}
