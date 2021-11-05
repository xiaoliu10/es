package com.example.es.config;

import com.example.es.annotation.EsDocument;
import com.example.es.annotation.EsModelConfig;
import com.example.es.exception.EsCommonException;
import com.example.es.util.IndexHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.reflections.Reflections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Set;

@Configuration
@Slf4j
@EsModelConfig(packages = {"com.example.es.domain"})
public class ElasticSearchConfig {

    /**
     * 解决netty引起的issue
     */
    @PostConstruct
    public void init() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        EsModelConfig config = ElasticSearchConfig.class.getAnnotation(EsModelConfig.class);
        String[] packages = config.packages();
        if (ArrayUtils.isEmpty(packages)){
            throw new EsCommonException("启动时请指定ES model 包路径!");
        }
        Arrays.asList(packages).parallelStream().forEach(v -> {
            Reflections reflections = new Reflections(v);
            Set<Class<?>> classes = reflections.getTypesAnnotatedWith(EsDocument.class);
            classes.parallelStream().forEach(IndexHelper::initIndexInfo);
        });

    }

}
