package com.yibao.canaldemo.kafka.listenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.yibao.canaldemo.canal.EventType;
import com.yibao.canaldemo.canal.TableBean;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 原始yb_patient_doctor_relation的处理
 *
 * @author houxiurong
 * @date 2019-07-26
 */
//@Component
public class DataConsumerListener {
    private Logger logger = LoggerFactory.getLogger(DataConsumerListener.class);

    //@KafkaListener(topics = {"#{'${topics}'.split(',')}"})
    public void listen(ConsumerRecord<?, ?> record) {
        logger.info("Consumer--->" + record.topic() + "～～～～～～listen");
        logger.info("kafka消息:\n" + record.toString());
        logger.info("offset ={},topic= {},partition={},key ={},\nvalue={}", record.offset(), record.topic(), record.partition(), record.key(), record.value());
        logger.info("数据记录:" + record.value().toString());
        TableBean tableBean = JSON.parseObject(record.value().toString(), TableBean.class);
        logger.info("tableBean:" + JSON.toJSONString(tableBean));
        switch (EventType.valueOfType(tableBean.getType()).getCode()) {
            case CanalEntry.EventType.INSERT_VALUE:
                logger.info("canalBean.getEventType():" + tableBean.getType());
                logger.info("CanalEntry.EventType.INSERT_VALUE:" + CanalEntry.EventType.INSERT_VALUE);
                Map InsertRowData = tableBean.getData().get(0);
                logger.info("doctorId=" + InsertRowData.get("doctor_id") + ",patientId=" + InsertRowData.get("patient_id"));
                insertEs(InsertRowData);
                break;
            case CanalEntry.EventType.UPDATE_VALUE:
                logger.info("tableBean.getEventType():" + tableBean.getType());
                logger.info("tableBean.EventType.UPDATE_VALUE:" + CanalEntry.EventType.UPDATE_VALUE);
                Map updateRowData = tableBean.getData().get(0);
                //logger.info("doctorId=" + updateRowData.get("doctor_id") + ",patientId=" + updateRowData.get("patient_id"));
                updateEs(updateRowData);
                break;
            case CanalEntry.EventType.DELETE_VALUE:
                logger.info("tableBean.getEventType():" + tableBean.getType());
                logger.info("tableBean.EventType.DELETE_VALUE:" + CanalEntry.EventType.DELETE_VALUE);
                Map deleteRowData = tableBean.getData().get(0);
                //logger.info("doctorId=" + deleteRowData.get("doctor_id") + ",patientId=" + deleteRowData.get("patient_id"));
                updateEs(deleteRowData);
                break;
            case CanalEntry.EventType.CREATE_VALUE:
                //todo
                break;
            case CanalEntry.EventType.ALTER_VALUE:
                //todo
                break;
            case CanalEntry.EventType.ERASE_VALUE:
                //todo
                break;
            case CanalEntry.EventType.QUERY_VALUE:
                break;
        }
    }

    public void insertEs(Map bizData) {
        System.out.println("=====insert调用Elasticsearch服务更新索引=======");
        //System.out.println("doctorId=" + bizData.get("doctor_id") + ",patientId=" + bizData.get("patient_id"));
    }

    public void updateEs(Map bizData) {
        System.out.println("=====update调用Elasticsearch服务更新索引=======");
        //System.out.println("doctorId=" + bizData.get("doctor_id") + ",patientId=" + bizData.get("patient_id"));
    }


}
