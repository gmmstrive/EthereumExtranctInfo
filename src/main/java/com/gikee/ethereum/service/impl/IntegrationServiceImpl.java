package com.gikee.ethereum.service.impl;

import com.alibaba.fastjson.JSON;
import com.gikee.ethereum.dao.es.*;
import com.gikee.ethereum.dao.mongo.MongoBlockDao;
import com.gikee.ethereum.dao.mongo.MongoTokenTransactionDao;
import com.gikee.ethereum.dao.mongo.MongoTransactionDao;
import com.gikee.ethereum.dao.mongo.MongoUncleDao;
import com.gikee.ethereum.model.*;
import com.gikee.ethereum.service.*;
import com.gikee.ethereum.utils.DateTransform;
import com.neovisionaries.ws.client.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Service
public class IntegrationServiceImpl implements IntegrationService {

    @Autowired
    private BlockService blockService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionReceiptService transactionReceiptService;

    @Autowired
    private TraceService traceService;

    @Autowired
    private UncleService uncleService;

    @Autowired
    private TokenTransactionService tokenTransactionService;

    @Autowired
    private ESAddressDao addressDao;

    @Autowired
    private ESBlockDao esBlockDao;

    @Autowired
    private ESUncleDao esUncleDao;

    @Autowired
    private ESTransactionDao esTransactionDao;

    @Autowired
    private ESTokenTransactionDao esTokenTransactionDao;

    @Autowired
    private MongoBlockDao mongoBlockDao;

    @Autowired
    private MongoUncleDao mongoUncleDao;

    @Autowired
    private MongoTransactionDao mongoTransactionDao;

    @Autowired
    private MongoTokenTransactionDao mongoTokenTransactionDao;

    @Autowired
    private CommonService commonService;

    @Autowired
    private DateTransform dateTransform;

    @Autowired
    private WebSocket ws;

    @Override
    public void ethereumInfo(Integer blockNumber) {

        String blockHex = commonService.blockHex(BigInteger.valueOf(blockNumber));

        Set<String> ethereumAddress = new HashSet<>();
        Map<String, Set<Token>> calculatedAddress = new HashMap<>();
        List<Transaction> totalTx = new ArrayList<>();
        Map<String, String> txType = new HashMap<>();
        Map<String, String> isError = new HashMap<>();

        // 获取块信息
        EthBlock.Block tmpBlock = blockService.ethereumBlock(blockNumber);
        List<EthBlock.TransactionResult> txs = tmpBlock.getTransactions();
        // 整合块信息
        Block block = blockService.ethereumBlock(tmpBlock);
        // 获取交易收据
        List<TransactionReceipt> txReceipts = transactionReceiptService.ethereumTxReceipts(blockHex);
        // 获取内部交易、补充块信息(BlockReward、UnclesInclusionReward、UnclesReward、InternalTransactionCount、InternalTransactionValue)
        // 交易类型、错误信息、参与交易地址
        totalTx.addAll(traceService.getInternalTxs(blockHex, block, txType, isError, ethereumAddress));
        // 获取外部交易、补充块信息(ExternalTransactionCount、ExternalTransactionValue、FeesReward)、参与交易地址,并且使用交易类型、错误信息作为外部交易的额外信息
        totalTx.addAll(transactionService.getExternalTxs(txs, block, txReceipts, blockHex, txType, isError, ethereumAddress));
        // 获取 token 交易、补充块信息(Erc20TransactionCount、Erc721TransactionCount)、获取 token 交易地址列表(附带 token 详情)
        List<TokenTransaction> tokenTransactions = tokenTransactionService.ethereumTokenTxs(txReceipts, block, calculatedAddress);
        // 获取叔块信息、补充块信息(UncleCount)
        List<Uncle> uncles = uncleService.etherumUncles(tmpBlock.getUncles(), block);

        // 交易地址列表加上挖矿地址
        ethereumAddress.add(block.getMiner());

        // 发布大额交易，交易量超过 50000 ，即为大额交易
        totalTx.stream()
                .filter(tx -> tx.getValueNumber() != null && tx.getValueNumber().compareTo(new BigDecimal("50000")) >= 0)
                .map(x -> new MessageReminder(
                        new Message(
                                x.getBlockNumber().toString(),
                                x.getTransactionHash(),
                                x.getFromAddress(),
                                x.getToAddress(),
                                x.getValueNumber().toPlainString(),
                                dateTransform.getUTCToTimeStamp(x.getBlockTimeStamp())
                        )
                ));
                //.forEach(x -> ws.sendText(JSON.toJSONString(x)));

        if (block.getBlockTimeStamp() != null) {
            // ES, 写入块信息
            //esBlockDao.elasticSearchBulk(block);
            mongoBlockDao.insertCollection(block);
            //System.out.println(JSON.toJSONString(block));
        }

        if (!uncles.isEmpty()) {
            //esUncleDao.elasticSearchDeleteByQuery(blockNumber.toString());
            //esUncleDao.elasticSearchBulk(uncles);
            mongoUncleDao.insertCollection(uncles);
            //System.out.println(JSON.toJSONString(uncles));
        }

        if (!totalTx.isEmpty()) {
            //esTransactionDao.elasticSearchDeleteByQuery(blockNumber.toString());
            //esTransactionDao.elasticSearchBulk(totalTx);
            mongoTransactionDao.insertCollection(totalTx);
            //System.out.println(JSON.toJSONString(totalTx));
        }

        if (!tokenTransactions.isEmpty()) {
            //esTokenTransactionDao.elasticSearchDeleteByQuery(blockNumber.toString());
            //esTokenTransactionDao.elasticSearchBulk(tokenTransactions);
            mongoTokenTransactionDao.insertCollection(tokenTransactions);
            //System.out.println(JSON.toJSONString(tokenTransactions));
        }

        if (!calculatedAddress.isEmpty()) {
            // ES, 更新 token 余额
            addressDao.elasticSearchAddressUpdate(calculatedAddress);
        }

        if (!ethereumAddress.isEmpty()) {
            // ES, 更新以太币余额
            addressDao.integratedAndSave(ethereumAddress, BigInteger.valueOf(commonService.ethereumBlockNumber()));
        }

    }

}
