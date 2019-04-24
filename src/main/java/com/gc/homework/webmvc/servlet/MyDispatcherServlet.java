package com.gc.homework.webmvc.servlet;

import com.gc.homework.framework.annotation.MyController;
import com.gc.homework.framework.annotation.MyRequestMapping;
import com.gc.homework.framework.context.MyApplicationContext;
import com.gc.homework.webmvc.servlet.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/19
 */
@Slf4j
public class MyDispatcherServlet extends HttpServlet {
    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";
    private MyApplicationContext context;
    List<MyHandlerMapping> handlerMappings = new ArrayList<MyHandlerMapping>();
    Map<MyHandlerMapping, MyHandlerAdapter> adapterMap = new HashMap<MyHandlerMapping, MyHandlerAdapter>();
    List<MyViewResolver> resolvers = new ArrayList<MyViewResolver>();


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        MyApplicationContext applicationContext = new MyApplicationContext("classPath:application.properties");
        applicationContext.refresh();
        initStrategies(applicationContext);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doOptions(req, resp);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.service(req, resp);
        doDispatch(req, resp);
    }

    void doDispatch(HttpServletRequest req, HttpServletResponse resp) {
        MyModelAndView mv = new MyModelAndView("404");
        MyHandlerMapping handler = getHandler(req);
        if (handler == null) {
            processDispatchResult(req, resp, handler, mv);
            return;
        }

        MyHandlerAdapter handlerAdapter = getHandlerAdapter(handler);
        mv = handlerAdapter.handle(req, resp, handler);
        processDispatchResult(req, resp, handler, mv);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, MyHandlerMapping handler, MyModelAndView mv) {
        if (mv == null)
            return;
        if (resolvers.isEmpty())
            return;
        MyView view =null;
        for (MyViewResolver resolver : resolvers) {
             view = resolver.resolveViewName(mv.getViewName(), null);
            if (view != null) {
                break;
            }
        }
        try {
            if(view==null)
                view=new MyView();
            view.render(mv.getModel(), req, resp);
        } catch (Exception e) {
            resp.setStatus(500);
        }

    }

    private MyHandlerAdapter getHandlerAdapter(MyHandlerMapping handler) {
        if (adapterMap.isEmpty())
            return null;
        MyHandlerAdapter adapter = adapterMap.get(handler);
        return adapter.supports(handler) ? adapter : null;
    }


    private MyHandlerMapping getHandler(HttpServletRequest request) {
        if (handlerMappings.isEmpty())
            return null;
        String url = request.getRequestURI();
        String contextPath = request.getContextPath();
        url = url.replace(contextPath, "").replace("/+", "/");
        for (MyHandlerMapping handler : handlerMappings) {
            Matcher matcher = handler.getPattern().matcher(url);
            if (!matcher.matches())
                continue;
            return handler;
        }
        return null;
    }


    protected void initStrategies(MyApplicationContext context) {
        this.context = context;
        //多文件上传
        initMultipartResolver(context);
        //初始化本地语言环境
        initLocaleResolver(context);
        //初始化模板处理器
        initThemeResolver(context);
        //handlerMapping*
        initHandlerMappings(context);
        //初始化参数适配器*
        initHandlerAdapters(context);
        //初始化异常拦截器
        initHandlerExceptionResolvers(context);
        //初始化视图预处理器
        initRequestToViewNameTranslator(context);
        //初始化视图转换器*
        initViewResolvers(context);
        //参数缓存器
        initFlashMapManager(context);
    }

    private void initMultipartResolver(MyApplicationContext context) {

    }

    private void initLocaleResolver(MyApplicationContext context) {

    }

    private void initThemeResolver(MyApplicationContext context) {

    }


    private void initHandlerMappings(MyApplicationContext context) {
        for (String s : context.getBeanDefinitionNames()) {
            Object controller = context.getBean(s);
            Class<?> clazz = controller.getClass();
            String baseUrl = "";
            if (!clazz.isAnnotationPresent(MyController.class))
                continue;
            if (clazz.isAnnotationPresent(MyRequestMapping.class)) {
                MyRequestMapping requestMapping = clazz.getAnnotation(MyRequestMapping.class);
                baseUrl = requestMapping.value();
            }
            Method[] methods = clazz.getMethods();
            for (Method m : methods) {
                if (!m.isAnnotationPresent(MyRequestMapping.class))
                    continue;
                MyRequestMapping requestMapping = m.getAnnotation(MyRequestMapping.class);

                String url = ("/" + baseUrl + requestMapping.value()).replaceAll("\\*", ".*").replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(url);
                handlerMappings.add(new MyHandlerMapping(pattern, controller, m));
                log.info(url);
            }
        }
    }

    private void initHandlerAdapters(MyApplicationContext context) {
        for (MyHandlerMapping mapping : handlerMappings) {
            adapterMap.put(mapping, new MyHandlerAdapter());
        }
    }

    private void initHandlerExceptionResolvers(MyApplicationContext context) {

    }

    private void initRequestToViewNameTranslator(MyApplicationContext context) {

    }

    private void initViewResolvers(MyApplicationContext context) {
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String path = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        try {
            path = java.net.URLDecoder.decode(path, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        File file = new File(path);
        for (File f : file.listFiles()) {
            resolvers.add(new MyViewResolver(f.getPath()));
        }
    }

    private void initFlashMapManager(MyApplicationContext context) {

    }
}
