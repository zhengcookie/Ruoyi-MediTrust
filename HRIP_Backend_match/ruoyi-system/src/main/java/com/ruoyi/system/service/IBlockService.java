package com.ruoyi.system.service;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.system.domain.Appointments;
import com.ruoyi.system.domain.Info;
import com.ruoyi.system.domain.MedicalRecord;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.springframework.http.ResponseEntity;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface IBlockService {

    /**
     * 注册验证码
     */
    JSONObject sendCode(String email);

    /**
     * 用户登录
     */
    JSONObject login(String userName, BigInteger userType, String password) throws ContractException;

    /**
     * 忘记密码
     */
    JSONObject forgetPassword(String userName, BigInteger userType, String password) throws ContractException;

    /**
     * 获取用户信息
     */
    JSONObject getInfo(BigInteger userId) throws ContractException;
    JSONObject updateInfo(Info info);

    /**
     * 创建用户信息
     */
    JSONObject register(Info info);

    /**
     * 获取所有医生姓名信息
     */
    List<JSONObject> getAllDoctorsName();

    /**
     * 获取所有患者姓名信息
     */
    List<JSONObject> getAllPatientsName();


    /**
     * 添加挂号信息
     */
    JSONObject createAppointment(Appointments appointment);

    /**
     * 更新挂号预约信息
     */
    JSONObject updateAppointment(Appointments appointment);

    /**
     * 完成挂号预约信息
     */
    JSONObject completeAppointment(BigInteger appointmentId);

    /**
     * 检查预约是否完成
     */
    JSONObject isCompletedAppointment(BigInteger appointmentId);

    /**
     * 删除预约信息
     */
    JSONObject deleteAppointment(BigInteger appointmentId);

    /**
     * 获取挂号预约信息
     */
    JSONObject getSickAppointment(BigInteger appointmentId, BigInteger userId);

    /**
     * 获取用户所有挂号预约信息
     */
    List<Object> getAppointmentsList(BigInteger userId);

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
     * 检查病历是否填写完成
     */
    JSONObject isCompleteMedicalRecord(BigInteger recordId);

    /**
     * 删除病历信息
     */
    JSONObject deleteMedicalRecord(BigInteger recordId);

    /**
     * 获取用户所有病历信息
     */
    List<JSONObject> getMedicalRecordList(BigInteger userId);

    /**
     * 获取病历信息
     */
    JSONObject getMedicalRecordByIdentityNumber(BigInteger recordId, BigInteger userId);

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
    Map<String, Object> getAll(BigInteger userId) throws ContractException;

    ResponseEntity<Map<String, Object>> getImage(String imagePath);
} 