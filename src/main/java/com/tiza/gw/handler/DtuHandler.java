package com.tiza.gw.handler;

import com.tiza.support.cache.ICache;
import com.tiza.support.config.Constant;
import com.tiza.support.util.SpringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: DtuHandler
 * Author: DIYILIU
 * Update: 2018-01-26 10:39
 */

public class DtuHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Attribute attribute;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("建立连接...");
        attribute = ctx.channel().attr(AttributeKey.valueOf(Constant.NETTY_DEVICE_ID));

        // 断开连接
        ctx.channel().closeFuture().addListener(
                (ChannelFuture future) -> {
                    String deviceId = (String) attribute.get();
                    if (StringUtils.isNotEmpty(deviceId)) {
                        logger.info("设备[{}]断开连接...", deviceId);

                        ICache online = SpringUtil.getBean("onlineCacheProvider");
                        online.remove(deviceId);
                    }
                }
        );
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String deviceId = (String) attribute.get();

        ByteBuf byteBuf = (ByteBuf) msg;

        int addr = byteBuf.readUnsignedByte();
        int code = byteBuf.readUnsignedByte();

        int length = byteBuf.readUnsignedByte();
        if (length % 4 != 0) {

            return;
        }

        int count = length / 4;
        for (int i = 0; i < count; i++) {
            int data = byteBuf.readInt();
            float f = Float.intBitsToFloat(data);

            System.out.println(i + 1 + ":" + f);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("服务器异常...{}", cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
