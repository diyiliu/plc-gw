package com.tiza.support.config;

import com.tiza.support.cache.ICache;
import com.tiza.support.cache.ram.RamCacheProvider;
import com.tiza.support.task.AutoSenderTask;
import com.tiza.support.task.ITask;
import com.tiza.support.util.SpringUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Description: SpringConfiguration
 * Author: DIYILIU
 * Update: 2018-01-29 10:25
 */


@EnableScheduling
@Configuration
public class SpringConfig {

    /**
     * spring 工具类
     *
     * @return
     */
    @Bean
    public SpringUtil springUtil() {

        return new SpringUtil();
    }

    /**
     * 设备注册缓存
     *
     * @return
     */
    @Bean
    public ICache onlineCacheProvider() {

        return new RamCacheProvider();
    }


    /**
     * 读保持寄存器
     *
     * @return
     */
    @Bean
    public ITask taskStorage() {
        int address = 2;
        int code = 3;
        int start = 60;
        int count = 4;

        return new AutoSenderTask(address, code, start, count, onlineCacheProvider());
    }


    @Scheduled(fixedDelay = 1 * 1000 * 60, initialDelay = 5 * 1000)
    public void refreshTaskStorage() {
        ITask task = taskStorage();
        task.execute();
    }
}
