package com.example.es.util;

import com.alibaba.fastjson.JSON;
import com.example.es.domain.EsPage;
import com.example.es.enums.ErrorEnum;
import com.example.es.exception.EsCommonException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * ElasticSearch工具类
 *
 * @author 154594742@qq.com
 * @date 2021/3/4 19:34
 */

@Slf4j
@Component
public class ElasticSearchUtils {

    @Value("${spring.elasticsearch.rest.uris}")
    private String uris;

    @Resource
    private EsConfig esConfig;

    private RestHighLevelClient restClient;

    /**
     * 在Servlet容器初始化前执行
     */
    @PostConstruct
    private void init() {
        try {
            if (restClient != null) {
                restClient.close();
            }
            if (StringUtils.isBlank(uris)) {
                log.error("spring.elasticsearch.rest.uris is blank");
                return;
            }

            //解析yml中的配置转化为HttpHost数组
            String[] uriArr = uris.split(",");
            HttpHost[] httpHostArr = new HttpHost[uriArr.length];
            int i = 0;
            for (String uri : uriArr) {
                if (StringUtils.isEmpty(uris)) {
                    continue;
                }

                try {
                    //拆分出ip和端口号
                    String[] split = uri.split(":");
                    String host = split[0];
                    String port = split[1];
                    HttpHost httpHost = new HttpHost(host, Integer.parseInt(port), "http");
                    httpHostArr[i++] = httpHost;
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }

            RestClientBuilder builder = RestClient.builder(httpHostArr);
            restClient = new RestHighLevelClient(builder);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 创建索引
     *
     * @param req
     * @return
     */
    public <T extends EsBaseModel> boolean createIndex(EsBaseRequest<T> req) throws IOException {
        if (isIndexExist(req)) {
            log.error("Index is  exits!");
            return false;
        }
        //1.创建索引请求
        IndexRequest request = new IndexRequest(req.getIndex());
        //2.执行客户端请求
        IndexResponse indexResponse = restClient.index(request);
        return Objects.equals(RestStatus.CREATED, indexResponse.status());
    }

    /**
     * 判断索引是否存在
     *
     * @param request
     * @return boolean
     */
    public <T extends EsBaseModel> boolean isIndexExist(EsBaseRequest<T> request) throws IOException {
        GetRequest getRequest = new GetRequest(request.getIndex());
        return restClient.exists(getRequest);
    }

    /**
     * 删除索引
     *
     * @param req
     * @return
     */
    public <T extends EsBaseModel> boolean deleteIndex(EsBaseRequest<T> req) throws IOException {
        if (!isIndexExist(req)) {
            log.error("Index is not exits!");
            return false;
        }
        DeleteRequest request = new DeleteRequest(req.getIndex());
        DeleteResponse response = restClient.delete(request);
        return Objects.equals(response.status(), RestStatus.OK);
    }

    /**
     * 新增/更新数据
     *
     * @param req 要新增/更新的数据
     * @return String
     */
    public <T extends EsBaseModel> String submitData(BaseSearchRequest<T> req) throws IOException {
        if (this.existsById(req)) {
            this.updateDataByIdNoRealTime(req);
            return req.getDocumentId();
        } else {
            return addData(req);
        }
    }

    /**
     * 新增数据
     *
     * @param req 要增加的数据
     * @return String
     */
    public <T extends EsBaseModel> String addData(BaseSearchRequest<T> req) throws IOException {
        if (req.getDocumentId() != null) {
            if (this.existsById(req)) {
                this.updateDataByIdNoRealTime(req);
                return req.getDocumentId();
            }
        }

        //创建请求
        IndexRequest request = new IndexRequest(req.getIndex(), req.getType(), req.getDocumentId());
        request.timeout(TimeValue.timeValueSeconds(esConfig.getTimeout()));
        //将数据放入请求 json
        request.source(JSON.toJSONString(req.getData()), XContentType.JSON);
        //客户端发送请求
        IndexResponse response = restClient.index(request);
        log.info("添加数据成功 索引为: {}, response 状态: {}, id为: {}", req.getIndex(), response.status().getStatus(), response.getId());
        return response.getId();
    }


    /**
     * 通过ID删除数据
     *
     * @param req 索引，类似数据库
     * @return String
     */
    public <T extends EsBaseModel> String deleteDataById(BaseIdRequest<T> req) throws IOException {
        DeleteRequest request = new DeleteRequest(req.getIndex(), req.getType(), req.getDocumentId());
        DeleteResponse deleteResponse = restClient.delete(request);
        return deleteResponse.getId();
    }

    /**
     * 通过ID 更新数据
     *
     * @param req 要更新数据
     * @return String
     */
    public <T extends EsBaseModel> String updateData(BaseSearchRequest<T> req) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(req.getIndex(), req.getType(), req.getDocumentId());
        updateRequest.timeout(TimeValue.timeValueSeconds(esConfig.getTimeout()));
        updateRequest.doc(JSON.toJSONString(req.getData()), XContentType.JSON);
        UpdateResponse updateResponse = restClient.update(updateRequest);
        log.info("索引为: {}, id为: {},updateResponseID：{}, 更新数据成功", req.getIndex(), req.getDocumentId(), updateResponse.getId());
        return updateResponse.getId();
    }

    /**
     * 通过ID 更新数据,保证实时性
     *
     * @param req 要增加的数据
     * @return String
     */
    public <T extends EsBaseModel> boolean updateDataByIdNoRealTime(BaseSearchRequest<T> req) throws IOException {
        //更新请求
        UpdateRequest updateRequest = new UpdateRequest(req.getIndex(), req.getType(), req.getDocumentId());
        //保证数据实时更新
        updateRequest.setRefreshPolicy("wait_for");

        updateRequest.timeout("1s");
        updateRequest.doc(JSON.toJSONString(req.getData()), XContentType.JSON);
        //执行更新请求
        UpdateResponse response = restClient.update(updateRequest);
        log.info("索引为: {}, id为: {},updateResponseID：{}, 实时更新数据成功", req.getIndex(), req.getDocumentId(), response.getId());
        return Objects.equals(RestStatus.OK, response.status());
    }

    /**
     * 通过ID获取数据
     *
     * @param req 索引，类似数据库
     * @return
     */
    public <T extends EsBaseModel> T searchDataById(BaseSearchRequest<T> req) throws IOException {
        if (StringUtils.isBlank(req.getDocumentId())) {
            throw new EsCommonException(ErrorEnum.ID_MUST_NOT_NO);
        }
        GetRequest request = new GetRequest(req.getIndex(), req.getType(), req.getDocumentId());
        if (ArrayUtils.isNotEmpty(req.getExcludes())) {
            //只查询特定字段。如果需要查询所有字段则不设置该项。
            request.fetchSourceContext(new FetchSourceContext(true, req.getIncludes(), req.getExcludes()));
        }
        GetResponse response = restClient.get(request);
        Map<String, Object> map = response.getSourceAsMap();
        IndexInfo indexInfo = IndexHelper.getIndexInfo(req.getClazz());
        return indexInfo.mappingData(map);
    }

    /**
     * 通过ID判断文档是否存在
     *
     * @param req
     * @return
     */
    public <T extends EsBaseModel> boolean existsById(BaseSearchRequest<T> req) throws IOException {
        GetRequest request = new GetRequest(req.getIndex(), req.getType(), req.getDocumentId());
        //不获取返回的_source的上下文
        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields("_none_");
        return restClient.exists(request);
    }

    /**
     * 批量插入false成功
     *
     * @param index   索引，类似数据库
     * @param objects 数据
     * @return
     */
    public boolean bulkPost(String index, List<?> objects) {
        BulkRequest bulkRequest = new BulkRequest();
        BulkResponse response = null;
        //最大数量不得超过20万
        for (Object object : objects) {
            IndexRequest request = new IndexRequest(index);
            request.source(JSON.toJSONString(object), XContentType.JSON);
            bulkRequest.add(request);
        }
        try {
            response = restClient.bulk(bulkRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null != response && response.hasFailures();
    }


    /**
     * 获取低水平客户端
     *
     * @return
     */
    public RestClient getLowLevelClient() {
        return restClient.getLowLevelClient();
    }

    /**
     * 高亮结果集 特殊处理
     * map转对象 JSONObject.parseObject(JSONObject.toJSONString(map), Content.class)
     *
     * @param searchResponse
     * @param highlightField
     */
    private List<Map<String, Object>> setSearchResponse(SearchResponse searchResponse, String highlightField) {
        //解析结果
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            Map<String, HighlightField> high = hit.getHighlightFields();
            HighlightField title = high.get(highlightField);
            //原来的结果
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //解析高亮字段,将原来的字段换为高亮字段
            if (title != null) {
                Text[] texts = title.fragments();
                StringBuilder nTitle = new StringBuilder();
                for (Text text : texts) {
                    nTitle.append(text);
                }
                //替换
                sourceAsMap.put(highlightField, nTitle.toString());
            }
            list.add(sourceAsMap);
        }
        return list;
    }

    /**
     * 查询
     *
     * @param index          索引名称
     * @param query          查询条件
     * @param highlightField 高亮字段
     * @return
     */
    public List<Map<String, Object>> searchListData(String index,
                                                    SearchSourceBuilder query,
                                                    String highlightField) throws IOException {
        SearchRequest request = new SearchRequest(index);

        //高亮
        HighlightBuilder highlight = new HighlightBuilder();
        highlight.field(highlightField);
        //关闭多个高亮
        highlight.requireFieldMatch(false);
        highlight.preTags("<span style='color:red'>");
        highlight.postTags("</span>");
        query.highlighter(highlight);
        //不返回源数据。只有条数之类的数据。
        //builder.fetchSource(false);
        request.source(query);
        SearchResponse response = restClient.search(request);
        log.info("totalHits:" + response.getHits().getTotalHits());
        if (response.status().getStatus() == 200) {
            // 解析对象
            return setSearchResponse(response, highlightField);
        }
        return null;
    }

    /**
     * 分页查询
     * @param req
     * @param <T>
     * @return
     */
    public <T extends EsBaseModel> EsPage<T> searchPageData(BaseSearchRequest<T> req) throws IOException {
        SearchRequest request = new SearchRequest(req.getIndex());
//        SearchRequestBuilder requestBuilder = new SearchRequestBuilder();
//        if (StringUtils.isNotEmpty(type)) {
//            request.set(type.split(","));
//        }
//        requestBuilder.setSearchType(SearchType.QUERY_THEN_FETCH);
//
//        // 需要显示的字段，逗号分隔（缺省为全部字段）
//        if (ArrayUtils.isNotEmpty(req.getExcludes())) {
//            requestBuilder.setFe(new FetchSourceContext(true, req.getIncludes(), req.getExcludes()));
//        }
//
//        //排序字段
//        if (StringUtils.isNotEmpty(sortField)) {
//            requestBuilder.addSort(sortField, SortOrder.DESC);
//        }
//
//        // 高亮（xxx=111,aaa=222）
//        if (StringUtils.isNotEmpty(highlightField)) {
//            HighlightBuilder highlightBuilder = new HighlightBuilder();
//
//            //highlightBuilder.preTags("<span style='color:red' >");//设置前缀
//            //highlightBuilder.postTags("</span>");//设置后缀
//
//            // 设置高亮字段
//            highlightBuilder.field(highlightField);
//            requestBuilder.highlighter(highlightBuilder);
//        }
//
//        //requestBuilder.setQuery(QueryBuilders.matchAllQuery());
//        requestBuilder.setQuery(query);
//
//        // 分页应用
//        requestBuilder.setFrom(startPage).setSize(pageSize);
//
//        // 设置是否按查询匹配度排序
//        requestBuilder.setExplain(true);

        //打印的内容 可以在 Elasticsearch head 和 Kibana  上执行查询
//        log.info("\n{}", requestBuilder);
//
//        // 执行搜索,返回搜索响应信息
//        SearchResponse response = restClient.search(requestBuilder.request());
//        long totalHits = response.getHits().getTotalHits();
//        long length = response.getHits().getHits().length;
//
//        log.debug("共查询到[{}]条数据,处理数据条数[{}]", totalHits, length);
//
//        if (Objects.equals(response.status(), RestStatus.OK)) {
//            // 解析对象
//            List<Map<String, Object>> sourceList = setSearchResponse(response, highlightField);
//
//            return new EsPage(req.getPageNo(), req.getPageSize(), (int) totalHits, sourceList);
//        }
//
        return null;

    }
}