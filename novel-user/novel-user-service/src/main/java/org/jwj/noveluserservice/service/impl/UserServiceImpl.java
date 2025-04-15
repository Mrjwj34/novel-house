package org.jwj.noveluserservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.jwj.novelcommon.auth.JwtUtils;
import org.jwj.novelcommon.constants.CommonConsts;
import org.jwj.novelcommon.constants.DatabaseConsts;
import org.jwj.novelcommon.constants.ErrorCodeEnum;
import org.jwj.novelcommon.constants.SystemConfigConsts;
import org.jwj.novelcommon.resp.RestResp;
import org.jwj.novelconfig.exception.BusinessException;
import org.jwj.noveluserapi.dto.UserInfoDto;
import org.jwj.noveluserapi.dto.req.UserInfoUptReqDto;
import org.jwj.noveluserapi.dto.req.UserLoginReqDto;
import org.jwj.noveluserapi.dto.req.UserRegisterReqDto;
import org.jwj.noveluserapi.dto.resp.UserInfoRespDto;
import org.jwj.noveluserapi.dto.resp.UserLoginRespDto;
import org.jwj.noveluserapi.dto.resp.UserRegisterRespDto;
import org.jwj.noveluserservice.dao.entity.UserBookshelf;
import org.jwj.noveluserservice.dao.entity.UserFeedback;
import org.jwj.noveluserservice.dao.entity.UserInfo;
import org.jwj.noveluserservice.dao.mapper.UserBookshelfMapper;
import org.jwj.noveluserservice.dao.mapper.UserFeedbackMapper;
import org.jwj.noveluserservice.dao.mapper.UserInfoMapper;
import org.jwj.noveluserservice.manager.redis.VerifyCodeManager;
import org.jwj.noveluserservice.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserInfoMapper userInfoMapper;

    private final VerifyCodeManager verifyCodeManager;

    private final UserFeedbackMapper userFeedbackMapper;

    private final UserBookshelfMapper userBookshelfMapper;
    private final PasswordEncoder passwordEncoder;
    @Override
    public RestResp<UserRegisterRespDto> register(UserRegisterReqDto dto) {
        // 校验图形验证码是否正确
        boolean isValidCode = verifyCodeManager.imgVerifyCodeOk(dto.getSessionId(), dto.getVelCode());
        if(!isValidCode) {
            // 图形验证码校验失败
            throw new BusinessException(ErrorCodeEnum.USER_VERIFY_CODE_ERROR);
        }
        // 校验手机号是否已经注册
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getUsername, dto.getUsername())
                .last(DatabaseConsts.SqlEnum.LIMIT_1.getSql());
        if(userInfoMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ErrorCodeEnum.USER_NAME_EXIST);
        }
        // 注册成功, 保存用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setPassword(
                passwordEncoder.encode(dto.getPassword()));
        userInfo.setUsername(dto.getUsername());
        userInfo.setNickName(dto.getUsername());
        userInfo.setCreateTime(LocalDateTime.now());
        userInfo.setUpdateTime(LocalDateTime.now());
        userInfo.setSalt("0"); // 无效字段
        userInfoMapper.insert(userInfo);
        // 删除验证码
        verifyCodeManager.removeImgVerifyCode(dto.getSessionId());
        // 生成包含jwt并返回给前端数据
        return RestResp.ok(
                UserRegisterRespDto.builder()
                        .token(JwtUtils.generateToken(userInfo.getId(), SystemConfigConsts.NOVEL_FRONT_KEY))
                        .uid(userInfo.getId())
                        .build()
        );
    }

    @Override
    public RestResp<UserLoginRespDto> login(UserLoginReqDto dto) {
        // 查询用户信息
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getUsername, dto.getUsername())
                .last(DatabaseConsts.SqlEnum.LIMIT_1.getSql());
        // 用户不存在
        UserInfo userInfo = userInfoMapper.selectOne(wrapper);
        if(Objects.isNull(userInfo)) {
            throw new BusinessException(ErrorCodeEnum.USER_ACCOUNT_NOT_EXIST);
        }
        // 判断密码是否正确
        if(!passwordEncoder.matches(dto.getPassword(), userInfo.getPassword())) {
            throw new BusinessException(ErrorCodeEnum.USER_PASSWORD_ERROR);
        }
        // 登录成功返回jwt令牌
        return RestResp.ok(
                UserLoginRespDto.builder()
                        .token(JwtUtils.generateToken(userInfo.getId(), SystemConfigConsts.NOVEL_FRONT_KEY))
                        .uid(userInfo.getId())
                        .nickName(userInfo.getNickName())
                        .build()
        );
    }

    @Override
    public RestResp<UserInfoRespDto> getUserInfo(Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        return RestResp.ok(UserInfoRespDto.builder()
                .nickName(userInfo.getNickName())
                .userSex(userInfo.getUserSex())
                .userPhoto(userInfo.getUserPhoto())
                .build());
    }

    @Override
    public RestResp<Void> updateUserInfo(UserInfoUptReqDto dto) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(dto.getUserId());
        userInfo.setNickName(dto.getNickName());
        userInfo.setUserPhoto(dto.getUserPhoto());
        userInfo.setUserSex(dto.getUserSex());
        userInfoMapper.updateById(userInfo);
        return RestResp.ok();
    }

    @Override
    public RestResp<Void> saveFeedback(Long userId, String content) {
        UserFeedback userFeedback = new UserFeedback();
        userFeedback.setUserId(userId);
        userFeedback.setContent(content);
        userFeedback.setCreateTime(LocalDateTime.now());
        userFeedback.setUpdateTime(LocalDateTime.now());
        userFeedbackMapper.insert(userFeedback);
        return RestResp.ok();
    }

    @Override
    public RestResp<Void> deleteFeedback(Long userId, Long id) {
        LambdaQueryWrapper<UserFeedback> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFeedback::getId, id)// 出于安全考虑，删除反馈时需要校验用户id
                .eq(UserFeedback::getUserId, userId);
        userFeedbackMapper.delete(queryWrapper);
        return RestResp.ok();
    }

    @Override
    public RestResp<Integer> getBookshelfStatus(Long userId, String bookId) {
        LambdaQueryWrapper<UserBookshelf> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserBookshelf::getUserId, userId)
                .eq(UserBookshelf::getBookId, bookId);
        return RestResp.ok(
                userBookshelfMapper.selectCount(queryWrapper) > 0
                        ? CommonConsts.YES
                        : CommonConsts.NO
        );
    }
}
