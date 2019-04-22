package com.gikee.ethereum.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class ElasticSearchUtil<T> {

    private static final String BLOCKNUMBER = "blockNumber";

    @Autowired
    protected RestHighLevelClient restHighLevelClient;

    /**
     * 反射获取泛型类型
     *
     * @return
     */
    protected abstract Class<T> getEntityClass();


    public void elasticSearchBulk(List<T> list, String index, String type) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        for (T l : list) {
            bulkRequest.add(new IndexRequest(index, type).source(JSON.parseObject(JSON.toJSONString(l))));
        }
        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    public void elasticSearchBulk(List<T> list, List<String> ids, String index, String type) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        for (int i = 0; i < list.size(); i++) {
            bulkRequest.add(new IndexRequest(index, type, ids.get(i)).source(JSON.parseObject(JSON.toJSONString(list.get(i)))));
        }
        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    public void elasticSearchBulk(T t, String index, String type) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(new IndexRequest(index, type).source(JSON.parseObject(JSON.toJSONString(t))));
        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    public void elasticSearchBulk(T t, String id, String index, String type) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(new IndexRequest(index, type, id).source(JSON.parseObject(JSON.toJSONString(t))));
        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    public List<T> elasticSearchMultiString(List<String> list, String index, String type) throws IOException {
        MultiGetRequest multiGetRequest = new MultiGetRequest();
        List<T> result = new ArrayList<>();
        MultiGetResponse multiGetResponse;
        Iterator<MultiGetItemResponse> iterator;
        for (String l : list) {
            multiGetRequest.add(new MultiGetRequest.Item(index, type, l));
        }
        multiGetResponse = restHighLevelClient.mget(multiGetRequest, RequestOptions.DEFAULT);
        iterator = multiGetResponse.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> source = iterator.next().getResponse().getSource();
            if (source != null) {
                result.add(JSON.parseObject(JSON.toJSONString(source), getEntityClass()));
            }
        }
        return result;
    }

    public void elasticSearchDeleteByQuery(String index, String type, String deleteBlockNumber) throws IOException {
        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(index);
        deleteByQueryRequest.setDocTypes(type);
        deleteByQueryRequest.setQuery(new TermQueryBuilder(BLOCKNUMBER, deleteBlockNumber));
        deleteByQueryRequest.setIndicesOptions(IndicesOptions.LENIENT_EXPAND_OPEN);
        restHighLevelClient.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
    }
}
