package com.example.es.domain;


import com.example.es.annotation.EsDocument;
import com.example.es.annotation.EsField;
import com.example.es.util.EsBaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 *  实体类
 *
 * @author HanCL
 * @ClassName: SendMessage - SENDDATE
 */
@Data
@EsDocument(index = "send_message")
@EqualsAndHashCode(callSuper = true)
public class SendMessage extends EsBaseModel {

    private static final long serialVersionUID = -1L;

    /**
     * 编码.
     */
//    @EsField(name = "")
    private Integer id;
    /**
     * 发送对象.
     */
    private String senddest;
    /**
     * 发送手机号.
     */
    private String sendmobile;
    /**
     * 目标号码类型
                0:对方接收手机号码
                1：对方登录名.
     */
    private String desttype;
    /**
     * 发送内容.
     */
    private String sendcontent;
    /**
     * 发送日期.分片字段
     */
    @EsField("senddate")
    private BigDecimal sendDate;
    /**
     * 发送时间.
     */
    private BigDecimal sendtime;
    /**
     * 成功状.
     */
    private String backstatus;
    /**
     * 发送状态：0:未发送 1:已发送.
     */
    private String sendstatus;

}
