package com.gc.homework.webmvc.servlet;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/20
 */
@Data
@AllArgsConstructor
public class MyHandlerMapping {
    private Pattern pattern;
    private Object controller;
    private Method method;

}
