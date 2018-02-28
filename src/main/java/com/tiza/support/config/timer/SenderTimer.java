package com.tiza.support.config.timer;

import com.tiza.support.cache.ICache;
import com.tiza.support.model.QueryFrame;
import com.tiza.support.task.impl.AutoSenderTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.PeriodicTrigger;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Description: SenderTimer
 * Author: DIYILIU
 * Update: 2018-02-28 11:31
 */

public class SenderTimer implements SchedulingConfigurer {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    // 功能集编号
    protected String functionId;

    // 执行频率
    protected long period = 10;

    // 延时启动
    protected long initialDay = 5;

    // 查询结构
    protected QueryFrame queryFrame;

    @Resource
    protected ICache onlineCacheProvider;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        taskRegistrar.addTriggerTask(new AutoSenderTask(queryFrame, onlineCacheProvider),
                (TriggerContext triggerContext) -> {
                    PeriodicTrigger trigger = new PeriodicTrigger(period, TimeUnit.SECONDS);
                    trigger.setInitialDelay(initialDay);
                    trigger.setFixedRate(true);

                    return trigger.nextExecutionTime(triggerContext);
                });
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public String getFunctionId() {
        return functionId;
    }
}
