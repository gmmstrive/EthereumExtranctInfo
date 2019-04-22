package com.gikee.ethereum.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gikee.ethereum.service.TransactionReceiptService;
import com.gikee.ethereum.utils.JsonRpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.List;

@Service
public class TransactionReceiptServiceImpl implements TransactionReceiptService {

    @Autowired
    private JsonRpc jsonRpc;

    @Override
    public List<TransactionReceipt> ethereumTxReceipts(String blockHex) {

        String receipts = jsonRpc.sendJsonRpcMessage("parity_getBlockReceipts", new Object[]{blockHex});
        JSONObject jsonObj = JSON.parseObject(receipts);
        JSONArray result = jsonObj.getJSONArray("result");
        return result.toJavaList(TransactionReceipt.class);
    }

}
