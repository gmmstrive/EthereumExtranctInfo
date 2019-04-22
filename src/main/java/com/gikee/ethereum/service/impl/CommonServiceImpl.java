package com.gikee.ethereum.service.impl;

import com.gikee.ethereum.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class CommonServiceImpl implements CommonService {

    private static final String HEX = "0x";
    private static final double WEIPOW = Math.pow(10, 18);
    private static final String BALANCEOF = "balanceOf";

    @Autowired
    private Web3j web3j;

    @Override
    public String hexToASCII(String hexValue) {
        hexValue = hexValue.substring(2);
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexValue.length(); i += 2) {
            String str = hexValue.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    @Override
    public String blockHex(BigInteger blockNumber) {
        return HEX + blockNumber.toString(16);
    }

    @Override
    public String blockHex(String blockNumber) {
        return HEX + blockNumber;
    }

    @Override
    public String ethereumVersion() {
        try {
            return web3j.web3ClientVersion().sendAsync().get().getWeb3ClientVersion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Integer ethereumBlockNumber() {
        try {
            return web3j.ethBlockNumber().sendAsync().get().getBlockNumber().intValue();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BigDecimal digitalConversion(String value) {
        return digitalConversion(new BigDecimal(Numeric.decodeQuantity(value)));
    }

    @Override
    public BigDecimal digitalConversion(BigInteger value) {
        return digitalConversion(new BigDecimal(value));
    }

    @Override
    public BigDecimal digitalConversion(BigDecimal value) {
        return Convert.toWei(value, Convert.Unit.WEI).divide(new BigDecimal(WEIPOW));
    }

    @Override
    public String getTokenBalance(String fromAddress, String contractAddress, int decimal) {
        String tokenBalance = getTokenBalance(fromAddress, contractAddress);
        if (new BigDecimal(tokenBalance).compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }
        return new BigDecimal(tokenBalance).divide(new BigDecimal(Math.pow(10, decimal))).toPlainString();
    }

    @Override
    public String getTokenBalance(String fromAddress, String contractAddress) {
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();

        Address address = new Address(fromAddress);
        TypeReference<Uint256> typeReference = new TypeReference<Uint256>() {
        };

        inputParameters.add(address);
        outputParameters.add(typeReference);

        Function function = new Function(BALANCEOF, inputParameters, outputParameters);
        String data = FunctionEncoder.encode(function);

        Transaction transaction = Transaction.createEthCallTransaction(fromAddress, contractAddress, data);

        EthCall ethCall;
        BigInteger balanceValue = BigInteger.ZERO;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            if (!results.isEmpty()) {
                balanceValue = (BigInteger) results.get(0).getValue();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return balanceValue.toString();
    }

    @Override
    public String getEthereumBalance(String address, BigInteger blockNumber) {
        BigInteger value;
        BigDecimal balance = BigDecimal.ZERO;
        try {
            value = web3j.ethGetBalance(address, DefaultBlockParameter.valueOf(blockNumber)).sendAsync().get().getBalance();
            if (value.compareTo(BigInteger.ZERO) > 0) {
                balance = new BigDecimal(value).divide(new BigDecimal(WEIPOW));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return balance.toPlainString();
    }

}
