package com.gikee.ethereum.service;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface CommonService {

    String hexToASCII(String hexValue);

    String blockHex(BigInteger blockNumber);

    String blockHex(String blockNumber);

    String ethereumVersion();

    Integer ethereumBlockNumber();

    BigDecimal digitalConversion(String value);

    BigDecimal digitalConversion(BigInteger value);

    BigDecimal digitalConversion(BigDecimal value);

    String getTokenBalance(String fromAddress, String contractAddress, int decimal);

    String getTokenBalance(String fromAddress, String contractAddress);

    String getEthereumBalance(String address, BigInteger blockNumber);

    

}
