package com.gc.homework.aop.Intercept;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/22
 */
public interface MethodInterceptor {
    Object invoke(MethodInvocation invocation) throws Exception;
}
