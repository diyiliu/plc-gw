package com.tiza.support.client;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;

import java.util.Properties;

/**
 * Description: KafkaClient
 * Author: DIYILIU
 * Update: 2018-02-02 09:30
 */

public class KafkaClient {
    private final Producer<Integer, String> producer;
    private String topic;

    public KafkaClient(String brokerList, String topic) {
        this.topic = topic;

        Properties props = new Properties();
        props.put("metadata.broker.list", brokerList);
        // 消息传递到broker时的序列化方式
        props.put("serializer.class", StringEncoder.class.getName());
        props.put("request.required.acks", "1");
        props.put("producer.type", "async");

        producer = new Producer(new ProducerConfig(props));
    }

    public void sendMessage(String msg){
        producer.send(new KeyedMessage(topic, msg));
    }
}
