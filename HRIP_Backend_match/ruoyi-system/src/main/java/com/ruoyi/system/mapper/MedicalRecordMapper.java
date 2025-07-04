package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.MedicalRecord;

/**
 * 医疗记录 数据层
<<<<<<< HEAD
 *
=======
 * 
>>>>>>> ceece8c (实现多选删除功能)
 * @author ruoyi
 */
public interface MedicalRecordMapper
{
    /**
<<<<<<< HEAD
     * 根据条件分页查询医疗记录
     *
     * @param medicalRecord 医疗记录信息
     * @return 医疗记录集合信息
     */
    public List<MedicalRecord> selectMedicalRecordList(MedicalRecord medicalRecord);

    /**
     * 根据所有医疗记录
     *
     * @return 医疗记录集合信息
     */
    public List<MedicalRecord> selectMedicalRecordAll();

    /**
     * 根据医疗记录ID查询信息
     *
     * @param id 医疗记录ID
     * @return 医疗记录信息
     */
    public MedicalRecord selectMedicalRecordById(Long id);

    /**
     * 通过医疗记录ID删除医疗记录信息
     *
     * @param id 医疗记录ID
     * @return 结果
     */
    public int deleteMedicalRecordById(Long id);

    /**
     * 批量删除医疗记录信息
     *
     * @param ids 需要删除的医疗记录ID
     * @return 结果
     */
    public int deleteMedicalRecordByIds(Long[] ids);

    /**
     * 新增医疗记录信息
     *
     * @param medicalRecord 医疗记录信息
     * @return 结果
     */
    public int insertMedicalRecord(MedicalRecord medicalRecord);

    /**
     * 修改医疗记录信息
     *
     * @param medicalRecord 医疗记录信息
     * @return 结果
     */
    public int updateMedicalRecord(MedicalRecord medicalRecord);
=======
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
>>>>>>> ceece8c (实现多选删除功能)
}