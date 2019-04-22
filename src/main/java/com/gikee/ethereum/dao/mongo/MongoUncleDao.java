package com.gikee.ethereum.dao.mongo;

import com.gikee.ethereum.model.Uncle;
import com.gikee.ethereum.utils.MongoDBDao;
import org.springframework.stereotype.Repository;

@Repository
public class MongoUncleDao extends MongoDBDao<Uncle> {
    @Override
    protected Class<Uncle> getEntityClass() {
        return Uncle.class;
    }
}
