package org.jwj.novelconfig.annotation;


import org.jwj.novelcommon.constants.ErrorCodeEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 分布式锁 注解
 * @author jwj44
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface Lock {
    /**
     * 锁的前缀
     */
    String prefix();
    /**
     * 是否等待锁
     */
    boolean isWait() default false;
    /**
     * 等待锁的时间
     */
    long waitTime() default 3L;
    /**
     * 锁失败的返回码
     */
    ErrorCodeEnum failCode() default ErrorCodeEnum.OK;
}
