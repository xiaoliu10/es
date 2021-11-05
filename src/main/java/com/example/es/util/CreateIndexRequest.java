package com.example.es.util;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;



/**
 * Description: es
 * Created by 60125 on 2021/11/3 11:22
 *
 * @author 60125
 */

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateIndexRequest<T extends EsBaseModel> extends EsBaseRequest<T> {

}
