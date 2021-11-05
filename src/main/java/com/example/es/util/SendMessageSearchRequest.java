package com.example.es.util;

import com.example.es.domain.SendMessage;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Description: es
 * Created by 60125 on 2021/11/4 10:14
 *
 * @author 60125
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class SendMessageSearchRequest extends BaseSearchRequest<SendMessage>{

}
