package com.gikee.ethereum.service;

import com.gikee.ethereum.model.Block;
import com.gikee.ethereum.model.Token;
import com.gikee.ethereum.model.TokenTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TokenTransactionService {

    List<TokenTransaction> ethereumTokenTxs(List<TransactionReceipt> txReceipts, Block block, Map<String, Set<Token>> calculatedAddress);

}
