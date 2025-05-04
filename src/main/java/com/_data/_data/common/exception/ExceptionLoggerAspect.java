package com._data._data.common.exception;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class ExceptionLoggerAspect {

    private final Logger logger = LoggerFactory.getLogger(ExceptionLoggerAspect.class);

    @AfterThrowing(pointcut = "execution(* *(..))", throwing = "e")
    public void logException(Throwable e) {
        logger.error("Exception occurred", e);
    }
}

