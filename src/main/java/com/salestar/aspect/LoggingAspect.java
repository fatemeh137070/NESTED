package com.salestar.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.salestar.service.*.*(..))")
    public void logBefore() {
        System.out.println("🔍 Service method called");
    }
}