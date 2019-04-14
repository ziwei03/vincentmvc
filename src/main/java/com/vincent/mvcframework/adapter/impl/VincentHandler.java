package com.vincent.mvcframework.adapter.impl;

import com.vincent.mvcframework.adapter.HandlerAdapter;
import com.vincent.mvcframework.annotation.VService;
import com.vincent.mvcframework.resolver.ArgumentResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@VService
public class VincentHandler implements HandlerAdapter {
    @Override
    public Object[] handler(HttpServletRequest request, HttpServletResponse response, Method method, Map<String, Object> ioc) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] args = new Object[parameterTypes.length];
        Map<String, Object> resolverMap = getInstanceByType(ioc, ArgumentResolver.class);
        for (int i = 0; i < parameterTypes.length; i++) {
            for (Map.Entry<String, Object> entry : resolverMap.entrySet()) {
                ArgumentResolver argumentResolver = (ArgumentResolver) entry.getValue();
                if (argumentResolver.support(method, i)) {
                    args[i] = argumentResolver.resolve(request, response, i, method);
                }
            }
        }
        return args;
    }

    private Map<String, Object> getInstanceByType(Map<String, Object> ioc, Class<?> type) {
        Map<String, Object> resultMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                if (anInterface.isAssignableFrom(type)) {
                    resultMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
        resultMap.remove(type.getName());
        return resultMap;
    }
}
