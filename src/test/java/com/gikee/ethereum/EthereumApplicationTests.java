package com.gikee.ethereum;

import com.gikee.ethereum.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EthereumApplicationTests {

    private static final String TRANSFER = "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef";

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
    private AsynGetInfo asynGetInfo;

    @Autowired
    private IntegrationService integrationService;


    @Test
    public void contextLoads() throws InterruptedException {

        //integrationService.ethereumInfo(7596758);//7610712
//        long begin = System.currentTimeMillis();
//        CountDownLatch countDownLatch = new CountDownLatch(2449356);
//
//        for (int i = 1; i <= 2449356; i++) {
//          //  integrationService.ethereumInfo(i);
//            asynGetInfo.executeAsync(i,countDownLatch);
//            Thread.sleep(300); //2019-04-21 23:32:44.779 //2019-04-21 23:38:40.285 //1181
//        }
//        countDownLatch.await();
//        long end = System.currentTimeMillis();
//        System.out.println(end-begin);
    }

}
