package com.tiza.support.config;

import com.tiza.support.cache.ICache;
import com.tiza.support.config.timer.SenderTimer;
import com.tiza.support.dao.DeviceDao;
import com.tiza.support.dao.FunctionSetDao;
import com.tiza.support.model.CanPackage;
import com.tiza.support.model.FunctionInfo;
import com.tiza.support.task.ITask;
import com.tiza.support.task.impl.DeviceInfoTask;
import com.tiza.support.task.impl.FunctionSetTask;
import com.tiza.support.util.SpringUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.Map;

/**
 * Description: SpringQuartz
 * Author: DIYILIU
 * Update: 2018-01-30 10:41
 */

@EnableScheduling
@Configuration
public class SpringQuartz {

    @Resource
    private ICache deviceCacheProvider;

    @Resource
    private ICache functionSetCacheProvider;

    @Resource
    private DeviceDao deviceDao;

    @Resource
    private FunctionSetDao functionSetDao;

    /**
     * 刷新功能集列表
     *
     * @return
     */
    @Scheduled(fixedDelay = 30 * 60 * 1000, initialDelay = 5 * 1000)
    public void functionSetTask() {

        ITask task = new FunctionSetTask(functionSetDao, functionSetCacheProvider);
        task.execute();

        String softVersion = "1";
        FunctionInfo functionInfo = (FunctionInfo) functionSetCacheProvider.get(softVersion);

        Map parses = SpringUtil.getBeansOfType(SenderTimer.class);
        for (Iterator iterator = parses.keySet().iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            SenderTimer timer = (SenderTimer) parses.get(key);

            // 修改执行频率
            CanPackage canPackage = functionInfo.getCanPackages().get(timer.getFunctionId());
            timer.setPeriod(canPackage.getPeriod());
        }
    }


    /**
     * 刷新设备列表
     */
    @Scheduled(fixedDelay = 15 * 60 * 1000, initialDelay = 3 * 1000)
    public void refreshTaskDeviceInfo() {

        ITask task = new DeviceInfoTask(deviceDao, deviceCacheProvider);
        task.execute();
    }


    /**
     * 刷新数字输出数据
     @Scheduled(fixedDelay = 4 * 1000, initialDelay = 3 * 1000)
     public void refreshTaskDigitalOutput() {
     int address = 2;
     int code = 1;
     int start = 0;
     int count = 16;
     QueryFrame queryFrame = new QueryFrame(address, code, start, count);

     ITask task = new AutoSenderTask(queryFrame, onlineCacheProvider);
     task.execute();
     }*/

    /**
     * 刷新数字量输入数据
     @Scheduled(fixedDelay = 4 * 1000, initialDelay = 4 * 1000)
     public void refreshTaskDigitalInput() {
     int address = 2;
     int code = 2;
     int start = 0;
     int count = 24;
     QueryFrame queryFrame = new QueryFrame(address, code, start, count);

     ITask task = new AutoSenderTask(queryFrame, onlineCacheProvider);
     task.execute();
     }*/

    /**
     * 刷新保持寄存器数据
     @Scheduled(fixedDelay = 4 * 1000, initialDelay = 5 * 1000)
     public void refreshTaskStorage() {
     int address = 2;
     int code = 3;
     int start = 0;
     int count = 30;
     QueryFrame queryFrame = new QueryFrame(address, code, start, 2 * count);

     ITask task = new AutoSenderTask(queryFrame, onlineCacheProvider);
     task.execute();
     }*/
}
