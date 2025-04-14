package org.jwj.novelconfig.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 分布式锁-Key 注解
 * @author jwj44
 */
@Documented
@Retention(RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface Key {
    /**
     * 表达式
     */
    String expr() default "";
}
