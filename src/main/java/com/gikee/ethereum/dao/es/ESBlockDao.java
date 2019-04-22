package com.gikee.ethereum.dao.es;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gikee.ethereum.model.Block;
import com.gikee.ethereum.utils.ElasticSearchUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Slf4j
@Repository
public class ESBlockDao extends ElasticSearchUtil<Block> {

    private static final String INDEX = "ethereum_block";
    private static final String TYPE = "ethBlock";
    private static final String BLOCKNUMBER = "blockNumber";

    @Override
    protected Class<Block> getEntityClass() {
        return Block.class;
    }

    public void elasticSearchBulk(Block block) {
        log.info("------> 开始向 ES 写入块信息 <------");
        BulkRequest bulkRequest = new BulkRequest();
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(block));
        //getConversion(jsonObject);
        try {
            bulkRequest.add(new IndexRequest(INDEX, TYPE, jsonObject.getString(BLOCKNUMBER)).source(jsonObject));
            restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("向 ES 写入块信息出错,错误信息 : {}", e.getMessage());
            e.printStackTrace();
        }
        log.info("------> 向 ES 写入块信息结束 <------");
    }

    public void elasticSearchDeleteByQuery(String deleteBlockNumber) {
        log.info("------> 批量删除 ETHBlock 开始 <------");
        try {
            super.elasticSearchDeleteByQuery(INDEX, TYPE, deleteBlockNumber);
        } catch (IOException e) {
            log.error("------> 批量删除 ETHBlock 出错 , 错误信息 : {}", e.getMessage());
            e.printStackTrace();
        }
        log.info("------> 批量删除 ETHBlock 结束 <------");
    }


}
