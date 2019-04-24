package com.gc.homework.aop.support;

import com.gc.homework.aop.config.AopConfig;
import com.gc.homework.aop.aspect.AfterThrowingAdviceInterceptor;
import com.gc.homework.aop.aspect.MethodAfterAdviceInterceptor;
import com.gc.homework.aop.aspect.MethodBeforeAdviceInterceptor;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/22
 */
@Data
public class AdvisedSupport {

    Object target;
    Class<?> targetClass;
    private Pattern pointCutPattern;
    AopConfig aopConfig;
    Map<Method, List<Object>> methodCache = new HashMap<>();

    public AdvisedSupport(AopConfig aopConfig) {
        this.aopConfig = aopConfig;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    void parse() {
        String pointCut = aopConfig.getPointCut();
        String pointCutClass = pointCut.substring(0, pointCut.lastIndexOf("(") - 3);
        pointCutPattern = Pattern.compile(pointCutClass);
        String pointCutRe = aopConfig.getPointCut()
                .replaceAll("\\.", "\\\\.")
                .replaceAll("\\\\.\\*", ".*")
                .replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");
        Pattern pattern = Pattern.compile(pointCutRe);
        Class<?> aspectClass = null;
        Map<String, Method> aspectMethods = new HashMap<>();
        try {
            aspectClass = Class.forName(aopConfig.getAspectClass());
            for (Method method : aspectClass.getMethods()) {
                aspectMethods.put(method.getName(), method);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        for (Method method : targetClass.getMethods()) {
//            Method interfaceMethod = method;
//            if (targetClass.getInterfaces().length > 0) {
//                for (Class<?> aClass : targetClass.getInterfaces()) {
//                    try {
//                        interfaceMethod = aClass.getMethod(method.getName(), method.getParameterTypes());
//                    } catch (NoSuchMethodException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
            String methodStr = method.toString();
            if (methodStr.contains("throws")) {
                methodStr = methodStr.substring(0, methodStr.lastIndexOf("throws")).trim();
            }
            Matcher matcher = pattern.matcher(methodStr);
            if (matcher.matches()) {
                List<Object> advices = new LinkedList<>();
                if (!isEmpty(aopConfig.getAspectBefore())) {
                    try {
                        advices.add(new MethodBeforeAdviceInterceptor(aspectMethods.get(aopConfig.getAspectBefore()), aspectClass.newInstance()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (!isEmpty(aopConfig.getAspectAfter())) {
                    try {
                        advices.add(new MethodAfterAdviceInterceptor(aspectMethods.get(aopConfig.getAspectAfter()), aspectClass.newInstance()));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (!isEmpty(aopConfig.getAspectAfterThrow())) {
                    try {
                        AfterThrowingAdviceInterceptor throwingAdviceInterceptor = new AfterThrowingAdviceInterceptor(aspectMethods.get(aopConfig.getAspectAfterThrow()), aspectClass.newInstance());
                        throwingAdviceInterceptor.setThrowName(aopConfig.getAspectAfterThrowingName());
                        advices.add(throwingAdviceInterceptor);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                methodCache.put(method, advices);
            }
        }


    }

    boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    public List<Object> getAdvice(Method method, Class<?> targetClass) {
        this.targetClass = targetClass;
        List<Object> objects = methodCache.get(method);
        if (objects == null) {

        }
        return objects;
    }

    public boolean pointCutMatch() {
        return true;
    }
}
