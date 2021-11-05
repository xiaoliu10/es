package com.example.es;

import com.example.es.domain.EsPage;
import com.example.es.domain.SendMessage;
import com.example.es.util.*;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * Description: es
 * Created by 60125 on 2021/11/1 9:54
 *
 * @author 60125
 */
@SpringBootTest(classes = EsApplication.class)
@RunWith(SpringRunner.class)
public class EsTest {

    @Resource
    private ElasticSearchUtils elasticSearchUtils;

    @Test
    public void testCreateIndex(){
        SendMessageSearchRequest req = new SendMessageSearchRequest();
        try {
            boolean flag = elasticSearchUtils.createIndex(req);
            System.out.println(flag);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testExist(){
        SendMessageSearchRequest req = new SendMessageSearchRequest();
        try {
            boolean exist = elasticSearchUtils.isIndexExist(req);
            System.out.println(exist);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEsGet(){
        SendMessageSearchRequest request = new SendMessageSearchRequest();
        request.setDocumentId("283972");
        try {
            SendMessage message = elasticSearchUtils.searchDataById(request);
            System.out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSearchPage() throws IOException {
        BaseSearchRequest<SendMessage> request = new SendMessageSearchRequest();
        request.setQueryBuilder(EsQueryWrapper.builder(SendMessage.class)
                .mustEq(SendMessage::getSendDate, "20191125")
                .mustLike(SendMessage::getSendcontent, "xxx")
                .mustLeftLike(SendMessage::getSendcontent, "xxx")
                .mustNotEq(SendMessage::getSendDate, "xxxx")
                .orEq(SendMessage::getSendDate, "xxx").build());

//        request.buildQuery().mustEq(SendMessage::getSenddate, sendMessage.getSenddate());
//        EsPage<SendMessage> sendMessageEsPage = elasticSearchUtils.searchDataById(request);
    }

    @Test
    public void  testEsSearch() throws IOException {
        BaseSearchRequest<SendMessage> request = new SendMessageSearchRequest();
        request.setQueryBuilder(EsQueryWrapper.builder(SendMessage.class)
                .mustEq(SendMessage::getSendDate, "20191125")
                .mustLike(SendMessage::getSendcontent, "xxx")
                .mustLeftLike(SendMessage::getSendcontent, "xxx")
                .mustNotEq(SendMessage::getSendDate, "xxxx").build());
//        builder.must(QueryBuilders.termQuery("mobile", mobile));
        EsPage<SendMessage> page = elasticSearchUtils.searchPageData(request);
        System.out.println(page);
    }
}
