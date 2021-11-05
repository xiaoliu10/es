package com.example.es.util;

import com.example.es.annotation.EsDocument;
import com.example.es.enums.ErrorEnum;
import com.example.es.exception.EsCommonException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.ParameterizedType;

/**
 * Description: es
 * Created by 60125 on 2021/11/3 10:20
 *
 * @author 60125
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class EsBaseRequest<T extends EsBaseModel> {

    private String type;

    private String index;

    private T data;


    protected String getIndex(){
        if (StringUtils.isNotBlank(this.index)){
            return this.index;
        }
        if (data != null){
            EsDocument esDocument = data.getClass().getAnnotation(EsDocument.class);
            return esDocument.index();
        }
        return getIndexByClazz();
    }

    private String getIndexByClazz() {
        ParameterizedType superclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        Class<T> clazz = (Class<T>) superclass.getActualTypeArguments()[0];
        EsDocument esDocument = clazz.getAnnotation(EsDocument.class);
        return esDocument == null ? null : esDocument.index();
    }

    public Class<T> getClazz(){
        ParameterizedType superclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        return (Class) superclass.getActualTypeArguments()[0];
    }

    protected String getType(){
        if (this.type != null){
            return this.type;
        }
        if (data != null){
            EsDocument esDocument = data.getClass().getAnnotation(EsDocument.class);
            return esDocument.type();
        }
        return getTypeByClazz();
    }

    private String getTypeByClazz() {
        ParameterizedType superclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        Class<T> clazz = (Class<T>) superclass.getActualTypeArguments()[0];
        EsDocument esDocument = clazz.getAnnotation(EsDocument.class);
        return esDocument == null ? null : esDocument.type();
    }
}
