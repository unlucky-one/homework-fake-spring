package com.gc.homework.framework.beans.config;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/15
 */

@Data
public class MyBeanDefinition {
    private String beanClassName;
    private String factoryBeanName;
    private boolean lazyInit = false;
}
