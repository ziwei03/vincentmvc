package com.vincent.mvcframework.resolver;

import com.vincent.mvcframework.annotation.VRequestParam;
import com.vincent.mvcframework.annotation.VService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@VService
public class RequestParamResolver implements ArgumentResolver {
    @Override
    public boolean support(Method method, int paramIndex) {
        Annotation[][] annotations = method.getParameterAnnotations();
        Annotation[] anno = annotations[paramIndex];
        for (Annotation annotation : anno) {
            if (VRequestParam.class.isAssignableFrom(annotation.getClass())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object resolve(HttpServletRequest request, HttpServletResponse response, int paramIndex, Method method) {
        Annotation[][] annotations = method.getParameterAnnotations();
        Annotation[] anno = annotations[paramIndex];
        for (Annotation annotation : anno) {
            if (VRequestParam.class.isAssignableFrom(annotation.getClass())) {
                VRequestParam requestParam = (VRequestParam) annotation;
                String value = requestParam.value();
                return request.getParameter(value);
            }
        }
        return null;
    }
}
