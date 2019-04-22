package com.gikee.ethereum.service;

import com.gikee.ethereum.model.Block;
import com.gikee.ethereum.model.Transaction;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TransactionService {

    List<Transaction> getExternalTxs(List<EthBlock.TransactionResult> txs, Block block, List<TransactionReceipt> txReceipts, String blockHex,
                                     Map<String, String> txType, Map<String, String> isError, Set<String> address);

}
