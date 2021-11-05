package com.example.es.exception;

import com.example.es.enums.ErrorEnum;
import lombok.Data;

/**
 * Description: es
 * Created by 60125 on 2021/11/3 10:50
 *
 * @author 60125
 */
@Data
public class EsCommonException extends RuntimeException {

    private String code;

    private String msg;

    public EsCommonException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public EsCommonException(String msg, Throwable e) {
        super(e);
        this.code = code;
        this.msg = msg;
    }

    public EsCommonException(String msg) {
        super(msg);
        this.code = String.valueOf(ErrorEnum.ERROR.getCode());
        this.msg = msg;
    }

    public EsCommonException(ErrorEnum errorEnum) {
        super(errorEnum.getDesc());
        this.code = String.valueOf(errorEnum.getCode());
        this.msg = errorEnum.getDesc();
    }
}
