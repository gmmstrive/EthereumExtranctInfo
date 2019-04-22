package com.gikee.ethereum.service;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.List;

public interface TransactionReceiptService {

    List<TransactionReceipt> ethereumTxReceipts(String blockHex);

}
