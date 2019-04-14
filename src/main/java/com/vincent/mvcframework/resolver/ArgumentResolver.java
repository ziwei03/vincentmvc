package com.vincent.mvcframework.resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

public interface ArgumentResolver {
    boolean support(Method method, int paramIndex);

    Object resolve(HttpServletRequest request, HttpServletResponse response,
                   int paramIndex, Method method);
}
