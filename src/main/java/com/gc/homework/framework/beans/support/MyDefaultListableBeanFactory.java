package com.gc.homework.framework.beans.support;

import com.gc.homework.framework.beans.config.MyBeanDefinition;
import com.gc.homework.framework.context.support.MyAbstractApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/15
 */
public class MyDefaultListableBeanFactory extends MyAbstractApplicationContext {

    protected final Map<String, MyBeanDefinition> beanDefinitionMap = new HashMap<String, MyBeanDefinition>();
}
