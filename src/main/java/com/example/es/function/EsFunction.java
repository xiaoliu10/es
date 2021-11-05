package com.example.es.function;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Description: es
 * Created by 60125 on 2021/11/4 14:10
 *
 * @author 60125
 */
@FunctionalInterface
public interface EsFunction<T, R> extends Function<T, R>, Serializable {

}
