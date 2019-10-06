package com.yibao.canaldemo.dao.mapper;

import com.yibao.canaldemo.dao.entity.PatientDoctorRelationDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author houxiurong
 * @date 2019/7/8
 */
@Mapper
public interface PatientDoctorRelationMapper extends PatientDoctorRelationBaseMapper {

    /**
     * 获取全量患者医生关系数据
     *
     * @return List
     */
    List<PatientDoctorRelationDO> findAllDoctorPatientRelationList();

    /**
     * 获取患者医生关系数据
     *
     * @param doctorRelationList 患者医生关系List
     * @return List
     */
    List<PatientDoctorRelationDO> getDoctorPatientRelationList(@Param("patientDoctorRelationList") List<PatientDoctorRelationDO> doctorRelationList);

}