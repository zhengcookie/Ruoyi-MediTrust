package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.MedicalRecord;

/**
 * 医疗记录 数据层
 * 
 * @author ruoyi
 */
public interface MedicalRecordMapper
{
    /**
     * 查询医疗记录
     * 
     * @param id 医疗记录ID
     * @return 医疗记录
     */
    public MedicalRecord selectMedicalRecordById(Integer id);

    /**
     * 查询医疗记录列表
     * 
     * @param MedicalRecord 医疗记录
     * @return 医疗记录集合
     */
    public List<MedicalRecord> selectMedicalRecordList(MedicalRecord MedicalRecord);

    /**
     * 根据患者姓名查询医疗记录
     * 
     * @param patientName 患者姓名
     * @return 医疗记录集合
     */
    public List<MedicalRecord> selectMedicalRecordByPatientName(String patientName);

    /**
     * 根据医生姓名查询医疗记录
     * 
     * @param doctorName 医生姓名
     * @return 医疗记录集合
     */
    public List<MedicalRecord> selectMedicalRecordByDoctorName(String doctorName);

    /**
     * 新增医疗记录
     * 
     * @param MedicalRecord 医疗记录
     * @return 结果
     */
    public int insertMedicalRecord(MedicalRecord MedicalRecord);

    /**
     * 修改医疗记录
     * 
     * @param MedicalRecord 医疗记录
     * @return 结果
     */
    public int updateMedicalRecord(MedicalRecord MedicalRecord);

    /**
     * 删除医疗记录
     * 
     * @param id 医疗记录ID
     * @return 结果
     */
    public int deleteMedicalRecordById(Integer id);

    /**
     * 批量删除医疗记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteMedicalRecordByIds(Integer[] ids);
}