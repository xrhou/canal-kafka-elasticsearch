package com.yibao.canaldemo.elastic;

import java.util.Map;

/**
 * @author houxiurong
 * @date 2019-07-27
 */
public interface ElasticsearchService {
    /**
     * 新增
     *
     * @param index
     * @param type
     * @param id
     * @param dataMap
     */
    void insertById(String index, String type, String id, Map<String, Object> dataMap);

    /**
     * 批量索引
     *
     * @param index
     * @param type
     * @param idDataMap
     */
    void batchInsertById(String index, String type, Map<String, Map<String, Object>> idDataMap);

    /***
     * 更新
     * @param index
     * @param type
     * @param id
     * @param dataMap
     */
    void update(String index, String type, String id, Map<String, Object> dataMap);

    /**
     * 删除
     *
     * @param index
     * @param type
     * @param id
     */
    void deleteById(String index, String type, String id);

}
