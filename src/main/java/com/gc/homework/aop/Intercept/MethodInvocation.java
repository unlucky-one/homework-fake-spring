package com.gc.homework.aop.Intercept;

import com.gc.homework.aop.aspect.JoinPoint;
import lombok.AllArgsConstructor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/22
 */
public class MethodInvocation implements JoinPoint {
    private Object proxy;
    private Object target;
    private Method method;
    private Object[] args;
    private Class<?> targetClass;
    private List<Object> matchers;
    int currentInterceptorIndex = -1;

    public MethodInvocation(Object proxy, Object target, Method method, Object[] args, Class<?> targetClass, List<Object> matchers) {
        this.proxy = proxy;
        this.target = target;
        this.method = method;
        this.args = args;
        this.targetClass = targetClass;
        this.matchers = matchers;
    }

    public Object proceed() throws Exception {
        Object result = null;
        if (currentInterceptorIndex == matchers.size() - 1) {
            method.setAccessible(true);
            return method.invoke(target, args);
        }
        Object obj = null;
        obj = matchers.get(++currentInterceptorIndex);
        if (obj instanceof MethodInterceptor) {
            MethodInterceptor interceptor = (MethodInterceptor) obj;
           return interceptor.invoke(this);
        }
        return result;
    }

    private Object proceed(Object o) {
        if (o instanceof MethodInterceptor) {
            MethodInterceptor interceptor = (MethodInterceptor) o;
            try {
                return interceptor.invoke(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public Object getThis() {
        return target;
    }

    @Override
    public Object[] getArguments() {
        return args;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    Map<String, Object> attributeMap = new HashMap<>();

    @Override
    public void setUserAttribute(String key, Object value) {
        attributeMap.put(key, value);
    }

    @Override
    public Object getUserAttribute(String key) {
        return attributeMap.get(key);
    }
}
