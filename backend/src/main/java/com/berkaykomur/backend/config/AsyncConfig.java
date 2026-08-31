package com.berkaykomur.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "analysisTaskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);      // Aynı anda aktif çalışabilecek işçi sayısı
        executor.setMaxPoolSize(8);       // Yük artarsa çıkılabilecek maksimum işçi sayısı
        executor.setQueueCapacity(50);    // İşçiler doluysa sırada bekleyebilecek maksimum kişi sayısı
        executor.setThreadNamePrefix("AI-Analysis-Thread-");
        executor.initialize();
        return executor;
    }
}