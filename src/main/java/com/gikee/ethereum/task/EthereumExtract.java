package com.gikee.ethereum.task;

import com.gikee.ethereum.service.CommonService;
import com.gikee.ethereum.service.IntegrationService;
import com.gikee.ethereum.utils.SignNumberFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EthereumExtract {

    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private SignNumberFile signNumberFile;


    //@Scheduled(initialDelay = 1000 * 60, fixedRate = 1000 * 40)
    public void extractBLockInfo() {
        Integer blockSignNumber = signNumberFile.getBlockSignNumber();
        Integer lastBlockNumber = commonService.ethereumBlockNumber();
        if (lastBlockNumber != null && blockSignNumber != null) {
            blockSignNumber = blockSignNumber + 1;
            while (blockSignNumber <= lastBlockNumber) {
                integrationService.ethereumInfo(blockSignNumber);
                signNumberFile.saveBlockSignNumber(blockSignNumber);
                blockSignNumber = blockSignNumber + 1;
            }
        } else {
            log.error("更新最新块出错,游标或者获取最新块高度出错");
        }
    }

}
