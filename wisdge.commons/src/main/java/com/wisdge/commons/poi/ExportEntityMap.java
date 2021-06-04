package com.wisdge.commons.poi;

import java.lang.annotation.*;

/**
 * @Author Carlos.Chen
 * @Date 2021/3/25
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExportEntityMap {

    String EnName() default "数据库列名";
    String CnName() default "实体映射名";
    boolean primarykey() default false;
    boolean required() default false;
}
