package org.example.clothing_be.logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LogManager.getLogger(LoggingAspect.class);

    @Around("execution(* org.example.clothing_be.service.serviceImpl..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        long startTime = System.currentTimeMillis();

        // 👉 START LOG
        log.info("➡️ START {}.{}() with args={}", className, methodName, Arrays.toString(args));

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception ex) {
            // 👉 ERROR LOG
            log.error("❌ ERROR in {}.{}() with args={} | message={}",
                    className,
                    methodName,
                    Arrays.toString(args),
                    ex.getMessage(),
                    ex
            );
            throw ex;
        }

        long duration = System.currentTimeMillis() - startTime;

        // 👉 END LOG
        log.info("✅ END {}.{}() | result={} | time={}ms",
                className,
                methodName,
                result,
                duration
        );

        return result;
    }
}