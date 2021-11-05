package com.example.es.annotation;

import java.lang.annotation.*;

/**
 * Description: es
 * Created by 60125 on 2021/11/5 13:54
 *
 * @author 60125
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EsModelConfig {

    String[] packages();
}
