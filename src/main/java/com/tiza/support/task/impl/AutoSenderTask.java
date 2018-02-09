package com.tiza.support.task.impl;

import com.tiza.support.cache.ICache;
import com.tiza.support.model.QueryFrame;
import com.tiza.support.model.SendMsg;
import com.tiza.support.task.ITask;
import com.tiza.support.util.SpringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Description: AutoSenderTask
 * Author: DIYILIU
 * Update: 2018-01-29 10:45
 */

public class AutoSenderTask implements ITask {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public AutoSenderTask(QueryFrame queryFrame, ICache onlineCache) {
        this.queryFrame = queryFrame;
        this.onlineCache = onlineCache;
    }

    /** 发送数据帧 */
    private QueryFrame queryFrame;

    /** 在线设备 */
    private ICache onlineCache;

    @Override
    public void execute() {
        Set keys = onlineCache.getKeys();
        if (keys.size() < 1){

            return;
        }

        ByteBuf byteBuf = Unpooled.buffer(6);
        byteBuf.writeByte(queryFrame.getAddress());
        byteBuf.writeByte(queryFrame.getCode());
        byteBuf.writeShort(queryFrame.getStart());
        byteBuf.writeShort(queryFrame.getCount());
        byte[] bytes = byteBuf.array();

        ICache sendMsgCache = SpringUtil.getBean("sendMsgCacheProvider");
        keys.forEach(e -> {
            // 如果有下发指令没回复，则不下发本次指令
            if (sendMsgCache.containsKey(e)){
                logger.warn("下行指令尚未响应，取消终端[{}]本次指令[{}]下发...", e, queryFrame.getCode());

                SendMsg sendMsg = (SendMsg) sendMsgCache.get(e);
                if (sendMsg.getTime() > 2){
                    sendMsgCache.remove(e);
                }
            }else {
                ChannelHandlerContext context = (ChannelHandlerContext) onlineCache.get(e);
                context.writeAndFlush(Unpooled.copiedBuffer(bytes));

                SendMsg sendMsg = new SendMsg();
                sendMsg.setDeviceId((String) e);
                sendMsg.setCmd(queryFrame.getCode());
                sendMsg.setBytes(bytes);

                sendMsgCache.put(e, sendMsg);
            }
        });
    }
}
