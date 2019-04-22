package com.gikee.ethereum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorConfig implements AsyncConfigurer {

    private static final Integer COREPOOLSIZE = 20;
    private static final Integer MAXPOOLSIZE = 25;
    private static final Integer KEEPALIVESECONDS = 60;

    @Bean
    @Override
    public ThreadPoolExecutor getAsyncExecutor() {
        ThreadPoolExecutor threadPoolExecutor =
                new ThreadPoolExecutor(COREPOOLSIZE, MAXPOOLSIZE, KEEPALIVESECONDS, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        // 任务队列策略
        threadPoolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return threadPoolExecutor;
    }

}
