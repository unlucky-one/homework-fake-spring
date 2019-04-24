package com.gc.homework.aop.aspect;

import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/22
 */

public abstract class AbstractAspectAdvice implements MyAdvice {

    Method method;
    Object target;
    JoinPoint joinPoint;

    public AbstractAspectAdvice(Method method, Object target) {
        this.method = method;
        this.target = target;
    }

    public Object invokeAdviceMethod(JoinPoint joinPoint, Object result, Throwable throwable) throws Exception {
        Class<?>[] classes = method.getParameterTypes();
        if (classes == null || classes.length == 0) {
            return method.invoke(target);
        } else {
            Object[] args = new Object[classes.length];
            for (int i = 0; i < classes.length; i++) {
                if (classes[i] == JoinPoint.class) {
                    args[i] = joinPoint;
                } else if (classes[i] == Throwable.class) {
                    args[i] = throwable;
                } else {
                    args[i] = result;
                }
            }
            return method.invoke(target, args);
        }
    }

}
