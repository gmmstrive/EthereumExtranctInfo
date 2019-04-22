package com.gikee.ethereum.service;

import com.gikee.ethereum.model.Pool;

import java.util.List;

public interface PoolService {

    List<Pool> getEthereumPools();

    Pool getEthereumPool(String poolAddress);

}
