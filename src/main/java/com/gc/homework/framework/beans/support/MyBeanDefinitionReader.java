package com.gc.homework.framework.beans.support;

import com.gc.homework.framework.annotation.MyController;
import com.gc.homework.framework.annotation.MyService;
import com.gc.homework.framework.beans.config.MyBeanDefinition;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/16
 */
public class MyBeanDefinitionReader {
    @Getter
    private Properties config = new Properties();
    private final String SCAN_PACKAGE = "scanPackage";
    private List<String> registryBeans = new ArrayList<String>();

    public MyBeanDefinitionReader(String... locations) {
        doLoadConfig(locations);
        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    private void doLoadConfig(String... locations) {
        for (String location : locations) {
            InputStream stream = this.getClass().getResourceAsStream("/" + location.toLowerCase().replace("classpath:", ""));
            try {
                config.load(stream);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (stream != null)
                        stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doScanner(String packageName) {
        URL url = this.getClass().getResource("/" + packageName.replaceAll("\\.", "/"));
        String path = "";
        try {
            path = java.net.URLDecoder.decode(url.getFile(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        File file = new File(path);
        for (File f : file.listFiles()) {
            if (f.isDirectory()) {
                doScanner(packageName + "." + f.getName());
            } else {
                String fileName = f.getName();
                if (fileName.endsWith(".class")) {
                    String className = packageName + "." + fileName.replace(".class", "");
                    registryBeans.add(className);
                }
            }
        }
    }

    public List<MyBeanDefinition> loadBeanDefinitions(String... locations) {
        List<MyBeanDefinition> result = new ArrayList<MyBeanDefinition>();
        for (String className : registryBeans) {
            MyBeanDefinition myBeanDefinition = doCreateBeanDefinition(className);
            if (myBeanDefinition != null)
                result.add(myBeanDefinition);
        }
        return result;
    }

    private MyBeanDefinition doCreateBeanDefinition(String className) {
        try {
            Class<?> bean = Class.forName(className);
            if (bean.isInterface()) {
                return null;
            } else if (!(bean.isAnnotationPresent(MyController.class) || bean.isAnnotationPresent(MyService.class))) {
                return null;
            } else {
                MyBeanDefinition result = new MyBeanDefinition();
                Class<?> clazz = Class.forName(className);
                result.setBeanClassName(className);
                if (bean.isAnnotationPresent(MyService.class) && bean.getInterfaces().length > 0)
                    result.setFactoryBeanName(clazz.getInterfaces()[0].getName());
                else
                    result.setFactoryBeanName(clazz.getName());
                return result;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }
}
