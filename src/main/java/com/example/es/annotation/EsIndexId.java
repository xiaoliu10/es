package com.example.es.annotation;

import java.lang.annotation.*;

/**
 * Description: es
 * Created by 60125 on 2021/11/5 15:05
 *
 * @author 60125
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface EsIndexId {

    String name();
}
