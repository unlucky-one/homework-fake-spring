package com.gc.homework.service;

import com.gc.homework.framework.annotation.MyService;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/23
 */
@MyService
public class TestServiceImpl implements TestService {
    @Override
    public String hello() {
        System.out.println("hello");
        return "hello world!";
    }
}
