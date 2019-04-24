package com.gc.homework.aop.aspect;

import com.gc.homework.aop.Intercept.MethodInterceptor;
import com.gc.homework.aop.Intercept.MethodInvocation;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/22
 */
@Data
public class AfterThrowingAdviceInterceptor extends AbstractAspectAdvice implements MethodInterceptor {

    String throwName;
    public AfterThrowingAdviceInterceptor(Method method, Object target) {
        super(method, target);
    }

    void afterThrow(Method method, Object args[], Object target) throws Exception {
        super.invokeAdviceMethod(joinPoint, null, null);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Exception  {
        joinPoint=invocation;
        try {
           return invocation.proceed();
        } catch (Exception e) {
           invokeAdviceMethod(joinPoint,null,e);
        }
        return null;
    }
}
