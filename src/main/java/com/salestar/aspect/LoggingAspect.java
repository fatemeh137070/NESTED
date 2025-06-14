package com.salestar.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.sales.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("[LOG] Before: " + joinPoint.getSignature().getName());
    }
}