package org.jwj.novelconfig.Exception;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jwj.novelcommon.constants.ErrorCodeEnum;

/**
 * 自定义业务异常，用于处理用户请求时，业务错误时抛出
 * @author jwj44
 */
@EqualsAndHashCode(callSuper = true) // 生成equals和hashcode方法
@Data
public class BusinessException extends RuntimeException{
    private final ErrorCodeEnum errorCodeEnum;

    public BusinessException(ErrorCodeEnum errorCodeEnum) {
        // 不调用父类 Throwable的fillInStackTrace() 方法生成栈追踪信息（通过super第二个参数指定），提高应用性能
        // 构造器之间的调用必须在第一行
        super(errorCodeEnum.getMessage(), null, false, false);
        this.errorCodeEnum = errorCodeEnum;
    }
}
