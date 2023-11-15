package com.jtspringproject.JtSpringProject.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    @Bean(name = "asyncExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // Set the core pool size based on your requirements
        executor.setMaxPoolSize(10); // Set the max pool size based on your requirements
        executor.setQueueCapacity(25); // Set the queue capacity based on your requirements
        executor.setThreadNamePrefix("async-executor-");
        executor.initialize();
        return executor;
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10); // Set the pool size based on your requirements
        scheduler.setThreadNamePrefix("custom-scheduler-");
        scheduler.setDaemon(true);
        return scheduler;
    }
}
