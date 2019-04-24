package com.gc.homework.aop;

import com.gc.homework.aop.support.AdvisedSupport;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/22
 */
public class CglibAopProxy implements MyAopProxy {
    AdvisedSupport advice;

    public CglibAopProxy(AdvisedSupport advice) {
        this.advice = advice;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.getClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        try {
            return advice.getTargetClass().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
