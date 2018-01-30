package com.tiza.support.task;

import com.tiza.support.cache.ICache;
import com.tiza.support.model.QueryFrame;
import com.tiza.support.util.CommonUtil;
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

    public AutoSenderTask() {

    }

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

        ByteBuf byteBuf = Unpooled.buffer(6);
        byteBuf.writeByte(queryFrame.getAddress());
        byteBuf.writeByte(queryFrame.getCode());
        byteBuf.writeShort(queryFrame.getStart());
        byteBuf.writeShort(queryFrame.getCount());
        byte[] bytes = byteBuf.array();

        logger.info(" 在线终端{}, 下发查询指令, [从站地址:{}, 功能码:{}, 内容:{}]...",
                onlineCache.getKeys(), queryFrame.getAddress(), queryFrame.getCode(), CommonUtil.bytesToStr(bytes));

        keys.forEach(e -> {
            ChannelHandlerContext context = (ChannelHandlerContext) onlineCache.get(e);
            context.writeAndFlush(Unpooled.copiedBuffer(bytes));
        });
    }
}
