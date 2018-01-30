package com.tiza.gw.handler.codec;

import com.tiza.support.cache.ICache;
import com.tiza.support.util.CommonUtil;
import com.tiza.support.config.Constant;
import com.tiza.support.util.SpringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Description: DtuDecoder
 * Author: DIYILIU
 * Update: 2018-01-26 10:41
 */
public class DtuDecoder extends ByteToMessageDecoder {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < 4) {

            return;
        }
        // 绑定数据
        Attribute attribute = ctx.channel().attr(AttributeKey.valueOf(Constant.NETTY_DEVICE_ID));

        in.markReaderIndex();
        byte b1 = in.readByte();
        byte b2 = in.readByte();
        byte b3 = in.readByte();

        if (0x40 == b1 && b1 == b2 && b2 == b3) {
            byte[] bytes = new byte[in.readableBytes()];
            in.readBytes(bytes);

            String deviceId = new String(bytes);
            register(deviceId, attribute, ctx);
        } else if (0x24 == b1 && b1 == b2 && b2 == b3) {
            byte[] bytes = new byte[in.readableBytes()];
            in.readBytes(bytes);

            String deviceId = new String(bytes);
            if (attribute.get() == null) {
                register(deviceId, attribute, ctx);
            }

            logger.info("收到设备[{}]心跳...", deviceId);
        } else {
            String deviceId = (String) attribute.get();
            if (deviceId == null) {
                logger.error("设备未注册, 断开连接!");
                ctx.close();
                return;
            }

            in.resetReaderIndex();
            in.markReaderIndex();

            // 读取 地址、功能码
            in.readShort();
            // 内容长度
            int length = in.readUnsignedByte();

            if (in.readableBytes() < length + 2) {

                in.resetReaderIndex();
                return;
            }
            in.resetReaderIndex();

            byte[] bytes = new byte[3 + length];
            in.readBytes(bytes);

            // CRC校验码
            byte crc0 = in.readByte();
            byte crc1 = in.readByte();

            logger.info("收到设备[{}]数据[{}]...", deviceId,
                    CommonUtil.bytesToString(Unpooled.copiedBuffer(bytes, new byte[]{crc0, crc1}).array()));

            byte[] checkCRC = CommonUtil.checkCRC(bytes);
            if (crc0 != checkCRC[0] || crc1 != checkCRC[1]) {
                logger.error("CRC校验码错误, 断开连接!");
                ctx.close();
                return;
            }

            out.add(Unpooled.copiedBuffer(bytes));
        }
    }

    /**
     * 设备注册
     *
     * @param deviceId
     * @param attribute
     * @param context
     */
    private void register(String deviceId, Attribute attribute, ChannelHandlerContext context) {
        logger.info("设备[{}]注册...", deviceId);
        attribute.set(deviceId);

        // 加入在线列表
        ICache online = SpringUtil.getBean("onlineCacheProvider");
        online.put(deviceId, context);
    }
}
