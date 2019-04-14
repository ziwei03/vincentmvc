package com.vincent.mvcframework.servlet;

import com.vincent.mvcframework.adapter.HandlerAdapter;
import com.vincent.mvcframework.annotation.VAutowired;
import com.vincent.mvcframework.annotation.VController;
import com.vincent.mvcframework.annotation.VRequestMapping;
import com.vincent.mvcframework.annotation.VService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class DispatcherServlet extends HttpServlet {

    private Properties properties = new Properties();
    private List<String> classNames = new ArrayList<>();
    private Map<String, Object> ioc = new HashMap<>();
    private Map<String, Method> handlerMapping = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        //相对路径
        String url = requestURI.replace(contextPath, "").replaceAll("/+", "/");
        if (!handlerMapping.containsKey(url)) {
            PrintWriter writer = resp.getWriter();
            writer.write("404 not found");
        }
        Method method = handlerMapping.get(url);
        HandlerAdapter vincentHandler = (HandlerAdapter) ioc.get("vincentHandler");
        Object[] args = vincentHandler.handler(req, resp, method, ioc);
        String simpleName = method.getDeclaringClass().getSimpleName();
        try {
            method.invoke(ioc.get(lowerFirstCase(simpleName)), args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        //获取配置文件里要扫描的包名
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        //扫描路径下所有包，获取classNames
        doScanPackage(properties.getProperty("basePackage"));

        //实例化bean
        doInstance();

        //依赖注入
        doAutowired();

        //初始化handlerMapping(url与controller方法的映射)
        initHandlerMapping();
    }

    private void initHandlerMapping() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (clazz.isAnnotationPresent(VController.class)) {
                VRequestMapping requestMapping = clazz.getAnnotation(VRequestMapping.class);
                if (requestMapping == null) {
                    System.out.println("controller" + clazz.getSimpleName() + "没有requestMapping注解，跳过...");
                    continue;
                }
                String baseUrl = requestMapping.value();
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(VRequestMapping.class)) {
                        VRequestMapping mapping = method.getAnnotation(VRequestMapping.class);
                        String url = (baseUrl + mapping.value()).replaceAll("/+", "/");
                        if (handlerMapping.putIfAbsent(url, method) != null) {
                            throw new RuntimeException("url" + url + "已经被注册");
                        }
                        System.out.println("mapped: " + url + " -> " + method);
                    }
                }
            }
        }
    }

    private void doAutowired() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(VAutowired.class)) {
                    VAutowired autowired = field.getAnnotation(VAutowired.class);
                    String beanName = autowired.value();
                    if ("".equals(beanName)) {
                        beanName = field.getType().getName();
                    }
                    field.setAccessible(true);
                    try {
                        field.set(entry.getValue(), ioc.get(beanName));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void doInstance() {
        if (classNames.isEmpty()) {
            return;
        }
        try {
            for (String className : classNames) {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(VController.class)) {
                    String beanName = lowerFirstCase(clazz.getSimpleName());
                    ioc.put(beanName, clazz.newInstance());
                } else if (clazz.isAnnotationPresent(VService.class)) {
                    VService service = clazz.getAnnotation(VService.class);
                    String beanName = service.value();
                    //如果没有自定义bean名，默认使用类名首字母小写作为beanName
                    if ("".equals(beanName.trim())) {
                        beanName = lowerFirstCase(clazz.getSimpleName());
                    }
                    Object instance = clazz.newInstance();
                    ioc.put(beanName, instance);

                    //如果有接口，使用接口名作为beanName
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> anInterface : interfaces) {
                        ioc.put(anInterface.getName(), instance);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doLoadConfig(String contextConfigLocation) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doScanPackage(String basePackage) {
        URL url = this.getClass().getClassLoader().getResource("/" + basePackage.replaceAll("\\.", "/"));
        if (url == null) {
            System.out.println("找不到要扫描的包，扫描失败....");
            return;
        }
        File classDir = new File(url.getFile());
        File[] files = classDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    String className = (basePackage + "." + file.getName()).replace(".class", "");
                    classNames.add(className);
                } else {
                    doScanPackage(basePackage + "." + file.getName());
                }
            }
        }
    }

    private String lowerFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
