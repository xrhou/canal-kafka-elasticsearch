package com.yibao.canaldemo.kafka.listenter;

import com.alibaba.fastjson.JSON;
import com.yibao.canaldemo.canal.TableBean;
import com.yibao.canaldemo.kafka.KafkaMessageProcessFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author houxiurong
 * @date 2019-07-27
 */
@Slf4j
@Component
public class KafkaConsumerListener {

    @Autowired
    private KafkaMessageProcessFactory kafkaMessageProcessFactory;

    @KafkaListener(topics = {"#{'${topics}'.split(',')}"})
    public void listener(ConsumerRecord<?, ?> record) {
        log.info("Consumer--->" + record.topic() + "～～～～～～listener");
        log.info("kafka消息:\n" + record.toString());
        log.info("开始消费[kafka offset ={},topic= {},partition={},key ={},\nvalue={}", record.offset(), record.topic(), record.partition(),
                record.key(), record.value());
        log.info("消费数据:" + record.value().toString());
        //工厂模式处理不同topic类型的消息
        TableBean tableBean = JSON.parseObject(record.value().toString(), TableBean.class);
        kafkaMessageProcessFactory.process(tableBean);
        log.info("消费结束[kafka topic:{},partition:{}]", tableBean.getDatabase() + "_" + tableBean.getTable(), record.partition());
    }
}
