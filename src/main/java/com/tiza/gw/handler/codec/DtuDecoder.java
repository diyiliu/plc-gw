package com.tiza.gw.handler.codec;

import com.tiza.support.cache.ICache;
import com.tiza.support.client.KafkaClient;
import com.tiza.support.config.Constant;
import com.tiza.support.util.CommonUtil;
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
        // 设备缓存
        ICache deviceCache = SpringUtil.getBean("deviceCacheProvider");

        in.markReaderIndex();
        byte b1 = in.readByte();
        byte b2 = in.readByte();
        byte b3 = in.readByte();

        String deviceId;
        if (b1 == b2 && b1 == b3) {
            byte[] content = new byte[in.readableBytes()];
            in.readBytes(content);

            deviceId = new String(content);
            byte[] bytes = Unpooled.copiedBuffer(new byte[]{b1, b2, b2}, content).array();
            // 写入kafka
            KafkaClient.toKafka(deviceId, bytes);

            if (0x40 == b1) {
                register(deviceId, attribute, ctx);
            } else if (0x24 == b1) {
                // 未注册重新注册
                if (attribute.get() == null) {
                    register(deviceId, attribute, ctx);
                }
            }
        } else {
            deviceId = (String) attribute.get();
            if (deviceId == null) {
                logger.error("设备未注册, 断开连接!");
                ctx.close();
                return;
            }
            in.resetReaderIndex();

            // 读取 地址(byte) + 功能码(byte)
            in.readShort();
            // 内容长度
            int length = in.readUnsignedByte();

            if (in.readableBytes() < length + 2) {

                in.resetReaderIndex();
                return;
            }

            in.resetReaderIndex();

            byte[] content = new byte[3 + length];
            in.readBytes(content);

            // CRC校验码
            byte crc0 = in.readByte();
            byte crc1 = in.readByte();

            byte[] bytes = Unpooled.copiedBuffer(content, new byte[]{crc0, crc1}).array();
            //logger.info("收到设备[{}]原始数据[{}]...", deviceId, CommonUtil.bytesToStr(bytes));

            // 写入kafka
            KafkaClient.toKafka(deviceId, bytes);

            byte[] checkCRC = CommonUtil.checkCRC(content);
            if (crc0 != checkCRC[0] || crc1 != checkCRC[1]) {
                logger.error("CRC校验码错误, 断开连接!");
                ctx.close();
                return;
            }

            if (deviceCache.containsKey(deviceId)) {
                out.add(Unpooled.copiedBuffer(content));
            }else{
                logger.warn("设备[{}]不存在!", deviceId);
            }
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

        ICache online = SpringUtil.getBean("onlineCacheProvider");
        online.put(deviceId, context);
    }

}
