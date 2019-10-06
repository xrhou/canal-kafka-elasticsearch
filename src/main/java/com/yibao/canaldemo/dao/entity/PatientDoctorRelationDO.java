package com.yibao.canaldemo.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author houxiurong
 * @date 2019/7/8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDoctorRelationDO {

    /**
     * 自增ID
     */
    private Integer id;

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
     * 创建人,0表示无创建人值
     */
    private Integer creator;

    /**
     * 修改人,如果为0则表示纪录未修改
     */
    private Integer modifier;

    /**
     * 患者ID
     */
    private Integer patientId;

    /**
     * 医生ID
     */
    private Integer doctorId;

    /**
     * 关系来源（1:扫码关注 2:服务订单）
     */
    private Integer sourceType;

}
