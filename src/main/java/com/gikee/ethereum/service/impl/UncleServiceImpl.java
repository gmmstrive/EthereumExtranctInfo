package com.gikee.ethereum.service.impl;

import com.gikee.ethereum.model.Pool;
import com.gikee.ethereum.model.Uncle;
import com.gikee.ethereum.service.PoolService;
import com.gikee.ethereum.service.UncleService;
import com.gikee.ethereum.utils.DateTransform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class UncleServiceImpl implements UncleService {

    @Autowired
    private Web3j web3j;

    @Autowired
    private PoolService poolService;

    @Autowired
    private DateTransform dateTransform;

    @Override
    public List<Uncle> etherumUncles(List<String> unclesAddress, com.gikee.ethereum.model.Block block) {

        List<Uncle> uncles = new ArrayList<>();

        try {
            if (!unclesAddress.isEmpty()) {
                Uncle uncle;
                EthBlock.Block tmpBlock;
                Pool pool;
                String miner;
                for (int i = 0; i < unclesAddress.size(); i++) {
                    uncle = new Uncle();
                    tmpBlock = web3j.ethGetUncleByBlockNumberAndIndex(DefaultBlockParameter.valueOf(block.getBlockNumber()), BigInteger.valueOf(i))
                            .sendAsync().get().getBlock();
                    miner = tmpBlock.getMiner();
                    uncle.setUncleNumber(tmpBlock.getNumber());
                    uncle.setUnclePosition(i);
                    uncle.setBlockNumber(block.getBlockNumber());
                    uncle.setBlockHash(tmpBlock.getHash());
                    uncle.setParentHash(tmpBlock.getParentHash());
                    uncle.setSha3Uncles(tmpBlock.getSha3Uncles());
                    uncle.setMiner(miner);
                    pool = poolService.getEthereumPool(miner);
                    if (pool != null) {
                        uncle.setEthereumPoolName(pool.getPoolName());
                    }
                    uncle.setDifficulty(tmpBlock.getDifficulty().toString());
                    uncle.setGasLimit(tmpBlock.getGasLimit());
                    uncle.setGasUsed(tmpBlock.getGasUsed());
                    uncle.setUnclesReward(block.getUnclesReward());
                    uncle.setBlockTimeStamp(dateTransform.getTimeStampToUTC(tmpBlock.getTimestamp().toString()));
                    block.setUncleCount(block.getUncleCount() + 1);
                    uncles.add(uncle);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return uncles;
    }
}
