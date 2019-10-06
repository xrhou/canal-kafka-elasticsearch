package com.yibao.canaldemo.kafka;

import lombok.Getter;

import java.util.Objects;

/**
 * @author houxiurong
 * @date 2019-07-26
 */
public enum KafkaTopicEnum {
    /**
     * kafka topic
     * 数据库名+"_"+表名
     */
    DEFAULT_TOPIC("default.topic", "默认topic"),
    PATIENT_DOCTOR_RELATION("yibao_health_yb_patient_doctor_relation", "医生患者关系表topic"),
    PATIENT_GROUP_INSTANCE("yibao_health_yb_doctor_patient_grouping_instance", "医生患者表分组topic"),
    PATIENT_SCAN_LOG("yibao_health_yb_patient_scan_log", "医生患者扫码topic"),
    RECORD_DOCTOR_ADVICE("yibao_health_yb_patient_visit_record_doctor_advice", "医生建议topic"),
    RECORD_DISEASE_RELATION("yibao_health_yb_patient_visit_record_disease_relation", "就诊记录疾病topic"),
    ;

    @Getter
    private String topic;

    @Getter
    private String desc;

    KafkaTopicEnum(String topic, String desc) {
        this.topic = topic;
        this.desc = desc;
    }

    public static KafkaTopicEnum valueOfTopic(String topic) {
        for (KafkaTopicEnum topicEnum : KafkaTopicEnum.values()) {
            if (Objects.equals(topicEnum.topic, topic)) {
                return topicEnum;
            }
        }
        return null;
    }
}
