package com.gikee.ethereum.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@Document(collection = "Transaction")
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigInteger blockNumber;
    private String blockHash;
    private boolean statusOK = true;
    private String error;
    private String transactionHash;
    private BigInteger transactionIndex;
    private String fromAddress;
    private String toAddress;
    private BigDecimal valueNumber;
    private BigInteger gas;
    private BigInteger gasUsed;
    private String gasPrice;
    private BigDecimal transactionGasRatio;
    private BigDecimal transactionFee;
    private BigInteger nonce;
    private String type;
    private String transactionType;
    private String blockTimeStamp;

}
