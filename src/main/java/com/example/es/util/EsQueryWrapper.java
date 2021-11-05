package com.example.es.util;

import com.example.es.exception.EsCommonException;
import com.example.es.function.EsFunction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Map;

/**
 * @author 60125
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EsQueryWrapper<T extends EsBaseModel> {

    private T data;

    private boolean initColumnMap = false;

    private Map<String, ColumnCache> columnMap = null;

    private BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

    public EsQueryWrapper(T data) {
        this.data = data;
    }


    public static <T extends EsBaseModel> EsQueryWrapper<T> builder(Class<T> clazz){
        return new EsQueryWrapper<T>();
    }

    public EsQueryWrapper<T> mustEq(EsFunction<T, ?> column, Object s) {
        queryBuilder.must(QueryBuilders.termQuery(getColumn(LambdaUtils.resolve(column)), s));
        return this;
    }

    public BoolQueryBuilder build(){
        return this.queryBuilder;
    }

    /**
     * 获取 SerializedLambda 对应的列信息，从 lambda 表达式中推测实体类
     * <p>
     * 如果获取不到列信息，那么本次条件组装将会失败
     *
     * @param lambda     lambda 表达式
     * @return 列
     * @throws EsCommonException 获取不到列信息时抛出异常
     * @see SerializedLambda#getImplClass()
     * @see SerializedLambda#getImplMethodName()
     */
    private String getColumn(SerializedLambda lambda) throws EsCommonException {
        String fieldName = PropertyNamer.methodToProperty(lambda.getImplMethodName());
        Class<?> aClass = lambda.getInstantiatedType();
        if (!initColumnMap) {
            columnMap = LambdaUtils.getColumnMap(aClass);
            initColumnMap = true;
        }
        ColumnCache columnCache = columnMap.get(LambdaUtils.formatKey(fieldName));
        return columnCache.getColumn();
    }

    public EsQueryWrapper<T> mustLike(EsFunction<T, ?> column, Object s) {
        queryBuilder.must(QueryBuilders.matchQuery(getColumn(LambdaUtils.resolve(column)), s));
        return this;
    }

    public EsQueryWrapper<T> mustLeftLike(EsFunction<T, ?> column, Object s) {
        queryBuilder.must(QueryBuilders.matchPhrasePrefixQuery(getColumn(LambdaUtils.resolve(column)), s));
        return this;
    }

    public EsQueryWrapper<T> mustNotEq(EsFunction<T, ?> column, Object s) {
        queryBuilder.mustNot(QueryBuilders.termQuery(getColumn(LambdaUtils.resolve(column)), s));
        return this;
    }

    public EsQueryWrapper<T> orEq(EsFunction<T, ?> column, Object s) {
        queryBuilder.should(QueryBuilders.termQuery(getColumn(LambdaUtils.resolve(column)), s));
        return this;
    }
}