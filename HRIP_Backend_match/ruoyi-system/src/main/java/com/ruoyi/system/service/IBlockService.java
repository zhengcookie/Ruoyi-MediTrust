package com.ruoyi.system.service;

import com.alibaba.fastjson2.JSONObject;
<<<<<<< HEAD
import com.ruoyi.system.domain.Appointment;
import com.ruoyi.system.domain.Info;
import com.ruoyi.system.domain.MedicalRecord;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;

import java.math.BigInteger;
import java.util.Collections;
=======
import com.ruoyi.system.domain.Appointments;
import com.ruoyi.system.domain.Info;
import com.ruoyi.system.domain.MedicalRecord;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.springframework.http.ResponseEntity;

import java.math.BigInteger;
>>>>>>> ceece8c (实现多选删除功能)
import java.util.List;
import java.util.Map;

public interface IBlockService {
<<<<<<< HEAD
    /**
     * 用户登录
     */
    JSONObject login(String address, BigInteger type, String password) throws ContractException;
=======

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
>>>>>>> ceece8c (实现多选删除功能)

    /**
     * 获取用户信息
     */
<<<<<<< HEAD
    JSONObject getInfo(String address) throws ContractException;
=======
    JSONObject getInfo(BigInteger userId) throws ContractException;
    JSONObject updateInfo(Info info);
>>>>>>> ceece8c (实现多选删除功能)

    /**
     * 创建用户信息
     */
    JSONObject register(Info info);

    /**
<<<<<<< HEAD
     * 添加挂号信息
     */
    JSONObject createAppointment(Appointment appointment);
=======
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
>>>>>>> ceece8c (实现多选删除功能)

    /**
     * 更新挂号预约信息
     */
<<<<<<< HEAD
    JSONObject updateAppointment(Appointment appointment);
=======
    JSONObject updateAppointment(Appointments appointment);
>>>>>>> ceece8c (实现多选删除功能)

    /**
     * 完成挂号预约信息
     */
    JSONObject completeAppointment(BigInteger appointmentId);

    /**
<<<<<<< HEAD
=======
     * 检查预约是否完成
     */
    JSONObject isCompletedAppointment(BigInteger appointmentId);

    /**
>>>>>>> ceece8c (实现多选删除功能)
     * 删除预约信息
     */
    JSONObject deleteAppointment(BigInteger appointmentId);

    /**
     * 获取挂号预约信息
     */
<<<<<<< HEAD
    JSONObject getSickAppointment(BigInteger appointmentId,String address);
=======
    JSONObject getSickAppointment(BigInteger appointmentId, BigInteger userId);
>>>>>>> ceece8c (实现多选删除功能)

    /**
     * 获取用户所有挂号预约信息
     */
<<<<<<< HEAD
    List<Object> getAppointmentsList(String address);
=======
    List<Object> getAppointmentsList(BigInteger userId);
>>>>>>> ceece8c (实现多选删除功能)

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
<<<<<<< HEAD
=======
     * 检查病历是否填写完成
     */
    JSONObject isCompleteMedicalRecord(BigInteger recordId);

    /**
>>>>>>> ceece8c (实现多选删除功能)
     * 删除病历信息
     */
    JSONObject deleteMedicalRecord(BigInteger recordId);

    /**
     * 获取用户所有病历信息
     */
<<<<<<< HEAD
    List<JSONObject> getMedicalRecordList(String address);
=======
    List<JSONObject> getMedicalRecordList(BigInteger userId);
>>>>>>> ceece8c (实现多选删除功能)

    /**
     * 获取病历信息
     */
<<<<<<< HEAD
    JSONObject getMedicalRecordByIdentityNumber(BigInteger recordId,String address);
=======
    JSONObject getMedicalRecordByIdentityNumber(BigInteger recordId, BigInteger userId);
>>>>>>> ceece8c (实现多选删除功能)

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
<<<<<<< HEAD
    Map<String, Object> getAll(String address) throws ContractException;
=======
    Map<String, Object> getAll(BigInteger userId) throws ContractException;

    ResponseEntity<Map<String, Object>> getImage(String imagePath);
>>>>>>> ceece8c (实现多选删除功能)
} 