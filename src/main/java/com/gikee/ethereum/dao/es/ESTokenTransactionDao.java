package com.gikee.ethereum.dao.es;



import com.gikee.ethereum.model.TokenTransaction;
import com.gikee.ethereum.utils.ElasticSearchUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Slf4j
@Repository
public class ESTokenTransactionDao extends ElasticSearchUtil<TokenTransaction> {

    private static final String INDEX = "ethereum_token_transactions";
    private static final String TYPE = "ethTokenTransactions";

    @Override
    protected Class<TokenTransaction> getEntityClass() {
        return TokenTransaction.class;
    }

    public void elasticSearchBulk(List<TokenTransaction> list) {
        log.info("------> 批量插入 ETHTokenTransaction 开始 <------");
        try {
            super.elasticSearchBulk(list, INDEX, TYPE);
        } catch (IOException e) {
            log.error("------> 批量插入 ETHTokenTransaction 出错 , 错误信息 : {}", e.getMessage());
            e.printStackTrace();
        }
        log.info("------> 批量插入 ETHTokenTransaction 结束 <------");
    }

    public void elasticSearchDeleteByQuery(String deleteBlockNumber) {
        log.info("------> 批量删除 ETHTokenTransaction 开始 <------");
        try {
            super.elasticSearchDeleteByQuery(INDEX, TYPE, deleteBlockNumber);
        } catch (IOException e) {
            log.error("------> 批量删除 ETHTokenTransaction 出错 , 错误信息 : {}", e.getMessage());
            e.printStackTrace();
        }
        log.info("------> 批量删除 ETHTokenTransaction 结束 <------");
    }

}
