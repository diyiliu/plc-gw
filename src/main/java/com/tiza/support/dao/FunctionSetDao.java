package com.tiza.support.dao;

import com.tiza.support.model.FunctionInfo;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.List;

/**
 * Description: FunctionSetDao
 * Author: DIYILIU
 * Update: 2018-02-09 10:29
 */

@Component
public class FunctionSetDao extends BaseDao {

    public List<FunctionInfo> selectFunctionInfo() {
        String sql = "SELECT t.Id, t.Name, t.FuncSet FROM plc_version t";

        return jdbcTemplate.query(sql, (ResultSet rs, int rowNum) -> {
            FunctionInfo functionInfo = new FunctionInfo();
            functionInfo.setSoftVersion(rs.getString("id"));
            functionInfo.setSoftName(rs.getString("name"));
            functionInfo.setFunctionXml(rs.getString("funcSet"));

            return functionInfo;
        });
    }
}
