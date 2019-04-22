package com.gikee.ethereum.dao.es;

import com.gikee.ethereum.model.Transaction;
import com.gikee.ethereum.utils.ElasticSearchUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Slf4j
@Repository
public class ESTransactionDao extends ElasticSearchUtil<Transaction> {

    private static final String INDEX = "ethereum_transactions";
    private static final String TYPE = "ethTransactions";

    @Override
    protected Class getEntityClass() {
        return Transaction.class;
    }

    public void elasticSearchBulk(List<Transaction> list) {
        log.info("------> 批量插入 ETHTransaction 开始 <------");
        try {
            super.elasticSearchBulk(list, INDEX, TYPE);
        } catch (IOException e) {
            log.error("------> 批量插入 ETHTransaction 出错 , 错误信息 : {}", e.getMessage());
            e.printStackTrace();
        }
        log.info("------> 批量插入 ETHTransaction 结束 <------");
    }

    public void elasticSearchDeleteByQuery(String deleteBlockNumber) {
        log.info("------> 批量删除 ETHTransaction 开始 <------");
        try {
            super.elasticSearchDeleteByQuery(INDEX, TYPE, deleteBlockNumber);
        } catch (IOException e) {
            log.error("------> 批量删除 ETHTransaction 出错 , 错误信息 : {}", e.getMessage());
            e.printStackTrace();
        }
        log.info("------> 批量删除 ETHTransaction 结束 <------");
    }
}
