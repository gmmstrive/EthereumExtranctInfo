//package com.gikee.ethereum.utils;
//
//import com.alibaba.fastjson.JSONObject;
//import com.gikee.ethereum.model.Trace;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.web3j.protocol.Web3j;
//import org.web3j.protocol.core.DefaultBlockParameter;
//import org.web3j.protocol.core.methods.response.EthBlock;
//import org.web3j.protocol.core.methods.response.TransactionReceipt;
//
//import javax.annotation.PostConstruct;
//import java.io.IOException;
//import java.math.BigInteger;
//import java.time.LocalDateTime;
//import java.time.ZoneOffset;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Future;
//
//@Component
//public class EthereumJsonRpcUtils {
//
//    private static final String JSON_RPC = "jsonrpc";
//    private static final String JSON_RPC_NUMBER = "2.0";
//    private static final String METHOD = "method";
//    private static final String PARAMS = "params";
//    private static final String ID = "id";
//    private static final String RESULT = "result";
//    private static final ZoneOffset zoneOffset = ZoneOffset.of("+8");
//
//    private static final boolean returnFullTransactionObjects = true;
//    private static final String PARITY_GET_BLOCK_RECEIPTS = "parity_getBlockReceipts";
//    private static final String TRACE_BLOCK = "trace_block";
//
//    private static JSONObject jsonObject;
//
//    @Autowired
//    private CommonHttpUtil.DataRequest dataRequest;
//
//    @Autowired
//    private Web3j web3j;
//
//    /**
//     * 通过 @PostConstruct 注解，把 @Autowired 注解的属性初始化给静态变量
//     */
////    @PostConstruct
////    public void init() {
////        ethereumWeb3j = web3j;
////        ethereumRequest = dataRequest;
////    }
//
//    static {
//        jsonObject = new JSONObject();
//        jsonObject.put(JSON_RPC, JSON_RPC_NUMBER);
//    }
//
//    /**
//     * Parity Ethereum Version
//     *
//     * @return
//     */
//    public String ethereumVersion() {
//        try {
//            return web3j.web3ClientVersion().send().getWeb3ClientVersion();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    /**
//     * 获取最新块号
//     *
//     * @return
//     */
//    public Integer ethereumBlockNumber() {
//        try {
//            return web3j.ethBlockNumber().send().getBlockNumber().intValue();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//
//    /**
//     * 获取块信息
//     *
//     * @param blockNumber
//     * @return
//     */
//    public EthBlock.Block ethereumBlock(Integer blockNumber) {
//        try {
//            return web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNumber)), returnFullTransactionObjects)
//                    .send().getBlock();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public List<EthBlock.TransactionResult> ethereumBlockTxs(Integer blockNumber) {
//        try {
//            List<EthBlock.TransactionResult> transactions = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNumber)), returnFullTransactionObjects)
//                    .send().getBlock().getTransactions();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    /**
//     * 获取交易信息
//     *
//     * @param block
//     * @return
//     */
//    public List<EthBlock.TransactionResult> ethereumTxs(EthBlock.Block block) {
//        return block.getTransactions();
//    }
//
//    /**
//     * 获取交易收据信息
//     *
//     * @param blockHex
//     * @return
//     */
//    public List<TransactionReceipt> ethereumTxReceipt(String blockHex) {
//        Optional<JSONObject> jsonObj;
//        List<TransactionReceipt> txReceipt = new ArrayList<>();
//        CommonHttpUtil.HttpResponse response = httpResponse(PARITY_GET_BLOCK_RECEIPTS, new Object[]{blockHex});
//        if (response != null) {
//            jsonObj = response.tryJson();
//            if (jsonObj.isPresent()) {
//                txReceipt = jsonObj.get().getJSONArray(RESULT).toJavaList(TransactionReceipt.class);
//            }
//        }
//        return txReceipt;
//    }
//
//    /**
//     * 获取块 trace 信息
//     *
//     * @param blockHex
//     * @return
//     */
//    public List<Trace> ethereumBlockTraces(String blockHex) {
//        Optional<JSONObject> jsonObj;
//        List<Trace> traces = new ArrayList<>();
//        CommonHttpUtil.HttpResponse response = httpResponse(TRACE_BLOCK, new Object[]{blockHex});
//        if (response != null) {
//            jsonObj = response.tryJson();
//            if (jsonObj.isPresent()) {
//                traces = jsonObj.get().getJSONArray(RESULT).toJavaList(Trace.class);
//            }
//        }
//        return traces;
//    }
//
//    /**
//     * 获取叔块信息
//     *
//     * @param blockNumber
//     * @param i
//     * @return
//     */
//    public EthBlock.Block etherumUncles(BigInteger blockNumber, Integer i) {
//        try {
//            return web3j.ethGetUncleByBlockNumberAndIndex(DefaultBlockParameter.valueOf(blockNumber), BigInteger.valueOf(i))
//                    .send().getBlock();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    /**
//     * 获取 Response 返回结果
//     *
//     * @param method
//     * @param obj
//     * @return
//     */
//    private CommonHttpUtil.HttpResponse httpResponse(String method, Object[] obj) {
//        jsonObject.put(METHOD, method);
//        jsonObject.put(PARAMS, obj);
//        jsonObject.put(ID, LocalDateTime.now().toEpochSecond(zoneOffset));
//        CommonHttpUtil.HttpResponse response = dataRequest.body(jsonObject.toJSONString()).send();
//        if (response.isSuccessful()) {
//            return response;
//        }
//        return null;
//    }
//
//
//}
