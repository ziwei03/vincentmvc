package com.vincent.mvcframework.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VQualifier {
    String value() default "";
}
