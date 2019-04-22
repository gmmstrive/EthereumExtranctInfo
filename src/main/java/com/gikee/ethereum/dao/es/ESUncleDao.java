package com.gikee.ethereum.dao.es;

import com.gikee.ethereum.model.Uncle;
import com.gikee.ethereum.utils.ElasticSearchUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Slf4j
@Repository
public class ESUncleDao extends ElasticSearchUtil<Uncle> {

    private static final String INDEX = "ethereum_uncle";
    private static final String TYPE = "ethUncle";

    @Override
    protected Class getEntityClass() {
        return Uncle.class;
    }

    public void elasticSearchBulk(List<Uncle> list) {
        log.info("------> 批量插入 ETHUncle 开始 <------");
        try {
            super.elasticSearchBulk(list, INDEX, TYPE);
        } catch (IOException e) {
            log.error("------> 批量插入 ETHUncle 出错 , 错误信息 : {}", e.getMessage());
            e.printStackTrace();
        }
        log.info("------> 批量插入 ETHUncle 结束 <------");
    }

    public void elasticSearchDeleteByQuery(String deleteBlockNumber) {
        log.info("------> 批量删除 ETHUncle 开始 <------");
        try {
            super.elasticSearchDeleteByQuery(INDEX, TYPE, deleteBlockNumber);
        } catch (IOException e) {
            log.error("------> 批量删除 ETHUncle 出错 , 错误信息 : {}", e.getMessage());
            e.printStackTrace();
        }
        log.info("------> 批量删除 ETHUncle 结束 <------");
    }
}
