package com.tiza.protocol.dtu;

import com.tiza.protocol.IDataProcess;
import com.tiza.support.cache.ICache;
import com.tiza.support.client.KafkaClient;
import com.tiza.support.dao.DeviceDao;
import com.tiza.support.model.CanPackage;
import com.tiza.support.model.DeviceInfo;
import com.tiza.support.model.FunctionInfo;
import com.tiza.support.model.NodeItem;
import com.tiza.support.model.header.DtuHeader;
import com.tiza.support.model.header.Header;
import com.tiza.support.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.script.ScriptException;
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
    private ICache functionSetCacheProvider;

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
        DtuHeader dtuHeader = (DtuHeader) header;
        String deviceId = dtuHeader.getDeviceId();
        if (!deviceCacheProvider.containsKey(deviceId)){

            logger.warn("设备不存在[{}]!", deviceId);
            return;
        }

        DeviceInfo deviceInfo = (DeviceInfo) deviceCacheProvider.get(deviceId);
        if (!functionSetCacheProvider.containsKey(deviceInfo.getSoftVersion())){

            logger.warn("未配置的功能集[{}]", deviceInfo.getSoftVersion());
            return;
        }

        String canCode = String.valueOf(dtuHeader.getCode());
        FunctionInfo functionInfo = (FunctionInfo) functionSetCacheProvider.get(deviceInfo.getSoftVersion());
        CanPackage canPackage = functionInfo.getCanPackages().get(canCode);
        if (canPackage == null){

            logger.warn("未配置的功能码[{}]", canCode);
            return;
        }

        Map paramValues = parsePackage(content, canPackage.getItemList());
        updateStatus(dtuHeader, paramValues);
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

    protected Map parsePackage(byte[] content, List<NodeItem> nodeItems) {
        Map packageValues = new HashMap();

        for (NodeItem item : nodeItems) {
            try {
                packageValues.put(item.getField(), parseItem(content, item));
            } catch (ScriptException e) {
                logger.error("解析表达式错误：", e);
            }
        }

        return packageValues;
    }

    protected String parseItem(byte[] data, NodeItem item) throws ScriptException {

        String tVal;
        byte[] val = CommonUtil.byteToByte(data, item.getByteStart(), item.getByteLen(), item.getEndian());
        int tempVal = CommonUtil.byte2int(val);
        if (item.isOnlyByte()) {
            tVal = CommonUtil.parseExp(tempVal, item.getExpression(), item.getType());
        } else {
            int biteVal = CommonUtil.getBits(tempVal, item.getBitStart(), item.getBitLen());
            tVal = CommonUtil.parseExp(biteVal, item.getExpression(), item.getType());
        }

        return tVal;
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

        // 写入kafka
        KafkaClient.toKafka(deviceInfo.getId(), paramValues);
    }
}
