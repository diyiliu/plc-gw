package com.tiza.support.task;

import com.tiza.support.cache.ICache;
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

    public AutoSenderTask(int address, int code, int start, int count, ICache onlineCache) {
        this.address = address;
        this.code = code;
        this.start = start;
        this.count = count;
        this.onlineCache = onlineCache;
    }

    // 从站地址
    private int address;

    // 功能码
    private int code;

    // 起始地址
    private int start;

    // 数据量
    private int count;

    // 在线设备
    private ICache onlineCache;

    @Override
    public void execute() {
        Set keys = onlineCache.getKeys();

        ByteBuf byteBuf = Unpooled.buffer(6);
        byteBuf.writeByte(address);
        byteBuf.writeByte(code);
        byteBuf.writeShort(start);
        byteBuf.writeShort(count);
        byte[] bytes = byteBuf.array();

        logger.info(" 在线终端{}, 下发查询指令, [从站地址:{}, 功能码:{}, 内容:{}]...",
                onlineCache.getKeys(), address, code, CommonUtil.bytesToStr(bytes));

        keys.forEach(e -> {
            ChannelHandlerContext context = (ChannelHandlerContext) onlineCache.get(e);
            context.writeAndFlush(Unpooled.copiedBuffer(bytes));
        });
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ICache getOnlineCache() {
        return onlineCache;
    }

    public void setOnlineCache(ICache onlineCache) {
        this.onlineCache = onlineCache;
    }
}
