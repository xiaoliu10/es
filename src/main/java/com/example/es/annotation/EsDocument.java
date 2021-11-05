package com.example.es.annotation;

import java.lang.annotation.*;

/**
 * Description: es
 * Created by 60125 on 2021/11/3 10:35
 *
 * @author 60125
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface EsDocument {

    String index();

    String type() default "_doc";

}
