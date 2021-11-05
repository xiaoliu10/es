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

import com.example.es.exception.EsCommonException;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

import static java.util.stream.Collectors.joining;

/**
 * 数据库表反射信息
 *
 * @author hubin
 * @since 2016-01-23
 */
@Data
@Setter(AccessLevel.PACKAGE)
@Accessors(chain = true)
public class IndexInfo implements Constants {

    /**
     * 实体类型
     */
    private Class<?> entityType;
    /**
     * 表名称
     */
    private String index;
    /**
     * 表映射结果集
     */
    private String resultMap;
    /**
     * 是否是需要自动生成的 resultMap
     */
    private boolean autoInitResultMap;
    /**
     * 是否是自动生成的 resultMap
     */
    private boolean initResultMap;
    /**
     * 主键是否有存在字段名与属性名关联
     * <p>true: 表示要进行 as</p>
     */
    private boolean keyRelated;
    /**
     * 表主键ID 字段名
     */
    private String keyColumn;
    /**
     * 表主键ID 属性名
     */
    private String keyProperty;
    /**
     * 表主键ID 属性类型
     */
    private Class<?> keyType;
    /**
     * 表字段信息列表
     */
    private List<IndexFieldInfo> fieldList;
    /**
     * 表字段是否启用了更新填充
     *
     * @since 3.3.0
     */
    @Getter
    @Setter(AccessLevel.NONE)
    private boolean withUpdateFill;

    public IndexInfo(Class<?> entityType) {
        this.entityType = entityType;
    }

    public <T extends EsBaseModel> T mappingData(Map<String, Object> map) {

    }

    void setFieldList(List<IndexFieldInfo> fieldList) {
        this.fieldList = fieldList;
    }

    public void initResultMapIfNeed() {
        String id = ES_PLUS + UNDERSCORE + entityType.getSimpleName();
        List<ResultMapping> resultMappings = new ArrayList<>();
        if (havePK()) {
            ResultMapping idMapping = new ResultMapping.Builder(configuration, keyProperty, keyColumn, keyType)
                    .flags(Collections.singletonList(ResultFlag.ID)).build();
            resultMappings.add(idMapping);
        }
        if (CollectionUtils.isNotEmpty(fieldList)) {
            fieldList.forEach(i -> resultMappings.add(i.getResultMapping(configuration)));
        }
        ResultMap resultMap = new ResultMap.Builder(configuration, id, entityType, resultMappings).build();
        configuration.addResultMap(resultMap);
        this.resultMap = id;
    }
}
