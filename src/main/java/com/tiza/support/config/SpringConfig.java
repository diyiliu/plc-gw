package com.tiza.support.config;

import com.tiza.support.cache.ICache;
import com.tiza.support.cache.ram.RamCacheProvider;
import com.tiza.support.listener.CMDInitializer;
import com.tiza.support.util.SpringUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: SpringConfiguration
 * Author: DIYILIU
 * Update: 2018-01-29 10:25
 */

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
     * spring jdbcTemplate
     *
     * @param dataSource
     * @return
     */
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource){

        return new JdbcTemplate(dataSource);
    }

    /**
     * 指令初始化
     *
     * @return
     */
    @Bean
    public CMDInitializer cmdInitializer(){
        CMDInitializer cmdInitializer = new CMDInitializer();

        List<Class> protocols = new ArrayList();
        protocols.add(com.tiza.protocol.dtu.DtuDataProcess.class);
        cmdInitializer.setProtocols(protocols);

        return cmdInitializer;
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
     * 指令缓存
     *
     * @return
     */
    @Bean
    public ICache dtuCMDCacheProvider() {

        return new RamCacheProvider();
    }

    /**
     * 数据库设备缓存
     *
     * @return
     */
    @Bean
    public ICache deviceCacheProvider() {

        return new RamCacheProvider();
    }
}
