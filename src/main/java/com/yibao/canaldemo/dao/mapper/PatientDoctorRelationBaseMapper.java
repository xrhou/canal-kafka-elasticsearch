package com.yibao.canaldemo.dao.mapper;

import com.yibao.canaldemo.dao.entity.PatientDoctorRelationDO;
import com.yibao.canaldemo.dao.entity.builder.PatientDoctorRelationConditionBuilder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author houxiurong
 * @date 2019/7/8
 */
@Repository
public interface PatientDoctorRelationBaseMapper {

    /**
     * 插入（匹配有值的字段）
     *
     * @param record PatientDoctorRelationDO
     * @return int
     */
    int insertSelective(PatientDoctorRelationDO record);

    /**
     * 根据主键ID更新（匹配有值的字段）
     *
     * @param record PatientDoctorRelationDO
     * @return int
     */
    int updateById(PatientDoctorRelationDO record);

    /**
     * 动态条件查询（匹配有值的字段）
     *
     * @param params 筛选条件
     * @return List<PatientDoctorRelationDO>
     */
    List<PatientDoctorRelationDO> selectByCondition(PatientDoctorRelationConditionBuilder params);

    /**
     * 根据主键ID查询
     *
     * @param id 主键ID
     * @return PatientDoctorRelationDO
     */
    PatientDoctorRelationDO selectById(@Param("id") Integer id);
}