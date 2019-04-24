package com.gc.homework.controller;

import com.gc.homework.framework.annotation.MyAutowired;
import com.gc.homework.framework.annotation.MyController;
import com.gc.homework.framework.annotation.MyRequestMapping;
import com.gc.homework.service.TestService;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/17
 */
@MyController
@MyRequestMapping("/test")
public class TestController {
    @MyAutowired
    TestService testService;

    @MyRequestMapping("/test1")
    public String showTest() {
        return testService.hello();
    }

}
