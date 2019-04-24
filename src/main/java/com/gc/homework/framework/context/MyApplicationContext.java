package com.gc.homework.framework.context;

import com.gc.homework.aop.CglibAopProxy;
import com.gc.homework.aop.Intercept.MethodInterceptor;
import com.gc.homework.aop.JdkDynamicAopProxy;
import com.gc.homework.aop.config.AopConfig;
import com.gc.homework.aop.support.AdvisedSupport;
import com.gc.homework.framework.annotation.MyAutowired;
import com.gc.homework.framework.annotation.MyController;
import com.gc.homework.framework.annotation.MyService;
import com.gc.homework.framework.beans.MyBeanFactory;
import com.gc.homework.framework.beans.MyBeanWrapper;
import com.gc.homework.framework.beans.config.MyBeanDefinition;
import com.gc.homework.framework.beans.config.MyBeanPostProcessor;
import com.gc.homework.framework.beans.support.MyBeanDefinitionReader;
import com.gc.homework.framework.beans.support.MyDefaultListableBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/15
 */
public class MyApplicationContext extends MyDefaultListableBeanFactory implements MyBeanFactory {
    private String[] locations;
    private MyBeanDefinitionReader myBeanDefinitionReader;
    private Map<String, MyBeanWrapper> factoryBeanCache = new ConcurrentHashMap<String, MyBeanWrapper>();
    private Map<String, Object> singletonObjects = new ConcurrentHashMap<String, Object>();

    public MyApplicationContext(String... configLocations) {
        this.locations = configLocations;
    }

    @Override
    public void refresh() {
        myBeanDefinitionReader = new MyBeanDefinitionReader(locations);
        List<MyBeanDefinition> myBeanDefinitions = myBeanDefinitionReader.loadBeanDefinitions();
        doRegisterBeanDefinition(myBeanDefinitions);
        doAutowrited();
    }

    private void doRegisterBeanDefinition(List<MyBeanDefinition> definitions) {
        for (MyBeanDefinition beanDefinition : definitions) {
            beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
            if (!beanDefinition.isLazyInit()) {
                loadBean(beanDefinition.getFactoryBeanName());
            }
        }
    }

    void loadBean(String beanName) {
        MyBeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        MyBeanWrapper wrapper = initializeBean(beanName, beanDefinition);
        factoryBeanCache.put(beanName, wrapper);
    }

    private void doAutowrited() {
        for (Map.Entry<String, MyBeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            populateBean(beanName, entry.getValue(), factoryBeanCache.get(entry.getKey()));
        }
    }

    public <T> T getBean(Class<T> clazz) {
        return (T) getBean(clazz.getName());
    }

    public Object getBean(String beanName) {
        try {
            if (!factoryBeanCache.containsKey(beanName)) {
                loadBean(beanName);
            }
        } catch (Exception e) {

        }
        return factoryBeanCache.get(beanName).getWrappedInstance();
    }

    MyBeanWrapper initializeBean(String beanName, MyBeanDefinition beanDefinition) {
        String className = beanDefinition.getBeanClassName();
        Object instance = null;
        try {
            if (singletonObjects.containsKey(className)) {
                instance = singletonObjects.get(className);
            } else {

                Class<?> aClass = Class.forName(className);
                MyBeanPostProcessor processor = new MyBeanPostProcessor();
                processor.postProcessBeforeInitialization(null, beanName);
                AdvisedSupport advice = instantionAopAdvised(beanDefinition);
                advice.setTargetClass(aClass);
                if (advice.pointCutMatch()) {
                    instance = createProxy(advice);
                    advice.setTarget(aClass.newInstance());
                } else {
                    instance = aClass.newInstance();
                    advice.setTarget(instance);
                }
                processor.postProcessAfterInitialization(instance, beanName);
                singletonObjects.put(className, instance);
                singletonObjects.put(beanDefinition.getFactoryBeanName(), instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyBeanWrapper wrapper = new MyBeanWrapper(instance);
        return wrapper;
    }

    private Object createProxy(AdvisedSupport advice) {
        Class<?> targetClass = advice.getTargetClass();
        if (targetClass.getInterfaces().length > 0) {
            return new JdkDynamicAopProxy(advice).getProxy();
        }
        return new CglibAopProxy(advice).getProxy();
    }

    private AdvisedSupport instantionAopAdvised(MyBeanDefinition beanDefinition) {
        AopConfig aopConfig = new AopConfig();
        aopConfig.setPointCut(myBeanDefinitionReader.getConfig().getProperty("pointCut"));
        aopConfig.setAspectClass(myBeanDefinitionReader.getConfig().getProperty("aspectClass"));
        aopConfig.setAspectBefore(myBeanDefinitionReader.getConfig().getProperty("aspectBefore"));
        aopConfig.setAspectAfter(myBeanDefinitionReader.getConfig().getProperty("aspectAfter"));
        aopConfig.setAspectAfterThrow(myBeanDefinitionReader.getConfig().getProperty("aspectAfterThrow"));
        aopConfig.setAspectAfterThrowingName(myBeanDefinitionReader.getConfig().getProperty("aspectAfterThrowingName"));
        return new AdvisedSupport(aopConfig);
    }

    void populateBean(String beanName, MyBeanDefinition beanDefinition, MyBeanWrapper beanWrapper) {
        Object instance = beanWrapper.getWrappedInstance();
        Class<?> wrappedClass = beanWrapper.getWrappedClass();
        if (!(wrappedClass.isAnnotationPresent(MyController.class) || wrappedClass.isAnnotationPresent(MyService.class))) {
            return;
        }

        for (Field field : wrappedClass.getDeclaredFields()) {
            MyAutowired autowired = field.getAnnotation(MyAutowired.class);
            if (autowired != null) {
                String className = field.getType().getName();
                field.setAccessible(true);
                try {
                    field.set(instance, factoryBeanCache.get(className).getWrappedInstance());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public String[] getBeanDefinitionNames() {
        return beanDefinitionMap.keySet().toArray(new String[beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount() {
        return beanDefinitionMap.size();
    }

    public Properties getConfig() {
        return myBeanDefinitionReader.getConfig();
    }
}
