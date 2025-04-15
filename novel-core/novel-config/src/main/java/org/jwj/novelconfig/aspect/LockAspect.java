package org.jwj.novelconfig.aspect;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jwj.novelconfig.exception.BusinessException;
import org.jwj.novelconfig.annotation.Key;
import org.jwj.novelconfig.annotation.Lock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁切面
 * @author jwj44
 */
@Aspect
@Component
@RequiredArgsConstructor
public class LockAspect {
    private final RedissonClient redissonClient;
    private static final String KEY_PREFIX = "Lock";
    private static final String KEY_SEPARATOR = "::";

    @Around(value = "@annotation(org.jwj.novelconfig.annotation.Lock)")
    @SneakyThrows
    public Object doAround(ProceedingJoinPoint joinPoint) {
        // 1. 获取方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 2. 构建锁
        // 2.1 获取锁注解
        Lock lock = method.getAnnotation(Lock.class);
        // 2.2 构建key
        StringBuilder builder = new StringBuilder();
        Parameter[] parameters = method.getParameters(); //获取方法定义时的参数信息, 这里主要使用其包含的注解信息
        Object[] args = joinPoint.getArgs(); // 获取传入方法的参数列表
        String prefix = lock.prefix();
        if(StringUtils.hasText(prefix)) {
            builder.append(KEY_SEPARATOR).append(prefix);
        }
        for (int i = 0; i < parameters.length; i++) {
            builder.append(KEY_SEPARATOR);
            if(parameters[i].isAnnotationPresent(Key.class)) {
                Key key = parameters[i].getAnnotation(Key.class);
                String expr = key.expr();
                Object arg = args[i];
                if(!StringUtils.hasText(expr)) {
                    arg = arg.toString();
                }
                ExpressionParser expressionParser = new SpelExpressionParser();
                Expression expression = expressionParser.parseExpression(expr, new TemplateParserContext());
                String keyExpr = expression.getValue(arg, String.class);
                builder.append(keyExpr);
            }
        }
        String lockKey = KEY_PREFIX + builder;
        RLock rLock = redissonClient.getLock(lockKey);
        // 3. 如果指定等待则默认等待三秒否则直接尝试获取锁
        if(lock.isWait() ? rLock.tryLock(lock.waitTime(), TimeUnit.SECONDS) : rLock.tryLock()) {
            try {
                return joinPoint.proceed();
            } finally {
                rLock.unlock();
            }
        }
        throw new BusinessException(lock.failCode());
    }
}
