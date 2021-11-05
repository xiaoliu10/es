/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.example.es.util;

import com.example.es.annotation.EsField;
import com.example.es.annotation.EsIndexId;
import com.example.es.exception.EsCommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

/**
 * <p>
 * 实体类反射表辅助类
 * </p>
 *
 * @author hubin sjy
 * @since 2016-09-09
 */
public class IndexHelper {

    private static final Logger logger = LoggerFactory.getLogger(IndexHelper.class);

    /**
     * 储存反射类表信息
     */
    private static final Map<Class<?>, IndexInfo> TABLE_INFO_CACHE = new ConcurrentHashMap<>();

    /**
     * 默认表主键名称
     */
    private static final String DEFAULT_ID_NAME = "id";

    /**
     * <p>
     * 获取实体映射表信息
     * </p>
     *
     * @param clazz 反射实体类
     * @return 数据库表反射信息
     */
    public static IndexInfo getIndexInfo(Class<?> clazz) {
        if (clazz == null
            || ReflectionKit.isPrimitiveOrWrapper(clazz)
            || clazz == String.class) {
            return null;
        }
        // https://github.com/baomidou/mybatis-plus/issues/299
        IndexInfo tableInfo = TABLE_INFO_CACHE.get(ClassUtils.getUserClass(clazz));
        if (null != tableInfo) {
            return tableInfo;
        }
        //尝试获取父类缓存
        Class<?> currentClass = clazz;
        while (null == tableInfo && Object.class != currentClass) {
            currentClass = currentClass.getSuperclass();
            tableInfo = TABLE_INFO_CACHE.get(ClassUtils.getUserClass(currentClass));
        }
        if (tableInfo != null) {
            TABLE_INFO_CACHE.put(ClassUtils.getUserClass(clazz), tableInfo);
        }
        return tableInfo;
    }

    /**
     * <p>
     * 获取所有实体映射表信息
     * </p>
     *
     * @return 数据库表反射信息集合
     */
    @SuppressWarnings("unused")
    public static List<IndexInfo> getTableInfos() {
        return new ArrayList<>(TABLE_INFO_CACHE.values());
    }

    /**
     * <p>
     * 实体类反射获取表信息【初始化】
     * </p>
     *
     * @param clazz 反射实体类
     * @return 数据库表反射信息
     */
    public synchronized static IndexInfo initIndexInfo(Class<?> clazz) {
        IndexInfo indexInfo = TABLE_INFO_CACHE.get(clazz);
        if (indexInfo != null) {
            return indexInfo;
        }

        /* 没有获取到缓存信息,则初始化 */
        indexInfo = new IndexInfo(clazz);

        /* 初始化字段相关 */
        initIndexFields(clazz, indexInfo);

        /* 放入缓存 */
        TABLE_INFO_CACHE.put(clazz, indexInfo);

        /* 缓存 lambda */
        LambdaUtils.installCache(indexInfo);

        /* 自动构建 resultMap */
        indexInfo.initResultMapIfNeed();

        return indexInfo;
    }

    /**
     * <p>
     * 初始化 表主键,表字段
     * </p>
     *
     * @param clazz        实体类
     * @param indexInfo    数据库表反射信息
     */
    public static void initIndexFields(Class<?> clazz, IndexInfo indexInfo) {
        /* 数据库全局配置 */
        List<Field> list = getAllFields(clazz);

        List<IndexFieldInfo> fieldList = new ArrayList<>(list.size());
        for (Field field : list) {

            /* 有 @TableField 注解的字段初始化 */
            if (initTableFieldWithAnnotation(indexInfo, fieldList, field)) {
                continue;
            }

            /* 无 @TableField 注解的字段初始化 */
            fieldList.add(new IndexFieldInfo(field));
        }

        /* 字段列表,不可变集合 */
        indexInfo.setFieldList(Collections.unmodifiableList(fieldList));
    }

    /**
     * <p>
     * 判断主键注解是否存在
     * </p>
     *
     * @param list 字段列表
     * @return true 为存在 @TableId 注解;
     */
    public static boolean isExistTableId(List<Field> list) {
        return list.stream().anyMatch(field -> field.isAnnotationPresent(EsIndexId.class));
    }


    /**
     * <p>
     * 字段属性初始化
     * </p>
     *
     * @param tableInfo 表信息
     * @param fieldList 字段列表
     * @return true 继续下一个属性判断，返回 continue;
     */
    private static boolean initTableFieldWithAnnotation(IndexInfo tableInfo,
                                                        List<IndexFieldInfo> fieldList, Field field) {
        /* 获取注解属性，自定义字段 */
        EsField esField = field.getAnnotation(EsField.class);
        if (null == esField) {
            return false;
        }
        fieldList.add(new IndexFieldInfo(field, esField));
        return true;
    }

    /**
     * <p>
     * 判定 related 的值
     * </p>
     *
     * @param underCamel 驼峰命名
     * @param property   属性名
     * @param column     字段名
     * @return related
     */
    public static boolean checkRelated(boolean underCamel, String property, String column) {
        if (StringUtils.isNotColumnName(column)) {
            // 首尾有转义符,手动在注解里设置了转义符,去除掉转义符
            column = column.substring(1, column.length() - 1);
        }
        String propertyUpper = property.toUpperCase(Locale.ENGLISH);
        String columnUpper = column.toUpperCase(Locale.ENGLISH);
        if (underCamel) {
            // 开启了驼峰并且 column 包含下划线
            return !(propertyUpper.equals(columnUpper) ||
                propertyUpper.equals(columnUpper.replace(StringPool.UNDERSCORE, StringPool.EMPTY)));
        } else {
            // 未开启驼峰,直接判断 property 是否与 column 相同(全大写)
            return !propertyUpper.equals(columnUpper);
        }
    }

    /**
     * <p>
     * 获取该类的所有属性列表
     * </p>
     *
     * @param clazz 反射类
     * @return 属性集合
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fieldList = ReflectionKit.getFieldList(ClassUtils.getUserClass(clazz));
        return fieldList.stream()
            .filter(field -> {
                /* 过滤注解非表字段属性 */
                EsField esField = field.getAnnotation(EsField.class);
                return (esField == null || esField.exist());
            }).collect(toList());
    }

}
