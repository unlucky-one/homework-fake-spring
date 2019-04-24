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
public class MethodAfterAdviceInterceptor extends AbstractAspectAdvice implements MethodInterceptor {

    public MethodAfterAdviceInterceptor(Method method, Object target) {
        super(method, target);
    }

    void after(Method method, Object args[], Object target) throws Exception {
       super.invokeAdviceMethod(joinPoint, null, null);
    }

    @Override
    public Object invoke(MethodInvocation invocation)throws Exception {
        joinPoint=invocation;
        Object proceed = invocation.proceed();
        after(invocation.getMethod(), invocation.getArguments(), invocation.getThis());
        return proceed;
    }
}
