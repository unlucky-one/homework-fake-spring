package com.gc.homework.aop;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/22
 */
public interface MyAopProxy {

    public Object getProxy() throws Exception;
    public Object getProxy(ClassLoader classLoader) throws Exception;
}
