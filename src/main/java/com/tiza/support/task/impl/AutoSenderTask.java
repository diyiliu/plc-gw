package com.tiza.support.task.impl;

import com.tiza.support.cache.ICache;
import com.tiza.support.model.QueryFrame;
import com.tiza.support.task.ITask;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.util.Set;

/**
 * Description: AutoSenderTask
 * Author: DIYILIU
 * Update: 2018-01-29 10:45
 */

public class AutoSenderTask implements ITask {

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

        keys.forEach(e -> {
            ChannelHandlerContext context = (ChannelHandlerContext) onlineCache.get(e);
            context.writeAndFlush(Unpooled.copiedBuffer(bytes));
        });
    }
}
