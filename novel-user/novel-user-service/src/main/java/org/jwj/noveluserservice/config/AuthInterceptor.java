package org.jwj.noveluserservice.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jwj.novelcommon.auth.JwtUtils;
import org.jwj.novelcommon.auth.UserHolder;
import org.jwj.novelcommon.constants.ErrorCodeEnum;
import org.jwj.novelcommon.constants.SystemConfigConsts;
import org.jwj.novelconfig.exception.BusinessException;
import org.jwj.noveluserapi.dto.UserInfoDto;
import org.jwj.noveluserservice.manager.cache.UserInfoCacheManager;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
//    private final ObjectMapper objectMapper;
    private final UserInfoCacheManager userInfoCacheManager;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        // 1.获取jwt令牌
        String token = request.getHeader(SystemConfigConsts.HTTP_AUTH_HEADER_NAME);
        // 1.1令牌不存在
        if(!StringUtils.hasText(token)) {
            throw new BusinessException(ErrorCodeEnum.USER_LOGIN_EXPIRED);
        }
        // 2.解析userid
        Long uid = JwtUtils.paraseToken(token, SystemConfigConsts.NOVEL_FRONT_KEY);
        // 2.1解析失败
        if(Objects.isNull(uid)) {
            throw new BusinessException(ErrorCodeEnum.USER_LOGIN_EXPIRED);
        }
        // 3.获取用户
        UserInfoDto user = userInfoCacheManager.getUser(uid);
        // 3.1用户不存在
        if(Objects.isNull(user)) {
            throw new BusinessException(ErrorCodeEnum.USER_ACCOUNT_NOT_EXIST);
        }
        // 设置用户id到线程
        UserHolder.setUserId(uid);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }


    @SuppressWarnings("NullableProblems")
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.clear();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
