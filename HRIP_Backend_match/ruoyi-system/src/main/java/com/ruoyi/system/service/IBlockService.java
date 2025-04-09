package com.ruoyi.system.service;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.system.domain.Appointment;
import com.ruoyi.system.domain.Info;
import com.ruoyi.system.domain.MedicalRecord;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface IBlockService {
    /**
     * 用户登录
     */
    JSONObject login(String address, BigInteger type, String password) throws ContractException;

    /**
     * 获取用户信息
     */
    JSONObject getInfo(String address) throws ContractException;

    /**
     * 创建用户信息
     */
    JSONObject register(Info info);

    /**
     * 添加挂号信息
     */
    JSONObject createAppointment(Appointment appointment);

    /**
     * 更新挂号预约信息
     */
    JSONObject updateAppointment(Appointment appointment);

    /**
     * 完成挂号预约信息
     */
    JSONObject completeAppointment(BigInteger appointmentId);

    /**
     * 删除预约信息
     */
    JSONObject deleteAppointment(BigInteger appointmentId);

    /**
     * 获取挂号预约信息
     */
    JSONObject getSickAppointment(BigInteger appointmentId,String address);

    /**
     * 获取用户所有挂号预约信息
     */
    List<Object> getAppointmentsList(String address);

    /**
     * 创建病历
     */
    JSONObject createMedicalRecord(MedicalRecord medicalRecord);

    /**
     * 更新病历信息
     */
    JSONObject updateMedicalRecord(MedicalRecord medicalRecord);

    /**
     * 完成病历信息
     */
    JSONObject completeMedicalRecord(BigInteger recordId);

    /**
     * 删除病历信息
     */
    JSONObject deleteMedicalRecord(BigInteger recordId);

    /**
     * 获取用户所有病历信息
     */
    List<JSONObject> getMedicalRecordList(String address);

    /**
     * 获取病历信息
     */
    JSONObject getMedicalRecordByIdentityNumber(BigInteger recordId,String address);

    /**
     * 查询所有挂号预约信息
     */
    List<JSONObject> getPatientsList();

    /**
     * 获取区块链信息
     */
    Map<String, Object> getBlockchainInfo();

    /**
     * 获取用户所有信息统计
     */
    Map<String, Object> getAll(String address) throws ContractException;
} 