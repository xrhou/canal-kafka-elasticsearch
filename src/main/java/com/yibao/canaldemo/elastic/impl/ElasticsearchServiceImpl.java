package com.yibao.canaldemo.elastic.impl;

import com.alibaba.fastjson.JSON;
import com.yibao.canaldemo.dao.entity.PatientDoctorRelationDO;
import com.yibao.canaldemo.dao.mapper.PatientDoctorRelationMapper;
import com.yibao.canaldemo.elastic.ElasticsearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author houxiurong
 * @date 2019-07-27
 */
@Slf4j
@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Resource
    private PatientDoctorRelationMapper patientDoctorRelationMapper;

    @Override
    public void insertById(String index, String type, String id, Map<String, Object> dataMap) {
        log.info("=============================================================");
        log.info("开始查询数据库patientDoctorRelation->id=" + Integer.valueOf(id));
        PatientDoctorRelationDO patientDoctorRelation = patientDoctorRelationMapper.selectById(Integer.valueOf(id));
        log.info("数据库查询patientDoctorRelation->data:{}", JSON.toJSONString(patientDoctorRelation));
        Client transportClient = elasticsearchTemplate.getClient();
        IndexRequestBuilder indexRequestBuilder = transportClient.prepareIndex(index, type, id)
                .setSource(JSON.toJSONString(dataMap), XContentType.JSON);
        IndexResponse response = indexRequestBuilder.execute().actionGet();
        try {
            if (response.status().equals(RestStatus.OK)) {
                log.info("insert elasticsearch data success");
            }
        } catch (Exception e) {
            log.error("insert elasticsearch data  fail", e);
        }
        log.info("落数据到Elasticsearch-patientDoctorRelation->es-data:{}", JSON.toJSONString(dataMap));
        log.info("=============================================================");
    }

    @Override
    public void batchInsertById(String index, String type, Map<String, Map<String, Object>> idDataMap) {
        BulkRequestBuilder bulkRequestBuilder = elasticsearchTemplate.getClient().prepareBulk();
        idDataMap.forEach((id, dataMap) -> bulkRequestBuilder.add(elasticsearchTemplate.getClient().prepareIndex(index, type, id).setSource(dataMap)));
        try {
            BulkResponse bulkResponse = bulkRequestBuilder.execute().get();
            if (bulkResponse.hasFailures()) {
                log.error("elasticsearch批量插入错误, index=" + index + ", type=" + type + ", data=" + JSON.toJSONString(idDataMap) + ", cause:" + bulkResponse.buildFailureMessage());
            }
        } catch (Exception e) {
            log.error("elasticsearch批量插入错误, index=" + index + ", type=" + type + ", data=" + JSON.toJSONString(idDataMap), e);
        }
    }

    @Override
    public void update(String index, String type, String id, Map<String, Object> dataMap) {
        this.insertById(index, type, id, dataMap);
    }

    @Override
    public void deleteById(String index, String type, String id) {
        elasticsearchTemplate.getClient().prepareDelete(index, type, id).get();
    }
}
