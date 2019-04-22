package com.gikee.ethereum.dao.mongo;

import com.gikee.ethereum.model.Transaction;
import com.gikee.ethereum.utils.MongoDBDao;
import org.springframework.stereotype.Repository;

@Repository
public class MongoTransactionDao extends MongoDBDao<Transaction> {
    @Override
    protected Class<Transaction> getEntityClass() {
        return Transaction.class;
    }
}
