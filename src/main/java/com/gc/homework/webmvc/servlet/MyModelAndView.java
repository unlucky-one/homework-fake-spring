package com.gc.homework.webmvc.servlet;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/20
 */
@Data
@AllArgsConstructor
public class MyModelAndView {
    private String viewName;
    private Map<String, ?> model;
    private int status;

    public MyModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public MyModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }
}
