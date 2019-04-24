package com.gc.homework.webmvc.servlet;

import com.gc.homework.framework.annotation.MyController;
import com.gc.homework.framework.annotation.MyRequestMapping;
import com.gc.homework.webmvc.annotation.MyRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/20
 */
public class MyHandlerAdapter {

    public boolean supports(Object handler) {
        return handler instanceof MyHandlerMapping;
    }


    public MyModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        MyHandlerMapping handlerMapping = (MyHandlerMapping) handler;
        Map<String, Integer> paramIndexMapping = new HashMap();
        Annotation[][] annotations = handlerMapping.getMethod().getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            for (Annotation a : annotations[i]) {
                if (a instanceof MyRequestParam) {
                    String value = ((MyRequestParam) a).value();
                    if (value.trim().length() == 0) continue;
                    paramIndexMapping.put(value, i);
                }
            }
        }

        Class<?>[] paramTypes = handlerMapping.getMethod().getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> type = paramTypes[i];
            if (type == HttpServletRequest.class || type == HttpServletResponse.class) {
                paramIndexMapping.put(paramTypes[i].getName(), i);
            }
        }
        Object[] paramValues = new Object[paramTypes.length];
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> param : parameterMap.entrySet()) {
            String value = Arrays.toString(param.getValue()).
                    replaceAll("\\[\\]", "").replaceAll("\\s", "");
            if (!paramIndexMapping.containsKey(param.getKey()))
                continue;
            int index = paramIndexMapping.get(param.getKey());
            paramValues[index] = convert(value, paramTypes[index]);

        }
        if (paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            paramValues[paramIndexMapping.get(HttpServletRequest.class.getName())] = request;
        }
        if (paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            paramValues[paramIndexMapping.get(HttpServletResponse.class.getName())] = response;
        }

        try {
            Object result = null;
            if (paramValues.length == 0)
                result = handlerMapping.getMethod().invoke(handlerMapping.getController());
            else
                result = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);
            if (result instanceof MyModelAndView)
                return (MyModelAndView) result;
            else if (result instanceof String) {
                Map<String,Object> model=new HashMap<>();
                model.put("body",result);
                return new MyModelAndView("", model);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object convert(String value, Class<?> paramType) {
        if (paramType == Integer.class)
            return Integer.valueOf(value);
        else if (paramType == Double.class)
            return Double.valueOf(value);
        return value;
    }

    public long getLastModified(HttpServletRequest request, Object handler) {
        return 0;
    }
}
