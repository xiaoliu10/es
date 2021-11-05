package com.example.es.util;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ArrayUtils;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Description: es
 * Created by 60125 on 2021/11/3 15:34
 *
 * @author 60125
 */

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public abstract class BaseSearchRequest<T extends EsBaseModel> extends BaseIdRequest<T>{

    /**
     * 排除查询字段
     */
    private String[] excludes;

    /**
     * 包含查询字段
     */
    private String[] includes;

    private Integer pageNo;

    private Integer pageSize;

    @Builder.Default
    private SearchType searchType = SearchType.QUERY_THEN_FETCH;

    private QueryBuilder queryBuilder;

    public String[] getExcludes() {
        return excludes;
    }

    public void setExcludes(String[] excludes) {
        this.excludes = excludes;
    }

    /**
     * 默认情况下, 包含所有查询字段, 使用{@link BaseSearchRequest#getExcludes() 进行字段排除}
     * @return
     */
    public String[] getIncludes() {
        if (ArrayUtils.isEmpty(excludes)){
            ParameterizedType superclass = (ParameterizedType)this.getClass().getGenericSuperclass();
            Type type = superclass.getActualTypeArguments()[0];
            Class<? extends Type> clazz = type.getClass();
            Field[] fields = clazz.getDeclaredFields();
            return Arrays.asList(fields).parallelStream().map(v -> {
                v.setAccessible(true);
                return v.getName();
            }).toArray(String[]::new);
        }
        return includes;
    }

    public void setIncludes(String[] includes) {
        this.includes = includes;
    }

}
