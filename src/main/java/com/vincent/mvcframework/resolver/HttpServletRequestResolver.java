package com.vincent.mvcframework.resolver;

import com.vincent.mvcframework.annotation.VService;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@VService
public class HttpServletRequestResolver implements ArgumentResolver {

    @Override
    public boolean support(Method method, int paramIndex) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        return ServletRequest.class.isAssignableFrom(parameterTypes[paramIndex]);
    }

    @Override
    public Object resolve(HttpServletRequest request, HttpServletResponse response, int paramIndex, Method method) {
        return request;
    }
}
