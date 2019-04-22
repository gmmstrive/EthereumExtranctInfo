package com.gikee.ethereum.service.impl;

import com.gikee.ethereum.service.AsynGetInfo;
import com.gikee.ethereum.service.IntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

@Service
public class AsynGetInfoImpl implements AsynGetInfo {

    @Autowired
    private IntegrationService integrationService;

    @Qualifier("getAsyncExecutor")
    @Async
    @Override
    public void executeAsync(Integer blockNumber, CountDownLatch countDownLatch) {
        integrationService.ethereumInfo(blockNumber);
        countDownLatch.countDown();
    }

}
