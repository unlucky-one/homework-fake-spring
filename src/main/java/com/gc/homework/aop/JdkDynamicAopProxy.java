package com.gc.homework.aop;

import com.gc.homework.aop.support.AdvisedSupport;
import com.gc.homework.aop.Intercept.MethodInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/22
 */
public class JdkDynamicAopProxy implements MyAopProxy, InvocationHandler {
    AdvisedSupport advised = null;
    Object proxy = null;

    public JdkDynamicAopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }

    @Override
    public Object getProxy() {
        return getProxy(advised.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        if (proxy == null)
            proxy = Proxy.newProxyInstance(classLoader, advised.getTargetClass().getInterfaces(), this);
        return proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?>[] interfaces = advised.getTargetClass().getInterfaces();
        Method methodProxy = null;
        if (interfaces != null && interfaces.length > 0) {
            method = advised.getTargetClass().getMethod(method.getName(), method.getParameterTypes());
        }
        if (proxy != null) {
            methodProxy = proxy.getClass().getMethod(method.getName(), method.getParameterTypes());
        } else {
            methodProxy = method;
        }
        List<Object> matchers = advised.getAdvice(method, advised.getTargetClass());
        MethodInvocation methodInvocation = new MethodInvocation(proxy, advised.getTarget(), method, args, advised.getTargetClass(), matchers);
        return methodInvocation.proceed();
    }


}
