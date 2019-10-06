package com.yibao.canaldemo.kafka.producer;

import com.alibaba.fastjson.JSON;
import com.yibao.canaldemo.canal.TableBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * kafka的producer
 *
 * @author houxiurong
 * @date 2019-07-26
 */
@Component
public class HandlerProducer {

    private Logger logger = LoggerFactory.getLogger(HandlerProducer.class);

    @Autowired
    private KafkaProducerTask kafkaProducerTask;

    /**
     * 多线程同步提交
     *
     * @param tableBean canal传递过来的bean
     * @param waiting   是否等待线程执行完成 true:可以及时看到结果;
     *                  false:让线程继续执行，并跳出此方法返回调用方主程序;
     */
    public void sendMessage(TableBean tableBean, boolean waiting) {
        String canalBeanJsonStr = JSON.toJSONString(tableBean);
        Future<String> sendMessage = kafkaProducerTask.sendKafkaMessage(tableBean.getDatabase() + "_" + tableBean.getTable(), canalBeanJsonStr);
        logger.info("HandlerProducer日志-->当前线程:" + Thread.currentThread().getName() + ",接受的canalBeanJsonStr:" + canalBeanJsonStr);
        if (waiting) {
            try {
                sendMessage.get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Send kafka message job thread pool await termination time out.", e);
            }
        }
    }


}
