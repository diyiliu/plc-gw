package com.tiza.support.task.impl;

import com.tiza.support.dao.DeviceDao;
import com.tiza.support.cache.ICache;
import com.tiza.support.model.DeviceInfo;
import com.tiza.support.task.ITask;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Description: DeviceInfoTask
 * Author: DIYILIU
 * Update: 2018-01-30 11:07
 */

public class DeviceInfoTask implements ITask {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ICache deviceCache;

    private DeviceDao deviceDao;

    public DeviceInfoTask(DeviceDao deviceDao, ICache deviceCache) {
        this.deviceDao = deviceDao;
        this.deviceCache = deviceCache;
    }

    @Override
    public void execute() {
        logger.info("刷新设备列表...");

        List<DeviceInfo> list = deviceDao.selectDeviceInfo();
        refresh(list, deviceCache);
    }

    private void refresh(List<DeviceInfo> deviceList, ICache deviceCache) {
        if (deviceList == null || deviceList.size() < 1){
            logger.warn("无设备!");
            return;
        }

        Set oldKeys = deviceCache.getKeys();
        Set tempKeys = new HashSet(deviceList.size());

        for (DeviceInfo device : deviceList) {
            deviceCache.put(device.getDtuId(), device);
            tempKeys.add(device.getDtuId());
        }

        Collection subKeys = CollectionUtils.subtract(oldKeys, tempKeys);
        for (Iterator iterator = subKeys.iterator(); iterator.hasNext();){
            String key = (String) iterator.next();
            deviceCache.remove(key);
        }
    }
}
