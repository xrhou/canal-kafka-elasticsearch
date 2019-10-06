package com.yibao.canaldemo.canal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 消息对应com.alibaba.otter.canal.protocol->FlatMessage
 *
 * @author houxiurong
 * @date 2019-07-26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TableBean implements Serializable {
    /**
     * 数据库
     */
    private String database;
    /**
     * 表
     */
    private String table;

    /**
     * 操作类型
     */
    private String type;

    /**
     * 行数据
     */
    private List<Map<String, String>> data;

    /**
     * 更新时-行旧数据
     */
    private List<Map<String, String>> old;

    /**
     * 是否是ddl语句
     */
    private Boolean isDdl;

    /**
     * binlog executeTime
     */
    private Long es;

    /**
     * 操作时间
     */
    private Long ts;

    private String sql;

    /**
     * 表主键
     */
    private List<String> pkName;

    /**
     * 表字段语句
     */
    private Map<String, String> mysqlType;

    /**
     * 表字段类型
     */
    private Map<String, Integer> sqlType;
}
