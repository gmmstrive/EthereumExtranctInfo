package com.gikee.ethereum.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.gikee.ethereum.model.Block;
import com.gikee.ethereum.model.Trace;
import com.gikee.ethereum.model.TraceAction;
import com.gikee.ethereum.model.Transaction;
import com.gikee.ethereum.service.CommonService;
import com.gikee.ethereum.service.TraceService;
import com.gikee.ethereum.utils.JsonRpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TraceServiceImpl implements TraceService {

    @Autowired
    private JsonRpc jsonRpc;

    private static final String TRACE_BLOCK = "trace_block";
    private static final String INTERNALTRANSACTION = "internalTransaction";
    private static final String REWARD = "reward";
    private static final String CREATE = "create";
    private static final String SUICIDE = "suicide";

    @Autowired
    private CommonService commonService;

    @Override
    public List<Trace> ethereumBlockTraces(String blockHex) {
        String traces = jsonRpc.sendJsonRpcMessage(TRACE_BLOCK, new Object[]{blockHex});
        JSONArray result = JSON.parseObject(traces).getJSONArray("result");
        return result.toJavaList(Trace.class);
    }

    @Override
    public List<Transaction> getInternalTxs(String blockHex, Block block, Map<String, String> txType, Map<String, String> isError, Set<String> address) {

        List<Transaction> internalTx = new ArrayList<>();
        List<BigDecimal> rewards = new ArrayList<>();
        List<Trace> traces = ethereumBlockTraces(blockHex);

        if (!traces.isEmpty()) {
            TraceAction action;
            String type;
            String valueHex;
            BigInteger valueNumber = BigInteger.ZERO;
            for (Trace tr : traces) {
                action = tr.getAction();
                type = tr.getType();
                valueHex = action.getValue();
                if (valueHex != null) {
                    valueNumber = Numeric.decodeQuantity(valueHex);
                }
                if (tr.getTraceAddress().isEmpty()) {
                    if (REWARD.equals(type)) {
                        rewards.add(commonService.digitalConversion(valueNumber));
                    } else {
                        if (tr.getError() != null) {
                            isError.put(tr.getTransactionHash(), tr.getError());
                        }
                        txType.put(tr.getTransactionHash(), type);
                    }
                } else {
                    if (CREATE.equals(type) && tr.getResult() != null) {
                        action.setTo(tr.getResult().getAddress());
                        tr.setAction(action);
                        internalTx.add(traceToTransaction(tr, block, address));
                    } else if (SUICIDE.equals(type)) {
                        action.setFrom(action.getAddress());
                        action.setTo(action.getRefundAddress());
                        action.setValue(action.getBalance());
                        tr.setAction(action);
                        internalTx.add(traceToTransaction(tr, block, address));
                    } else {
                        if (valueNumber.compareTo(BigInteger.ZERO) > 0) {
                            internalTx.add(traceToTransaction(tr, block, address));
                        }
                    }
                }
            }
            block.setBlockReward(rewards.get(0));
            block.setUnclesInclusionReward(rewards.get(0));

            if (rewards.size() > 1) {
                for (int i = 1; i < rewards.size(); i++) {
                    block.setUnclesReward(block.getUnclesReward().add(rewards.get(i)));
                }
            }
        }

        return internalTx;

    }

    public Transaction traceToTransaction(Trace trace, Block block, Set<String> address) {
        Transaction tx = new Transaction();
        tx.setBlockNumber(trace.getBlockNumber());
        tx.setBlockHash(trace.getBlockHash());
        if (trace.getError() != null) tx.setStatusOK(false);
        tx.setError(trace.getError());
        tx.setTransactionHash(trace.getTransactionHash());
        tx.setTransactionIndex(trace.getTransactionPosition());
        tx.setFromAddress(trace.getAction().getFrom());
        tx.setToAddress(trace.getAction().getTo());
        tx.setValueNumber(commonService.digitalConversion(trace.getAction().getValue()));
        tx.setType(trace.getType());
        tx.setTransactionType(INTERNALTRANSACTION);
        tx.setBlockTimeStamp(block.getBlockTimeStamp());
        // 累加外部交易条数
        block.setInternalTransactionCount(block.getInternalTransactionCount() + 1);
        // 累加外部交易数量
        block.setInternalTransactionValue(block.getInternalTransactionValue().add(tx.getValueNumber()));
        // 获取参与交易地址
        if (tx.getFromAddress() != null) address.add(tx.getFromAddress());
        if (tx.getToAddress() != null) address.add(tx.getToAddress());
        return tx;
    }

}
