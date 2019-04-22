package com.gikee.ethereum.config;

import com.gikee.ethereum.utils.CommonHttpUtil;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.util.concurrent.TimeUnit;

@Configuration
public class OkHttpConfig {

    @Value(value = "${ethereum.rpcUrl}")
    private String rpcUrl;

    @Bean
    public Web3j getWeb3j() {
        return Web3j.build(new HttpService(rpcUrl));
    }

    @Bean
    public CommonHttpUtil.DataRequest getCommonHttpUtil() {
        return CommonHttpUtil.builder().build().post(rpcUrl);
    }

    @Bean
    public OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)       //设置连接超时
                .readTimeout(60, TimeUnit.SECONDS)          //设置读超时
                .writeTimeout(60, TimeUnit.SECONDS)         //设置写超时
                .retryOnConnectionFailure(true)                    //是否自动重连
                .build();                                          //构建OkHttpClient对象
    }
}
