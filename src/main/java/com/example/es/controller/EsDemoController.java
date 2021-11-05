package com.example.es.controller;

import com.example.es.domain.SendMessage;
import com.example.es.util.CreateIndexRequest;
import com.example.es.util.ElasticSearchUtils;
import com.example.es.util.EsBaseModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * Description: es
 * Created by 60125 on 2021/11/3 11:25
 *
 * @author 60125
 */
@RestController
public class EsDemoController {

    @Resource
    private ElasticSearchUtils elasticSearchUtils;

    @RequestMapping("/createIndex")
    public void createIndex(@RequestParam("index") String index){
        CreateIndexRequest<SendMessage> indexRequest = new CreateIndexRequest<>();
        try {
            boolean flag = elasticSearchUtils.createIndex(indexRequest);
            System.out.println(flag);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
