package com.example.es.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description: es
 * Created by 60125 on 2021/10/29 10:15
 *
 * @author 60125
 */
@Getter
@AllArgsConstructor
public enum ErrorEnum {

    SUCCESS(200, "成功"),
    ERROR(500, "失败"),
    UN_FOUND_INDEX(1001, "请求未设置index"),
    UN_FOUND_TYPE(1002, "请求未设置type"),
    ID_MUST_NOT_NO(1003, "主键不能为空"),
    ;

    private int code;

    private String desc;
}
