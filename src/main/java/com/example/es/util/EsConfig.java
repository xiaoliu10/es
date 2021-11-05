package com.example.es.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Description: es
 * Created by 60125 on 2021/11/3 9:18
 *
 * @author 60125
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "cn.wengine.es")
public class EsConfig {

    private long timeout;

    private String version;



}
