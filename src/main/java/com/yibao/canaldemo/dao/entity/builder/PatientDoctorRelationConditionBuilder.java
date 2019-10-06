package com.yibao.canaldemo.dao.entity.builder;

import lombok.Builder;

import java.util.List;

/**
 * @author houxiurong
 * @date 2019/7/8
 */
@Builder
public class PatientDoctorRelationConditionBuilder {

    /**
     * 自增ID
     */
    private Integer id;

    /**
     * id的List条件
     */
    private List<Integer> idList;

    /**
     * 是否删除（0:未删除 1:已删除）
     */
    private Integer isDeleted;

    /**
     * 记录创建时间
     */
    private java.time.LocalDateTime createTime;

    /**
     * 记录修改时间
     */
    private java.time.LocalDateTime modifyTime;

    /**
     * 患者ID
     */
    private Integer patientId;

    /**
     * patientId的List条件
     */
    private List<Integer> patientIdList;

    /**
     * 医生ID
     */
    private Integer doctorId;

    /**
     * doctorId的List条件
     */
    private List<Integer> doctorIdList;

    /**
     * 关系来源（1:扫码关注 2:服务订单）
     */
    private Integer sourceType;

    /**
     * sourceType的List条件
     */
    private List<Integer> sourceTypeList;

}
