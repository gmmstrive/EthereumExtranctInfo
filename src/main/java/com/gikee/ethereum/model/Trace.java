package com.gikee.ethereum.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

@Data
public class Trace implements Serializable {

    private static final long serialVersionUID = 1L;

    private TraceAction action;
    private String blockHash;
    private BigInteger blockNumber;
    private String error;
    private TraceResult result;
    private String subTraces;
    private List<Integer> traceAddress;
    private String transactionHash;
    private BigInteger transactionPosition;
    private String type;

}
