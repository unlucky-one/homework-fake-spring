package com.gc.homework.framework.beans;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/16
 */
public class MyBeanWrapper {

    private Object instance;
    Class<?> wrappedClass;

    public MyBeanWrapper(Object instance) {
        this.instance = instance;
    }

    public Object getWrappedInstance() {
        return instance;
    }

    public Class<?> getWrappedClass() {
        return instance.getClass();
    }

}
