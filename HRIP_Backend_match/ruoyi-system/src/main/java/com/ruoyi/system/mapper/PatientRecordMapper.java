package com.ruoyi.system.mapper;


import com.ruoyi.system.domain.MedicalHealthCase;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
//import com.baomidou.mybatisplus.core.mapper.BaseMapper;
/**
 * @description patient_recordMapper
 * @author jiabo
 * @date 2023-08-06
 */
@Mapper
@Repository
public interface PatientRecordMapper {


    @Insert("insert into medical_health_case" +
            " (name,age,sex,category,time,id)" +
            " values(#{patientRecord.name},#{patientRecord.age}," +
            "#{patientRecord.sex},#{patientRecord.category},#{patientRecord.time},#{patientRecord.id})")
    public Integer insert(@Param("patientRecord") MedicalHealthCase patientRecord);
}
