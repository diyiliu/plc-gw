package com.tiza.support.config.timer.scheduler;

import com.tiza.support.config.timer.SenderTimer;
import com.tiza.support.model.QueryFrame;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

/**
 * Description: DigitalInputScheduler
 * Author: DIYILIU
 * Update: 2018-02-28 13:22
 */

@Service
@EnableScheduling
public class DigitalInputScheduler extends SenderTimer {

    public DigitalInputScheduler(){
        period = 10;
        initialDay = 6;

        functionId = "2";
        queryFrame = new QueryFrame(2, 2, 0, 24);
    }
}
