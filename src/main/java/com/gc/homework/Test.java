package com.gc.homework;

import com.gc.homework.framework.context.MyApplicationContext;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/17
 */
public class Test {

    public static void main(String[] args) {
        MyApplicationContext applicationContext = new MyApplicationContext("classPath:application.properties");
        applicationContext.refresh();

    }
}
