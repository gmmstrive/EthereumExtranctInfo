package com.gikee.ethereum.service.impl;

import com.gikee.ethereum.model.Block;
import com.gikee.ethereum.model.Transaction;
import com.gikee.ethereum.service.CommonService;
import com.gikee.ethereum.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final String EXTERNALTRANSACTION = "externalTransaction";

    @Autowired
    private CommonService commonService;

    @Override
    public List<Transaction> getExternalTxs(List<EthBlock.TransactionResult> txs, Block block, List<TransactionReceipt> txReceipts, String blockHex,
                                            Map<String, String> txType, Map<String, String> isError, Set<String> address) {

        List<Transaction> externalTxs = new ArrayList<>();

        if (!txReceipts.isEmpty()) {
            Transaction tx;
            EthBlock.TransactionObject txObj;
            TransactionReceipt txr;
            BigInteger gas;
            BigInteger gasUsed;
            BigInteger gasPrice;
            BigDecimal txFree;
            String type;
            for (int i = 0; i < txReceipts.size(); i++) {
                tx = new Transaction();
                txr = txReceipts.get(i);
                txObj = (EthBlock.TransactionObject) txs.get(i).get();
                // 获取 gas
                gas = txObj.getGas();
                // 获取 gasUsed
                gasUsed = txr.getGasUsed();
                // 获取 gasPrice
                gasPrice = txObj.getGasPrice();
                // 获取 txFree
                txFree = commonService.digitalConversion(gasUsed.multiply(gasPrice));
                // 设置外部交易详情
                tx.setBlockNumber(txObj.getBlockNumber());
                tx.setBlockHash(txObj.getBlockHash());
                tx.setStatusOK(txr.isStatusOK());
                if (isError.containsKey(txObj.getHash())) {
                    tx.setError(isError.get(txObj.getHash()));
                }
                tx.setTransactionHash(txObj.getHash());
                tx.setTransactionIndex(txObj.getTransactionIndex());
                tx.setFromAddress(txObj.getFrom());
                type = txType.get(txObj.getHash());
                if ("create".equals(type)) {
                    tx.setToAddress(txObj.getCreates());
                } else {
                    tx.setToAddress(txObj.getTo());
                }
                tx.setValueNumber(commonService.digitalConversion(txObj.getValue()));
                tx.setGas(gas);
                tx.setGasUsed(gasUsed);
                tx.setGasPrice(commonService.digitalConversion(gasPrice).toPlainString());
                tx.setTransactionGasRatio(new BigDecimal(gas).divide(new BigDecimal(gasUsed), 4, BigDecimal.ROUND_HALF_UP));
                tx.setTransactionFee(txFree);
                tx.setNonce(txObj.getNonce());
                tx.setType(type);
                tx.setTransactionType(EXTERNALTRANSACTION);
                tx.setBlockTimeStamp(block.getBlockTimeStamp());
                // 累加外部交易条数
                block.setExternalTransactionCount(block.getExternalTransactionCount() + 1);
                // 累加外部交易数量
                block.setExternalTransactionValue(block.getExternalTransactionValue().add(tx.getValueNumber()));
                // 累加交易费
                block.setFeesReward(block.getFeesReward().add(txFree));
                // 获取参与交易地址
                if (tx.getFromAddress() != null) address.add(tx.getFromAddress());
                if (tx.getToAddress() != null) address.add(tx.getToAddress());
                // 添加到外部交易集合
                externalTxs.add(tx);
            }
        }
        return externalTxs;
    }

}
