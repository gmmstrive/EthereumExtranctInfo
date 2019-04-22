package com.gikee.ethereum.controller;

import com.gikee.ethereum.service.AsynGetInfo;
import com.gikee.ethereum.utils.SignNumberFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;

@RestController
public class EthereumExtractController {


    @Autowired
    private AsynGetInfo asynGetInfo;

    @Autowired
    private SignNumberFile signNumberFile;

    @RequestMapping("/initSignFile")
    private String initSignFile(Integer blockNumber) {
        signNumberFile.initSignFile(blockNumber);
        Integer blockSignNumber = signNumberFile.getBlockSignNumber();
        if (blockNumber.equals(blockSignNumber)) {
            return "初始化成功 , 块高度为 : " + blockNumber;
        } else {
            return "初始化失败";
        }
    }

    @RequestMapping("/AnsygetHistory")  // local1 tmp_7556387
    public void extractBLockInfo(Integer blockSignNumber, Integer lastBlockNumber) throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(lastBlockNumber - blockSignNumber + 1);
        while (blockSignNumber <= lastBlockNumber) {
            asynGetInfo.executeAsync(blockSignNumber, countDownLatch);
            blockSignNumber = blockSignNumber + 1;
        }
        countDownLatch.await();
    }

}
