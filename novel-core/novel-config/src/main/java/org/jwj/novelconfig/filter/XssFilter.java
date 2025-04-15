package org.jwj.novelconfig.filter;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jwj.novelconfig.XssProperties;
import org.jwj.novelconfig.wrapper.XssHttpServletRequestWrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
@ConditionalOnProperty(value = "novel.xss.enabled", havingValue = "true") // 通过配置文件控制是否启用
@WebFilter(urlPatterns = "/*", filterName = "xssFilter") // 指定过滤器的URL模式和名称
@EnableConfigurationProperties(value = {XssProperties.class}) // 启用配置属性类
@RequiredArgsConstructor
public class XssFilter implements Filter {
    private final XssProperties xssProperties;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if (handleExcludeUrl(request)) {
            filterChain.doFilter(servletRequest, servletResponse);// 校验是否属于放行路径
            return;
        }
        // 使用Xss包装器处理请求参数
        XssHttpServletRequestWrapper xssHttpServletRequestWrapper = new XssHttpServletRequestWrapper(request);
        filterChain.doFilter(xssHttpServletRequestWrapper, servletResponse);// 继续过滤器链
    }

    private boolean handleExcludeUrl(HttpServletRequest request) {
        List<String> excludes = xssProperties.excludes();
        if (CollectionUtils.isEmpty(excludes)) {
            return false;// 没有配置放行路径返回false
        }
        String servletPath = request.getServletPath();
        for (String exclude : excludes) {
            Pattern compile = Pattern.compile("^" + exclude);
            if (compile.matcher(servletPath).matches()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
