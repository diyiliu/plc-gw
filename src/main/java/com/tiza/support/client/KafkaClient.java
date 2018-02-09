package com.tiza.support.client;

import com.tiza.support.util.CommonUtil;
import com.tiza.support.util.JacksonUtil;
import com.tiza.support.util.SpringUtil;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Description: KafkaClient
 * Author: DIYILIU
 * Update: 2018-02-02 09:30
 */

public class KafkaClient {
    private static Logger logger = LoggerFactory.getLogger(KafkaClient.class);

    private final Producer<Integer, String> producer;
    private final String rowDataTopic;
    private String parseDataTopic;

    public KafkaClient(String brokerList, String rowDataTopic, String parseDataTopic) {
        this.rowDataTopic = rowDataTopic;
        this.parseDataTopic = parseDataTopic;

        Properties props = new Properties();
        props.put("metadata.broker.list", brokerList);
        // 消息传递到broker时的序列化方式
        props.put("serializer.class", StringEncoder.class.getName());
        props.put("request.required.acks", "1");
        props.put("producer.type", "async");

        producer = new Producer(new ProducerConfig(props));
    }

    public void sendMessage(String topic, String msg) {
        producer.send(new KeyedMessage(topic, msg));
    }

    public String getRowDataTopic() {
        return rowDataTopic;
    }

    public String getParseDataTopic() {
        return parseDataTopic;
    }

    /**
     * 存入kafka原始指令
     *
     * @param deviceId
     * @param bytes
     */
    public static void toKafka(String deviceId, byte[] bytes, int direction) {
        logger.info("[{}] 设备[{}]原始数据[{}]写入kafka...",
                direction == 1 ? "上行" : "下行", deviceId, CommonUtil.bytesToStr(bytes));

        Map map = new HashMap();
        map.put("id", deviceId);
        map.put("timestamp", System.currentTimeMillis());
        map.put("data", CommonUtil.bytesToStr(bytes));
        map.put("flow", direction);

        KafkaClient kafkaClient = SpringUtil.getBean("kafkaClient");
        kafkaClient.sendMessage(kafkaClient.getRowDataTopic(), JacksonUtil.toJson(map));
    }

    /**
     * 存入kafka解析数据
     *
     * @param id
     * @param paramValues
     */
    public static void toKafka(long id, Map paramValues){
        logger.info("设备[{}]解析数据写入kafka...", id);

        Map map = new HashMap();
        map.put("id", id);
        map.put("timestamp", System.currentTimeMillis());
        map.put("metrics", JacksonUtil.toJson(paramValues));

        KafkaClient kafkaClient = SpringUtil.getBean("kafkaClient");
        kafkaClient.sendMessage(kafkaClient.getParseDataTopic(), JacksonUtil.toJson(map));
    }
}
