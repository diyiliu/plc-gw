package com.tiza.support.dao;

import com.tiza.support.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Description: BaseDao
 * Author: DIYILIU
 * Update: 2018-01-30 13:47
 */

@Component
public class BaseDao {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    protected JdbcTemplate jdbcTemplate;


    public boolean update(String sql, Object[] values) {

        int result = 0;
        try {
            result = jdbcTemplate.update(sql, values);
        } catch (Exception e) {
            logger.error("msg:[{}], sql:[{}], 值:[{}]", e.getMessage(), sql, JacksonUtil.toJson(values));
            e.printStackTrace();
        }

        if (result > 0) {

            return true;
        }

        return false;
    }

    public boolean update(String sql) {
        if (jdbcTemplate == null) {
            logger.info("未装载数据源，无法连接数据库!");
            return false;
        }

        int result = jdbcTemplate.update(sql);
        if (result > 0) {

            return true;
        }

        return false;
    }
}
