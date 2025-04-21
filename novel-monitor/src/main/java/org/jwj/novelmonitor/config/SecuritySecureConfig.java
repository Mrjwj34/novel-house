package org.jwj.novelmonitor.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties; // 引入 Admin Server 的配置属性
import jakarta.servlet.DispatcherType;
import org.springframework.boot.autoconfigure.security.SecurityProperties; // 引入 Spring Boot 默认的安全配置属性
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // Http 安全配置的核心构建器
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // 用户详情服务接口
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 密码加密器
import org.springframework.security.crypto.password.PasswordEncoder; // 密码加密器接口
import org.springframework.security.provisioning.InMemoryUserDetailsManager; // 内存中的用户详情管理器
import org.springframework.security.web.SecurityFilterChain; // 安全过滤器链
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler; // 登录成功处理器
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter; // HTTP Basic 认证过滤器
import org.springframework.security.web.csrf.CookieCsrfTokenRepository; // CSRF Token 仓库 (Cookie 方式)
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler; // CSRF Token 请求属性处理器
import org.springframework.security.web.util.matcher.AntPathRequestMatcher; // URL 路径匹配器

import java.util.UUID;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.POST;

@Configuration(proxyBeanMethods = false) // 声明这是一个配置类
public class SecuritySecureConfig {

    private final AdminServerProperties adminServer; // 注入 Admin Server 的配置
    private final SecurityProperties security;      // 注入 Spring Boot 的安全配置

    // 构造函数注入依赖
    public SecuritySecureConfig(AdminServerProperties adminServer, SecurityProperties security) {
        this.adminServer = adminServer;
        this.security = security;
    }

    // 定义核心的安全过滤器链 Bean
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 1. 配置登录成功后的处理器
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo"); // 允许通过请求参数指定登录后跳转的 URL
        successHandler.setDefaultTargetUrl(this.adminServer.path("/")); // 默认跳转到 Admin Server 的根路径

        // 2. 配置 HTTP 请求授权规则 (authorizeHttpRequests)
        http.authorizeHttpRequests((authorizeRequests) -> authorizeRequests
                        // a. 允许匿名访问 Admin Server 的静态资源 (CSS, JS 等)
                        .requestMatchers(new AntPathRequestMatcher(this.adminServer.path("/assets/**"))).permitAll()
                        // b. 允许匿名访问 Actuator 的 info 端点
                        .requestMatchers(new AntPathRequestMatcher(this.adminServer.path("/actuator/info"))).permitAll()
                        // c. 允许匿名访问 Actuator 的 health 端点 (监控通常需要)
                        .requestMatchers(new AntPathRequestMatcher(adminServer.path("/actuator/health"))).permitAll()
                        // d. 允许匿名访问登录页面
                        .requestMatchers(new AntPathRequestMatcher(this.adminServer.path("/login"))).permitAll()
                        // e. 允许异步调度请求 (解决特定场景下的问题)
                        .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
                        // f. 其他所有未明确允许的请求都需要认证 (登录)
                        .anyRequest().authenticated()
                )
                // 3. 配置表单登录 (formLogin)
                .formLogin((formLogin) -> formLogin
                        // 指定登录页面的 URL
                        .loginPage(this.adminServer.path("/login"))
                        // 指定登录成功后使用的处理器 (上面配置的 successHandler)
                        .successHandler(successHandler)
                )
                // 4. 配置登出 (logout)
                .logout((logout) -> logout.logoutUrl(this.adminServer.path("/logout"))) // 指定登出 URL
                // 5. 启用 HTTP Basic 认证 (允许通过 Authorization 头进行认证，常用于机器间调用)
                .httpBasic(Customizer.withDefaults());

        // 6. 添加自定义的 CSRF 过滤器 (CustomCsrfFilter)
        // 确保 XSRF-TOKEN Cookie 被正确设置
        http.addFilterAfter(new CustomCsrfFilter(), BasicAuthenticationFilter.class)
                // 7. 配置 CSRF 防护
                .csrf((csrf) -> csrf
                        // 使用 CookieCsrfTokenRepository 存储 CSRF Token
                        // withHttpOnlyFalse() 让前端 JS 可以读取 XSRF-TOKEN Cookie
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        // 使用默认的请求处理器
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                        // 忽略对特定路径的 CSRF 检查 (这些通常是 API 调用，而非浏览器表单提交)
                        .ignoringRequestMatchers(
                                // Agent 向 Server 注册实例
                                new AntPathRequestMatcher(this.adminServer.path("/instances"), POST.toString()),
                                // Agent 从 Server 注销或 Server 删除实例
                                new AntPathRequestMatcher(this.adminServer.path("/instances/*"), DELETE.toString()),
                                // 所有 Actuator 端点 (通常由 Admin Server 后端或监控系统调用)
                                new AntPathRequestMatcher(this.adminServer.path("/actuator/**"))
                        )
                );

        // 8. 配置 "记住我" 功能 (rememberMe)
        http.rememberMe((rememberMe) -> rememberMe
                // 设置一个密钥用于生成和验证 remember-me cookie (每次重启不同，仅适合开发)
                .key(UUID.randomUUID().toString())
                // 设置 remember-me cookie 的有效期 (这里是 14 天)
                .tokenValiditySeconds(1209600)
        );

        // 9. 构建并返回配置好的 SecurityFilterChain
        return http.build();
    }

    // 定义 UserDetailsService Bean，提供用户信息给 Spring Security
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        // 创建一个用户，硬编码用户名 "user"，密码 "password" (经过加密)，角色 "USER"
        UserDetails user = User.withUsername("user")
                .password(passwordEncoder.encode("password")) // 密码必须加密存储
                .roles("USER")
                .build();
        // 使用 InMemoryUserDetailsManager，用户信息存储在内存中 (仅适合演示/开发)
        // 生产环境应该替换为从数据库读取用户的实现
        return new InMemoryUserDetailsManager(user);
    }

    // 定义 PasswordEncoder Bean，指定密码加密方式
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用 BCrypt 算法，这是当前推荐的标准
        return new BCryptPasswordEncoder();
    }
}