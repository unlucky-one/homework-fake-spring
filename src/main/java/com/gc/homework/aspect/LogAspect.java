package com.gc.homework.aspect;

import com.gc.homework.aop.aspect.JoinPoint;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/22
 */
public class LogAspect {

    public void before(JoinPoint joinPoint){
        System.out.println("before");
    }

    public void after(JoinPoint joinPoint){
        System.out.println("after");
    }

    public void afterThrow(Throwable throwable){
        System.out.println("throw");
    }
}
