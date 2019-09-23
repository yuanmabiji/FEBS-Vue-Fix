package cc.mrbird.febs.common.aspect;

import cc.mrbird.febs.common.exception.RedisConnectException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * AOP 处理redis操作异常
 *
 * @author MrBird
 * @link https://mrbird.cc/Spring-Boot-AOP%20log.html
 */
@Slf4j
@Aspect
@Component
public class RedisExceptionAspect {


    @Around("execution(public * cc.mrbird.febs.common.service.impl.RedisServiceImpl.*(..))")
    public Object around(ProceedingJoinPoint point) throws RedisConnectException {
        Object result = null;
        try {
            return point.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RedisConnectException(e.getMessage());
        }
    }
}
