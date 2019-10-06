package com.yibao.canaldemo.kafka;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.yibao.canaldemo.canal.TableBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author houxiurong
 * @date 2019-07-27
 */
@Slf4j
@Service
public class KafkaMessageProcessFactory {

    private static final Map<KafkaTopicEnum, KafkaMessageProcess> processMap = Maps.newConcurrentMap();

    @Autowired
    public KafkaMessageProcessFactory(List<KafkaMessageProcess> processList) {
        processList.forEach(m -> processMap.put(m.getTopic(), m));
    }

    public void process(TableBean message) {
        KafkaMessageProcess process = processMap.get(KafkaTopicEnum.valueOfTopic(message.getDatabase() + "_" + message.getTable()));
        if (process == null) {
            log.error("找不到该topic={}的对应process message = {}",
                    (message.getDatabase() + "_" + message.getTable()), JSON.toJSONString(message));
            return;
        }

        try {
            log.info("开始消费消息:" + JSON.toJSONString(message));
            process.process(message);
            log.info("消费消息结束,数据更新类型: " + message.getType());
        } catch (Exception e) {
            log.error("消费失败message = " + JSON.toJSONString(message), e);
        }

    }


}
