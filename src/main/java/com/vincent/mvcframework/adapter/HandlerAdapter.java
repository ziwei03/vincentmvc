package com.vincent.mvcframework.adapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

public interface HandlerAdapter {
    Object[] handler(HttpServletRequest request, HttpServletResponse response, Method method, Map<String, Object> ioc);
}
