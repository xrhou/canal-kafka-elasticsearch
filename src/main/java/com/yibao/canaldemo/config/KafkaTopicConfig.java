package com.yibao.canaldemo.config;

import com.google.common.collect.Sets;
import com.yibao.canaldemo.kafka.KafkaTopicEnum;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * @author houxiurong
 * @date 2019-07-26
 */
@Configuration
public class KafkaTopicConfig implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        Set<String> topicSet = Sets.newHashSet();
        for (KafkaTopicEnum topicEnum : KafkaTopicEnum.values()) {
            topicSet.add(topicEnum.getTopic());
        }
        System.setProperty("topics", StringUtils.join(topicSet, ","));
    }
}