package com.wisdge.commons.poi;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Column {
    boolean required() default false;
    String code();
    boolean primarykey() default false;
}