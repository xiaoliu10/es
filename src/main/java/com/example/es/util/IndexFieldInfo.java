package com.example.es.util;

import com.example.es.annotation.EsField;
import lombok.*;

import java.lang.reflect.Field;

/**
 * Description: es
 * Created by 60125 on 2021/11/5 9:34
 *
 * @author 60125
 */
@Getter
@ToString
@EqualsAndHashCode
public class IndexFieldInfo implements Constants {

    /**
     * 属性
     *
     * @since 3.3.1
     */
    private Field field;
    /**
     * 字段名
     */
    private String column;
    /**
     * 属性名
     */
    private String property;
    /**
     * 缓存 sql select
     */
    @Setter(AccessLevel.NONE)
    private String sqlSelect;

    /**
     * 全新的 存在 TableField 注解时使用的构造函数
     */
    @SuppressWarnings("unchecked")
    public IndexFieldInfo(Field field, EsField esField) {
        field.setAccessible(true);
        this.field = field;
        this.property = field.getName();

        String column = esField.value();
        if (StringUtils.isBlank(column)) {
            column = StringUtils.camelToUnderline(column);
        }

        this.column = column;
        this.sqlSelect = column;
    }

    public IndexFieldInfo() {
    }

    /**
     * 不存在 TableField 注解时, 使用的构造函数
     */
    public IndexFieldInfo(Field field) {
        field.setAccessible(true);
        this.field = field;
        this.property = field.getName();

        String column = field.getName();
        column = StringUtils.camelToUnderline(column);

        this.column = column;
    }

    private String convertIfProperty(String prefix, String property) {
        return StringUtils.isNotBlank(prefix) ? prefix.substring(0, prefix.length() - 1) + "['" + property + "']" : property;
    }


}
