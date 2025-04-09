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
}