package com.example.es.util;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Description: es
 * Created by 60125 on 2021/11/3 14:10
 *
 * @author 60125
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public abstract class BaseIdRequest<T extends EsBaseModel> extends EsBaseRequest<T> {

    private String documentId;
}
