package com.gikee.ethereum;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.gikee.ethereum.dao.mysql")
@EnableScheduling
@EnableAsync
@SpringBootApplication
public class EthereumApplication {

    public static void main(String[] args) {
        SpringApplication.run(EthereumApplication.class, args);
    }

}
