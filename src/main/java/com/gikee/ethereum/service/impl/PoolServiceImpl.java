package com.gikee.ethereum.service.impl;

import com.gikee.ethereum.dao.mysql.PoolDao;
import com.gikee.ethereum.model.Pool;
import com.gikee.ethereum.service.PoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@CacheConfig(cacheNames = "Pool")
@Slf4j
@Service
public class PoolServiceImpl implements PoolService {

    @Autowired
    private PoolDao poolDao;

    @Cacheable
    @Override
    public List<Pool> getEthereumPools() {
        return poolDao.getEthereumPools();
    }

    @Cacheable
    @Override
    public Pool getEthereumPool(String poolAddress) {
        return poolDao.getEthereumPool(poolAddress);
    }

}
