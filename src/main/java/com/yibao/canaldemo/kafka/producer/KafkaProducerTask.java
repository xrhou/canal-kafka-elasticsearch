package com.yibao.canaldemo.kafka.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.concurrent.Future;

/**
 * kafka的producer
 *
 * @author houxiurong
 * @date 2019-07-26
 */
@Component
public class KafkaProducerTask {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 通过kafkaProducer发送消息
     *
     * @param message 具体消息值
     */
    @Async("myExecutor")
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
    public Future<String> sendKafkaMessage(String topic, final String message) {
        Objects.requireNonNull(topic, "topic不能为空");
        Objects.requireNonNull(message, "data不能为空");
        /**
         * 1、如果指定了某个分区,会只将消息发到这个分区上
         * 2、如果同时指定了某个分区和key,则也会将消息发送到指定分区上,key不起作用
         * 3、如果没有指定分区和key,那么将会随机发送到topic的分区中 (int)(Math.random()*5)
         * 4、如果指定了key,那么将会以hash<key>的方式发送到分区中
         */
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, 0, null, message);
        kafkaTemplate.send(record);
        return new AsyncResult<>("send kafka message accomplished!");
    }

}