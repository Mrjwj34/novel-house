package org.jwj.noveluserservice.config;

import lombok.RequiredArgsConstructor;
import org.jwj.novelcommon.constants.ApiRouterConsts;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;
    // 添加jwt校验拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns(ApiRouterConsts.API_FRONT_USER_URL_PREFIX + "/**")
                .excludePathPatterns(ApiRouterConsts.API_FRONT_USER_URL_PREFIX + "/register",
                        ApiRouterConsts.API_ADMIN_URL_PREFIX + "/login")
                .order(2);
    }
}
