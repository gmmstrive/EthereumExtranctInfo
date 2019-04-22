package com.gikee.ethereum.dao.mongo;

import com.gikee.ethereum.model.TokenTransaction;
import com.gikee.ethereum.utils.MongoDBDao;
import org.springframework.stereotype.Repository;

@Repository
public class MongoTokenTransactionDao extends MongoDBDao<TokenTransaction> {
    @Override
    protected Class<TokenTransaction> getEntityClass() {
        return TokenTransaction.class;
    }
}
