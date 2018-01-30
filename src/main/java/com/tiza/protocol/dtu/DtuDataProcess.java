package com.tiza.protocol.dtu;

import com.tiza.protocol.IDataProcess;
import com.tiza.support.dao.DeviceDao;
import com.tiza.support.cache.ICache;
import com.tiza.support.model.DeviceInfo;
import com.tiza.support.model.header.DtuHeader;
import com.tiza.support.model.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Description: DtuDataProcess
 * Author: DIYILIU
 * Update: 2018-01-30 09:45
 */

@Service
public class DtuDataProcess implements IDataProcess {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected int cmd = 0xFF;

    @Resource
    private ICache dtuCMDCacheProvider;

    @Resource
    private ICache deviceCacheProvider;

    @Resource
    private DeviceDao deviceDao;

    @Override
    public void init() {
        dtuCMDCacheProvider.put(this.cmd, this);
    }

    @Override
    public Header dealHeader(byte[] bytes) {

        return null;
    }

    @Override
    public void parse(byte[] content, Header header) {

    }

    @Override
    public byte[] pack(Header header, Object... argus) {
        return new byte[0];
    }


    /**
     * 按字节解析
     * @param b
     * @param items
     * @return
     */
    protected Map parseByte(byte b, String[] items){
        Map map = new HashMap();
        for (int i = 0; i < items.length; i++){

            int value = (b >> i) & 0x01;
            map.put(items[i], value);
        }

        return map;
    }




    public void updateStatus(DtuHeader dtuHeader, Map paramValues){
        String deviceId = dtuHeader.getDeviceId();
        if (!deviceCacheProvider.containsKey(deviceId)){

            logger.warn("设备不存在[{}]!", deviceId);
            return;
        }
        DeviceInfo deviceInfo = (DeviceInfo) deviceCacheProvider.get(deviceId);

        List list = new ArrayList();
        StringBuilder sqlBuilder = new StringBuilder("UPDATE equipment_info SET ");
        paramValues.keySet().forEach(k -> {
            sqlBuilder.append(k).append("=?, ");

            Object val = paramValues.get(k);
            list.add(val);
        });

        // 最新时间
        sqlBuilder.append("lastTime").append("=?, ");
        list.add(new Date(dtuHeader.getTime()));

        logger.info("更新设备[{}]状态...", deviceId);
        String sql = sqlBuilder.substring(0, sqlBuilder.length() - 2) + " WHERE equipmentId=" + deviceInfo.getId();
        deviceDao.update(sql, list.toArray());
    }
}
