package com.gikee.ethereum.dao.mongo;

import com.gikee.ethereum.model.Block;
import com.gikee.ethereum.utils.MongoDBDao;
import org.springframework.stereotype.Repository;

@Repository
public class MongoBlockDao extends MongoDBDao<Block> {

    @Override
    protected Class<Block> getEntityClass() {
        return Block.class;
    }

}
