package com.example.es.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Description: es
 * Created by 60125 on 2021/11/5 15:09
 *
 * @author 60125
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface EsField {

    boolean exist() default true;

    String value() default "";
}
