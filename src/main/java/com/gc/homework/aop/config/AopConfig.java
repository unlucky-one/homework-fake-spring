package com.gc.homework.aop.config;

import com.gc.homework.framework.beans.config.MyBeanDefinition;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/22
 */
@Data
public class AopConfig {

    private String pointCut;
    private String aspectClass;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;

}
