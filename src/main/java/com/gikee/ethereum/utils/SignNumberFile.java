package com.gikee.ethereum.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Configuration
public class SignNumberFile {

    @Value(value = "${blockSignFile.path}")
    private String path;

    @Value(value = "${blockSignFile.fileName}")
    private String fileName;

    public void initSignFile(Integer blockNumber) {
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdir();
                File file = new File(path + fileName);
                file.createNewFile();
                saveBlockSignNumber(blockNumber);
            } else {
                saveBlockSignNumber(blockNumber);
            }
            log.info("初始化块游标成功,当前块游标高度为 : ---------------> {} <---------------", blockNumber);
        } catch (IOException e) {
            log.info("初始化块游标失败");
            e.printStackTrace();
        }
    }

    public Integer getBlockSignNumber() {
        Integer blockNumber = null;
        try {
            blockNumber = Integer.valueOf(FileUtils.readFileToString(new File(path + fileName), "UTF-8"));
            log.info("获取块游标成功,当前块游标高度为 : ---------------> {} <---------------", blockNumber);
        } catch (IOException e) {
            log.error("获取块游标失败,使用默认游标高度 0");
            e.printStackTrace();
        }
        return blockNumber;
    }

    public void saveBlockSignNumber(Integer blockNumber) {
        try {
            FileUtils.writeStringToFile(new File(path + fileName), String.valueOf(blockNumber), "UTF-8", false);
            log.info("保存块游标成功,当前块游标高度为 : ---------------> {} <---------------", blockNumber);
        } catch (IOException e) {
            log.error("保存块游标失败");
            e.printStackTrace();
        }
    }

    public Map<Integer, Integer> blockNumberMap(String blockNumber) {
        Integer prefix = Integer.parseInt(blockNumber.substring(0, 1)) * Double.valueOf(Math.pow(10, blockNumber.length() - 1)).intValue();
        Integer number = Integer.parseInt(blockNumber.substring(1));

        Map<Integer, Integer> blockNumberMap = new TreeMap<>();

        int n = number / 1000;
        for (int i = 1; i <= n; i++) {
            if (i == 1) {
                blockNumberMap.put(i + prefix - 1, i * 1000 + prefix);
                //System.out.println(i + prefix - 1 + " " + (i * 1000 + prefix));
            } else if (i == n) {
                blockNumberMap.put((i - 1) * 1000 + 1 + prefix, number + prefix);
                //System.out.println((i - 1) * 1000 + 1 + prefix + " " + (number + prefix));
            } else {
                blockNumberMap.put((i - 1) * 1000 + 1 + prefix, i * 1000 + prefix);
                //System.out.println((i - 1) * 1000 + 1 + prefix + " " + (i * 1000 + prefix));
            }
        }
        return blockNumberMap;
    }

}
