package com.gikee.ethereum.service;

import com.gikee.ethereum.model.Block;
import org.web3j.protocol.core.methods.response.EthBlock;

public interface BlockService {

    EthBlock.Block ethereumBlock(Integer blockNumber);

    Block ethereumBlock(EthBlock.Block tmpBlock);

}
