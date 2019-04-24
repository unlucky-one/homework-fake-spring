package com.gc.homework.webmvc.servlet;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/21
 */
public class MyViewResolver {
    File templateRootDir;
    private String viewName;

    public MyViewResolver(String templateRoot) {
        templateRootDir = new File(templateRoot);
        if (templateRootDir != null)
            viewName = templateRootDir.getName();
        if (viewName.lastIndexOf(".") > 0)
            viewName = viewName.substring(0, viewName.lastIndexOf("."));
    }

    public MyView resolveViewName(String viewName, Locale locale) {
        if (viewName == null || viewName.length() == 0)
            return null;
        if (viewName.toLowerCase().equals(this.viewName)) {
            File file = new File(templateRootDir.getPath());
            if (file.exists())
                return new MyView(file);
        }
        return null;
    }
}
