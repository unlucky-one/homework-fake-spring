package com.gc.homework.aop.aspect;

import com.gc.homework.aop.Intercept.MethodInterceptor;
import com.gc.homework.aop.Intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/22
 */
public class MethodBeforeAdviceInterceptor extends AbstractAspectAdvice implements MethodInterceptor {

    public MethodBeforeAdviceInterceptor(Method method, Object target) {
        super(method, target);
    }

    void before(Method method, Object args[], Object target) throws Exception {
        super.invokeAdviceMethod(joinPoint, null, null);
    }

    @Override
    public Object invoke(MethodInvocation invocation) {
        try {
            joinPoint = invocation;
            before(invocation.getMethod(), invocation.getArguments(), invocation.getThis());
            return invocation.proceed();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
