package com.vincent.mvcframework.resolver;

import com.vincent.mvcframework.annotation.VService;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@VService
public class HttpServletResponseResolver implements ArgumentResolver {

    @Override
    public boolean support(Method method, int paramIndex) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        return ServletResponse.class.isAssignableFrom( parameterTypes[paramIndex]);
    }

    @Override
    public Object resolve(HttpServletRequest request, HttpServletResponse response, int paramIndex, Method method) {
        return response;
    }
}
