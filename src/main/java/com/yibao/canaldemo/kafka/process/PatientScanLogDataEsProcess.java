package com.yibao.canaldemo.kafka.process;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.yibao.canaldemo.canal.EventType;
import com.yibao.canaldemo.canal.TableBean;
import com.yibao.canaldemo.kafka.KafkaMessageProcess;
import com.yibao.canaldemo.kafka.KafkaTopicEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author houxiurong
 * @date 2019-07-27
 */
@Slf4j
@Service
public class PatientScanLogDataEsProcess implements KafkaMessageProcess {

    @Override
    public KafkaTopicEnum getTopic() {
        return KafkaTopicEnum.PATIENT_SCAN_LOG;
    }

    @Override
    public void process(TableBean tableBean) {
        log.info("PatientScanLogDataEsProcess,tableBean:" + JSON.toJSONString(tableBean));
        switch (EventType.valueOfType(tableBean.getType()).getCode()) {
            case CanalEntry.EventType.INSERT_VALUE:
                log.info("canalBean.getEventType():{},CanalEntry.EventType.INSERT_VALUE:{}", tableBean.getType(), CanalEntry.EventType.INSERT_VALUE);
                Map insertRowData = tableBean.getData().get(0);
                log.info("doctorId=" + insertRowData.get("doctor_id") + ",patientId=" + insertRowData.get("patient_id"));
                //insertEs(InsertRowData);
                break;
            case CanalEntry.EventType.UPDATE_VALUE:
                log.info("tableBean.getEventType():{},tableBean.EventType.UPDATE_VALUE:{}", tableBean.getType(), CanalEntry.EventType.UPDATE_VALUE);
                Map updateRowData = tableBean.getData().get(0);
                log.info("doctorId=" + updateRowData.get("doctor_id") + ",patientId=" + updateRowData.get("patient_id"));
                //updateEs(updateRowData);
                break;
            case CanalEntry.EventType.DELETE_VALUE:
                log.info("tableBean.getEventType(),tableBean.EventType.DELETE_VALUE:{}", tableBean.getType(), CanalEntry.EventType.DELETE_VALUE);
                Map deleteRowData = tableBean.getData().get(0);
                log.info("doctorId=" + deleteRowData.get("doctor_id") + ",patientId=" + deleteRowData.get("patient_id"));
                //updateEs(deleteRowData);
                break;
        }
    }
}
