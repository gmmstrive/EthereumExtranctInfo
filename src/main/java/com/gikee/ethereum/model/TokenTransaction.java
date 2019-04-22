package com.gikee.ethereum.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@Document(collection = "TokenTransaction")
public class TokenTransaction implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigInteger blockNumber;
    private String blockHash;
    private String transactionHash;
    private BigInteger transactionIndex;
    private BigInteger logIndex;
    private String logType;
    private String tokenAddress;
    private String tokenType;
    private String fromAddress;
    private String toAddress;
    private BigDecimal valueNumber;
    private String tokenID;
    private String symbol;
    private String blockTimeStamp;

}
