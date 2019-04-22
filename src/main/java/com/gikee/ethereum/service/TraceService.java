package com.gikee.ethereum.service;

import com.gikee.ethereum.model.Block;
import com.gikee.ethereum.model.Trace;
import com.gikee.ethereum.model.Transaction;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TraceService {

    List<Trace> ethereumBlockTraces(String blockHex);

    List<Transaction> getInternalTxs(String blockHex, Block block, Map<String, String> txType, Map<String, String> isError, Set<String> address);

}
