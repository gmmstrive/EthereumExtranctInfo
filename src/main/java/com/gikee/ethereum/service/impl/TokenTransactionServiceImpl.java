package com.gikee.ethereum.service.impl;

import com.gikee.ethereum.model.Block;
import com.gikee.ethereum.model.Token;
import com.gikee.ethereum.model.TokenTransaction;
import com.gikee.ethereum.service.CommonService;
import com.gikee.ethereum.service.TokenService;
import com.gikee.ethereum.service.TokenTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TokenTransactionServiceImpl implements TokenTransactionService {

    private static final String HEX = "0x";
    private static final String TRANSFER = "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef";
    private static final String ERC20 = "ERC20";
    private static final String ERC721 = "ERC721";

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CommonService commonService;

    @Override
    public List<TokenTransaction> ethereumTokenTxs(List<TransactionReceipt> txReceipts, Block block, Map<String, Set<Token>> calculatedAddress) {

        List<TokenTransaction> tokenTxs = new ArrayList<>();

        List<Log> tokenLogs = txReceipts.parallelStream().map(TransactionReceipt::getLogs)
                .flatMap(log -> log.parallelStream()
                        .filter(l -> !l.getTopics().isEmpty() && TRANSFER.equals(l.getTopics().get(0))))
                .collect(Collectors.toList());

        if (!tokenLogs.isEmpty()) {
            TokenTransaction tokenTx;
            List<String> topics;
            Token token;
            String data;
            BigDecimal decimals;
            String fromAddress;
            String toAddress;
            for (Log log : tokenLogs) {
                tokenTx = new TokenTransaction();
                topics = log.getTopics();
                data = log.getData();
                if (!topics.isEmpty() && topics.size() >= 3) {
                    token = tokenService.getFinalETHTokenInfo(log.getAddress());
                    if (token != null) { //TRANSFER.equals(topics.get(0)) &&
                        if (ERC20.equals(token.getType())) {
                            decimals = new BigDecimal(Math.pow(10, token.getDecimals()));
                            fromAddress = topics.get(1).substring(topics.get(1).length() - 40);
                            toAddress = topics.get(2).substring(topics.get(2).length() - 40);
                            tokenTx.setValueNumber(new BigDecimal(Numeric.decodeQuantity(data)).divide(decimals));
                            tokenTx.setTokenType(ERC20);
                            block.setErc20TransactionCount(block.getErc20TransactionCount() + 1);
                        } else {
                            if (HEX.equals(data) && topics.size() >= 4) {
                                fromAddress = topics.get(1).substring(topics.get(1).length() - 40);
                                toAddress = topics.get(2).substring(topics.get(2).length() - 40);
                                tokenTx.setTokenID(Numeric.decodeQuantity(topics.get(3)).toString());
                            } else {
                                String dataStr = data.substring(26);
                                fromAddress = commonService.blockHex(dataStr.substring(0, 40));
                                toAddress = commonService.blockHex(dataStr.substring(64).substring(0, 40));
                                tokenTx.setTokenID(Numeric.decodeQuantity(commonService.blockHex(data.substring(data.length() - 40))).toString());
                            }
                            tokenTx.setTokenType(ERC721);
                            block.setErc721TransactionCount(block.getErc721TransactionCount() + 1);
                        }
                        tokenTx.setBlockHash(log.getBlockHash());
                        tokenTx.setBlockNumber(log.getBlockNumber());
                        tokenTx.setTransactionHash(log.getTransactionHash());
                        tokenTx.setTransactionIndex(log.getTransactionIndex());
                        tokenTx.setLogIndex(log.getLogIndex());
                        tokenTx.setLogType(log.getType());
                        tokenTx.setTokenAddress(log.getAddress());
                        tokenTx.setFromAddress(fromAddress);
                        tokenTx.setToAddress(toAddress);
                        tokenTx.setSymbol(token.getSymbol());
                        tokenTx.setBlockTimeStamp(block.getBlockTimeStamp());
                        updateCalculated(calculatedAddress, fromAddress, token);
                        updateCalculated(calculatedAddress, toAddress, token);
                        tokenTxs.add(tokenTx);
                    }
                }
            }
        }
        return tokenTxs;
    }


    public void updateCalculated(Map<String, Set<Token>> calculatedAddress, String address, Token token) {
        Set<Token> ethTokens;
        if (calculatedAddress.containsKey(address)) {
            ethTokens = calculatedAddress.get(address);
            ethTokens.add(token);
            calculatedAddress.put(address, ethTokens);
        } else {
            ethTokens = new HashSet<>();
            ethTokens.add(token);
            calculatedAddress.put(address, ethTokens);
        }
    }

}
