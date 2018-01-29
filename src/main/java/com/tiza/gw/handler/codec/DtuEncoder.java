package com.tiza.gw.handler.codec;

import com.tiza.support.util.CommonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Description: DtuEncoder
 * Author: DIYILIU
 * Update: 2018-01-26 10:41
 */

public class DtuEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {

        if (msg == null){

            return;
        }
        ByteBuf byteBuf = (ByteBuf) msg;

        // 下发内容
        byte[] content = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(content);

        // 校验位
        byte[] crc = CommonUtil.checkCRC(content);

        ByteBuf buf = Unpooled.copiedBuffer(content, crc);
        out.writeBytes(buf);
    }
}
