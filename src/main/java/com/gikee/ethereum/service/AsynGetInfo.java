package com.gikee.ethereum.service;

import java.util.concurrent.CountDownLatch;

public interface AsynGetInfo {

    void executeAsync(Integer blockNumber, CountDownLatch countDownLatch);

}
