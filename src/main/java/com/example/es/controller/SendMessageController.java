package com.example.es.controller;

import com.example.es.domain.EsPage;
import com.example.es.domain.SendMessage;
import com.example.es.domain.dto.ResultDTO;
import com.example.es.util.Constant;
import com.example.es.util.ElasticsearchUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Description: es
 * Created by 60125 on 2021/10/29 10:12
 *
 * @author 60125
 */
@RestController
public class SendMessageController {

    @RequestMapping(value = "/list")
    public ResultDTO<List<SendMessage>> listSendMessage(@RequestParam("sendDate") Integer sendDate,
                                                        @RequestParam("sendTime") Integer sendTime,
                                                        @RequestParam("mobile") String mobile,
                                                        @RequestParam("pageSize") Integer pageSize,
                                                        @RequestParam("pageNo") Integer pageNo){

        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(QueryBuilders.termQuery("senddate", sendDate));
        builder.must(QueryBuilders.termQuery("sendtime", sendTime));
        builder.must(QueryBuilders.termQuery("mobile", mobile));
        EsPage page = ElasticsearchUtil.searchDataPage(Constant.SEND_MESSAGE, null, pageNo, pageSize, builder, null,
                null, null);
        System.out.println(page);
        return ResultDTO.buildSuccess();
    }

    public static void main(String[] args) {
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(QueryBuilders.termQuery("senddate", 20191125));
//        builder.must(QueryBuilders.termQuery("mobile", mobile));
        EsPage page = ElasticsearchUtil.searchDataPage(Constant.SEND_MESSAGE, null, 1, 10, builder, null,
                null, null);
        System.out.println(page);
    }

}
