package com.sellics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {
    private static final int MAX_THREAD_COUNT = 10;

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(MAX_THREAD_COUNT);
    }
}
