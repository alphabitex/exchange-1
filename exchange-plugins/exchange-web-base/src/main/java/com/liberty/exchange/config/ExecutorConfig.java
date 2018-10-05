package com.liberty.exchange.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * WalletProperties Created with IntelliJ IDEA.
 * User: liulinhui
 * Date: 2018/10/6
 * Time: 3:06
 * Description: 线程池配置
 */
@Configuration
@EnableAsync
public class ExecutorConfig {

    @Bean(name = "commonAsyncTask")
    public Executor commonAsyncTask() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //Set the ThreadPoolExecutor's core pool size.
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(corePoolSize);
        //Set the ThreadPoolExecutor's maximum pool size.
        int maxPoolSize = 500;
        executor.setMaxPoolSize(maxPoolSize);
        //Set the capacity for the ThreadPoolExecutor's BlockingQueue.
        int queueCapacity = 20;
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("commonAsyncTask-");
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
