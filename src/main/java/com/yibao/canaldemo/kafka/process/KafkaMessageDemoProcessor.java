package com.yibao.canaldemo.kafka.process;

import com.alibaba.fastjson.JSON;
import com.yibao.canaldemo.canal.TableBean;
import com.yibao.canaldemo.kafka.KafkaMessageProcess;
import com.yibao.canaldemo.kafka.KafkaTopicEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author houxiurong
 * @date 2019/07/05
 */
@Slf4j
@Service
public class KafkaMessageDemoProcessor implements KafkaMessageProcess {

    @Override
    public KafkaTopicEnum getTopic() {
        return KafkaTopicEnum.DEFAULT_TOPIC;
    }

    @Override
    public void process(TableBean message) {
        log.info("消费:" + JSON.toJSONString(message));
    }

}
