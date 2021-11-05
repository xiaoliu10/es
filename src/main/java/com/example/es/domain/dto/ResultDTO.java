package com.example.es.domain.dto;

import com.example.es.enums.ErrorEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;

/**
 * Description: es
 * Created by 60125 on 2021/10/29 10:13
 *
 * @author 60125
 */
@Data
public class ResultDTO<T> implements Serializable {

    private static final long serialVersionUID = -1L;

    private long total;

    private T data;

    private boolean success;

    private String message;

    private int code;

    public static <T> ResultDTO<T> buildSuccess(){
        ResultDTO<T> dto = new ResultDTO<>();
        dto.setSuccess(true);
        dto.setCode(ErrorEnum.SUCCESS.getCode());
        dto.setMessage(ErrorEnum.SUCCESS.getDesc());
        return dto;
    }

    public static <T> ResultDTO<T> buildError(ErrorEnum errorEnum){
        ResultDTO<T> dto = new ResultDTO<>();
        dto.setSuccess(false);
        dto.setCode(errorEnum.getCode());
        dto.setMessage(errorEnum.getDesc());
        return dto;
    }

    public static <T> ResultDTO<T> buildSuccess(T data, long total){
        ResultDTO<T> dto = new ResultDTO<>();
        if (data instanceof Collection){
            dto.setData(data);
            dto.setTotal(total);
        }
        dto.setSuccess(true);
        dto.setCode(ErrorEnum.SUCCESS.getCode());
        dto.setMessage(ErrorEnum.SUCCESS.getDesc());
        return dto;
    }

}
