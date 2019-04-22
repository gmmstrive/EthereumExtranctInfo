package com.gikee.ethereum.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@Document(collection = "Block")
public class Block implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigInteger blockNumber;
    private String blockHash;
    private String miner;
    private String difficulty;
    private String totalDifficulty;
    private String extraData;
    private BigInteger size;
    private BigInteger gasUsed;
    private BigInteger gasLimit;
    private String nonce;
    private String parentHash;
    private String sha3Uncles;
    private Integer externalTransactionCount = 0;                            // ETH 外部交易条数
    private Integer internalTransactionCount = 0;                            // ETH 内部交易条数
    private BigDecimal transactionTotalValue = BigDecimal.ZERO;              // ETH 交易总数量
    private BigDecimal externalTransactionValue = BigDecimal.ZERO;           // ETH 外部交易总数量
    private BigDecimal internalTransactionValue = BigDecimal.ZERO;           // ETH 内部交易总数量
    private Integer erc20TransactionCount = 0;
    private Integer erc721TransactionCount = 0;
    private Integer minedReward;                                             // 挖矿奖励
    private BigDecimal feesReward = BigDecimal.ZERO;                         // 交易费奖励
    private BigDecimal unclesInclusionReward;                                // 叔块包含奖励
    private Integer uncleCount = 0;
    private BigDecimal blockReward = BigDecimal.ZERO;                        // 块奖励(挖矿奖励+交易费奖励+叔块包含奖励)
    private BigDecimal unclesReward = BigDecimal.ZERO;                       // 叔块奖励
    private String ethereumPoolName;
    private String blockTimeStamp;

    public void setMinedReward() {
        if (blockNumber.compareTo(BigInteger.valueOf(4370000)) >= 0) {
            if (blockNumber.compareTo(BigInteger.valueOf(7280000)) >= 0) {
                this.minedReward = 2;
            } else {
                this.minedReward = 3;
            }
        } else {
            this.minedReward = 5;
        }
    }

    public void setUnclesInclusionReward(BigDecimal blockReward) {
        setMinedReward();
        this.unclesInclusionReward = blockReward.subtract(new BigDecimal(this.minedReward));
    }

}
