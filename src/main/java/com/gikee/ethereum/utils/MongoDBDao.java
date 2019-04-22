package com.gikee.ethereum.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Collection;
import java.util.List;

/**
 * @param <T>
 * @author lucas
 * @Date 2019.03.25
 */
public abstract class MongoDBDao<T> {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 反射获取泛型类型
     *
     * @return
     */
    protected abstract Class<T> getEntityClass();

    /**
     * 单条插入
     *
     * @param t
     */
    public void insertCollection(T t) {
        this.mongoTemplate.save(t);
    }

    /**
     * 批量插入
     *
     * @param list
     */
    public int insertCollection(List<T> list) {
        Collection<T> insert = this.mongoTemplate.insert(list, this.getEntityClass());
        return insert.size();
    }

}