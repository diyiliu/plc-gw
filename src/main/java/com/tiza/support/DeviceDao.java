package com.tiza.support;

import com.tiza.support.model.DeviceInfo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.util.List;

/**
 * Description: DeviceDao
 * Author: DIYILIU
 * Update: 2018-01-30 11:15
 */

@Component
public class DeviceDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public List<DeviceInfo> selectDeviceInfo() {
        String sql = "SELECT t.Id, t.DtuId FROM equipment t";

        return jdbcTemplate.query(sql, (ResultSet rs, int rowNum) -> {
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setId(rs.getInt("id"));
            deviceInfo.setDtuId(rs.getString("dtuId"));

            return deviceInfo;
        });
    }
}
