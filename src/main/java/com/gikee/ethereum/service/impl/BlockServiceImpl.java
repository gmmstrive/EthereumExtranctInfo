package com.gikee.ethereum.service.impl;

import com.gikee.ethereum.model.Block;
import com.gikee.ethereum.model.Pool;
import com.gikee.ethereum.service.BlockService;
import com.gikee.ethereum.service.CommonService;
import com.gikee.ethereum.service.PoolService;
import com.gikee.ethereum.utils.DateTransform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

@Service
public class BlockServiceImpl implements BlockService {

    private static final boolean returnFullTransactionObjects = true;

    @Autowired
    private Web3j web3j;

    @Autowired
    private PoolService poolService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private DateTransform dateTransform;

    @Override
    public EthBlock.Block ethereumBlock(Integer blockNumber) {
        try {
            return web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNumber)), returnFullTransactionObjects)
                    .sendAsync().get().getBlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Block ethereumBlock(EthBlock.Block tmpBlock) {
        Block block = new Block();
        block.setBlockNumber(tmpBlock.getNumber());
        block.setBlockHash(tmpBlock.getHash());
        block.setMiner(tmpBlock.getMiner());
        Pool pool = poolService.getEthereumPool(tmpBlock.getMiner());
        if (pool != null) {
            block.setEthereumPoolName(pool.getPoolName());
        }
        block.setDifficulty(tmpBlock.getDifficulty().toString());
        block.setTotalDifficulty(tmpBlock.getTotalDifficulty().toString());
        block.setExtraData(commonService.hexToASCII(tmpBlock.getExtraData()));
        block.setSize(tmpBlock.getSize());
        block.setGasUsed(tmpBlock.getGasUsed());
        block.setGasLimit(tmpBlock.getGasLimit());
        block.setNonce(tmpBlock.getNonce().toString());
        block.setParentHash(tmpBlock.getParentHash());
        block.setSha3Uncles(tmpBlock.getSha3Uncles());
        block.setTransactionTotalValue(block.getInternalTransactionValue().add(block.getExternalTransactionValue()));
        block.setBlockTimeStamp(dateTransform.getTimeStampToUTC(tmpBlock.getTimestamp().toString()));
        return block;
    }

}
