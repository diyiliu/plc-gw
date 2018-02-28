package com.tiza.support.config.timer.scheduler;

import com.tiza.support.config.timer.SenderTimer;
import com.tiza.support.model.QueryFrame;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.stereotype.Service;

/**
 * Description: DigitalOutputScheduler
 * Author: DIYILIU
 * Update: 2018-02-28 10:49
 */

@Service
@EnableScheduling
public class DigitalOutputScheduler extends SenderTimer implements SchedulingConfigurer {

     public DigitalOutputScheduler(){
         period = 10;
         initialDay = 3;

         functionId = "1";
         queryFrame = new QueryFrame(2, 1, 0, 16);
     }
}
