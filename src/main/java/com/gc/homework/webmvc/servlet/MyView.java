package com.gc.homework.webmvc.servlet;

import com.sun.istack.internal.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/21
 */
public class MyView {
    File file;

    enum TYPE {
        str,
        template,
    }

    TYPE type = TYPE.str;
    private final String DEFAULT_CHARSET = "utf-8";
    private final String DEFAULT_CONTENT_TYPE = "text/html;charset:utf-8";

    public MyView(File file) {
        this.file = file;
        type = TYPE.template;
    }

    public MyView() {
    }

    public void render(@Nullable Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case str:
                sb.append(model.get("body").toString());
                break;
            case template:
                RandomAccessFile ra = new RandomAccessFile(file, "r");
                String lineStr = "";
                while ((lineStr = ra.readLine()) != null) {
                    lineStr = new String(lineStr.getBytes("ISO-8859-1"), DEFAULT_CHARSET);
                    Pattern pattern = Pattern.compile("$\\{[^\\}]+\\}", Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(lineStr);
                    while (matcher.find()) {
                        String group = matcher.group();
                        group = group.replaceAll("$\\{|\\}", "");
                        Object o = model.get(group);
                        if (o == null)
                            continue;
                        lineStr = matcher.replaceFirst(o.toString());
                        matcher = pattern.matcher(lineStr);
                    }
                    sb.append(lineStr);
                }
                break;

        }
        response.setCharacterEncoding(DEFAULT_CHARSET);
        response.setContentType(DEFAULT_CONTENT_TYPE);
        response.getWriter().write(sb.toString());
    }
}
