package com.yibao.canaldemo;


import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author houxiurong
 * @date 2019-07-26
 */
@SpringBootApplication(scanBasePackages = {"com.yibao.canaldemo"})
@MapperScan(basePackages = {"com.yibao.cannaldemo.dao"})
@EnableAsync
public class CanalKafkaApplication extends SpringBootServletInitializer {
    protected final static Logger logger = LoggerFactory.getLogger(CanalKafkaApplication.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(CanalKafkaApplication.class);
    }

    public static void main(String[] args) {
        logger.info("## started the CanalKafkaApplication ##");
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        SpringApplication.run(CanalKafkaApplication.class, args);
    }
}
