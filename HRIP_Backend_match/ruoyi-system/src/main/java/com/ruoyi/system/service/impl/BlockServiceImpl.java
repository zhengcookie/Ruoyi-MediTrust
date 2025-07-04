package com.ruoyi.system.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.system.contract.MedicalPlatform;
import com.ruoyi.system.domain.Appointments;
import com.ruoyi.system.domain.Info;
import com.ruoyi.system.domain.MedicalRecord;
import com.ruoyi.system.mapper.AppointmentsMapper;
import com.ruoyi.system.mapper.InfoMapper;
import com.ruoyi.system.mapper.MedicalRecordMapper;

import com.ruoyi.system.service.IBlockService;

import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.*;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.response.BcosBlock;
import org.fisco.bcos.sdk.client.protocol.response.TotalTransactionCount;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.*;

@Service
public class BlockServiceImpl implements IBlockService {

    @Resource
    private AppointmentsMapper appointmentsMapper;
    @Resource
    private InfoMapper infoMapper;
    @Resource
    private MedicalRecordMapper medicalRecordMapper;

    @Autowired
    private Client client;
    @Autowired
    private CryptoKeyPair cryptoKeyPair;
    @Value("${fisco.contractAddress.asset}")
    private String contractAddress;
    @Override
    public JSONObject register(Info info) {
        JSONObject outPut = new JSONObject();
        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            // Convert UserType from String to BigInteger
            TransactionReceipt receipt = medicalPlatform.register(
                    info.getName(),
                    info.getGender(),
                    info.getAge(),
                    info.getUserType(),
                    info.getEmail());
            infoMapper.insertInfo(info);
            outPut.put("code", 200);
            outPut.put("msg", "用户创建成功!");
            return outPut;
        } catch (Exception e) {
            e.printStackTrace();
            outPut.put("code", 400);
            outPut.put("msg", "用户创建失败!");
            return outPut;
        }
    }

    @Override
    public List<JSONObject> getAllDoctorsName() {
        List<JSONObject> doctorsList = new ArrayList<>();
        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            Tuple2<List<BigInteger>, List<String>> doctorsNameList = medicalPlatform.getAllDoctorsName();
            if (doctorsNameList == null){
                return doctorsList;
            }
            for (int i = 0; i < doctorsNameList.getValue1().size(); i++) {
                JSONObject doctor = new JSONObject();
                doctor.put("doctorId", doctorsNameList.getValue1().get(i));
                doctor.put("doctorName", doctorsNameList.getValue2().get(i));
                doctorsList.add(doctor);
            }
            return doctorsList;
        } catch (ContractException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<JSONObject> getAllPatientsName() {
        List<JSONObject> patientsList = new ArrayList<>();
        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            Tuple2<List<BigInteger>, List<String>> patientsNameList = medicalPlatform.getAllPatientssName();
            if (patientsNameList == null){
                return patientsList;
            }
            for (int i = 0; i < patientsNameList.getValue1().size(); i++) {
                JSONObject patient = new JSONObject();
                patient.put("patientId", patientsNameList.getValue1().get(i));
                patient.put("patientName", patientsNameList.getValue2().get(i));
                patientsList.add(patient);
            }
            return patientsList;

        } catch (ContractException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public JSONObject sendCode(String email) {
        JSONObject outPut = new JSONObject();
        try {
            Random random = new Random();
            String verificationCode = String.format("%06d", random.nextInt(999999));
            outPut.put("code", 200);
            outPut.put("msg", "验证码发送成功!");
            outPut.put("verificationCode", verificationCode);
        }catch (Exception ex){
            outPut.put("code", 400);
            outPut.put("msg", "验证码发送失败!");
        }
        return outPut;

    }



    @Override
    public JSONObject login(String userName, BigInteger userType, String password) throws ContractException {
        JSONObject outPut = new JSONObject();
        if (StringUtils.isNotBlank(String.valueOf(userType)) && StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);

            // 通过用户名获取用户ID
            BigInteger userId = medicalPlatform.getUserId(userName);
            Tuple7<BigInteger, String, String, BigInteger, BigInteger, String,String> accountInfo = medicalPlatform.users(userId);
            Info info = infoMapper.selectInfoById(userId.longValue());
            if (info.getName().equals(userName) && info.getPassword().equals(password) && userType.equals(accountInfo.getValue5())) {
                outPut.put("code", 200);
                outPut.put("msg", "用户登录成功");
                outPut.put("id", accountInfo.getValue1());
                outPut.put("name", accountInfo.getValue2());
                outPut.put("gender", accountInfo.getValue3());
                outPut.put("age", accountInfo.getValue4());
                outPut.put("userType", accountInfo.getValue5());
                outPut.put("address", accountInfo.getValue6());
                outPut.put("email", accountInfo.getValue7());
                return outPut;
            } else {
                outPut.put("code", 401);
                outPut.put("msg", "用户名或密码错误");
            }
        }
        return outPut;
    }

    @Override
    public JSONObject forgetPassword(String userName, BigInteger userType, String password) throws ContractException {
        JSONObject outPut = new JSONObject();
        if (StringUtils.isNotBlank(String.valueOf(userType)) && StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            BigInteger userId = medicalPlatform.getUserId(userName);
            Info info = infoMapper.selectInfoById(userId.longValue());
            info.setPassword(password);
            infoMapper.updateInfo(info);
            outPut.put("code", 200);
            outPut.put("msg", "密码重置成功");
            return outPut;
        }else{
            outPut.put("code",400);
            outPut.put("msg","密码重置失败");
            return outPut;
        }
    }


    @Override
    public JSONObject getInfo(BigInteger userId) throws ContractException {
        JSONObject outPut = new JSONObject();
        if (StringUtils.isNotBlank(userId.toString())) {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            // 通过用户名获取用户ID
            Tuple7<BigInteger, String, String, BigInteger, BigInteger, String, String> accountInfo = medicalPlatform.users(userId);
            outPut.put("id", userId.longValue());
            outPut.put("name", accountInfo.getValue2());
            outPut.put("gender", accountInfo.getValue3());
            outPut.put("age", accountInfo.getValue4());
            outPut.put("userType", accountInfo.getValue5());
            outPut.put("address", accountInfo.getValue6());
            outPut.put("email", accountInfo.getValue7());
            Info info = infoMapper.selectInfoById(userId.longValue());
            outPut.put("avatar", info.getAvatar());
            outPut.put("code", 200);
            outPut.put("msg", "获取数据成功!");
        }
        return outPut;
    }
    /**
     * 更新用户信息
     * @param info
     * @return
     */

    @Override
    public JSONObject updateInfo(Info info) {
        JSONObject outPut = new JSONObject();
        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            // Convert UserType from String to BigInteger
            TransactionReceipt receipt = medicalPlatform.updateUser(BigInteger.valueOf(info.getId()), info.getName(), info.getGender(), info.getAge(),info.getEmail());
            infoMapper.updateInfo(info);
            outPut.put("code", 200);
            outPut.put("msg", "用户信息更新成功!");
            return outPut;
        }catch (Exception e) {
            e.printStackTrace();
            outPut.put("code", 400);
            outPut.put("msg", "用户信息更新失败!");
            return outPut;
        }
    }

    /***
     *
     * @param appointment
     * @return
     */


    @Override
    public JSONObject createAppointment(Appointments appointment) {
        JSONObject outPut = new JSONObject();
        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            BigInteger createdAt = BigInteger.valueOf(appointment.getCreatedAt().getTime());
            
            // Use the correct method signature
            TransactionReceipt receipt = medicalPlatform.createAppointment(
                appointment.getPatientName(),
                appointment.getDoctorName(),
                appointment.getHospitalName(),
                appointment.getDepartment(),
                appointment.getRemark(),
                createdAt);
            System.out.println("receipt"+receipt);
            if (receipt.isStatusOK()){
                appointmentsMapper.insertAppointment(appointment);
                outPut.put("msg", "挂号预约成功，请前往挂号预约的科室就诊");
                outPut.put("code", 200);
            }else{
                outPut.put("msg", "挂号预约失败");
                outPut.put("code", 400);
            }
            return outPut;
        } catch (Exception e) {
            e.printStackTrace();
            outPut.put("msg", "挂号预约失败");
            return outPut;
        }
    }

    @Override
    public JSONObject updateAppointment(Appointments appointment) {
        JSONObject outPut = new JSONObject();

        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            BigInteger createdAt = BigInteger.valueOf(appointment.getCreatedAt().getTime());
            BigInteger appointmentId = new BigInteger(appointment.getId().toString());
            
            // Use the correct method signature based on contract
            TransactionReceipt receipt = medicalPlatform.updateAppointment(
                appointmentId,
                appointment.getHospitalName(),
                appointment.getDepartment(),
                appointment.getRemark(),
                createdAt);
            
            if (receipt.isStatusOK()){
                appointmentsMapper.updateAppointment(appointment);
                outPut.put("code", 200);
                outPut.put("msg", "更新成功");
            }else{
                outPut.put("code", 400);
                outPut.put("msg", "更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            outPut.put("code", 400);
            outPut.put("msg", "更新失败: " + e.getMessage());
        }
        return outPut;
    }

    @Override
    public JSONObject completeAppointment(BigInteger appointmentId) {
        JSONObject outPut = new JSONObject();
        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            medicalPlatform.completeAppointment(appointmentId);
            outPut.put("code", 200);
            outPut.put("msg", "完成挂号预约成功");
        } catch (Exception e) {
            outPut.put("code", 400);
            outPut.put("msg", "挂号预约已完成,无需再次填写");
            e.printStackTrace();
        }
        return outPut;
    }

    @Override
    public JSONObject deleteAppointment(BigInteger appointmentId) {
        JSONObject outPut = new JSONObject();
        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            medicalPlatform.deleteAppointment(appointmentId);
            appointmentsMapper.deleteAppointmentById(appointmentId.longValue());
            outPut.put("code", 200);
            outPut.put("msg", "删除挂号预约成功");
        } catch (Exception e) {
            outPut.put("code", 400);
            outPut.put("msg", "删除失败");
        }
        return outPut;
    }

    @Override
    public JSONObject getSickAppointment(BigInteger appointmentId, BigInteger userId) {
        JSONObject outPut = new JSONObject();
        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            
            /*
             * The Java interface for getSickAppointment returns a Tuple10 with the following types:
             * Tuple10<BigInteger, BigInteger, String, BigInteger, String, String, String, String, BigInteger, Boolean>
             */
            Tuple10<BigInteger, BigInteger, String, BigInteger, String, String, String, String, BigInteger, Boolean> appointment = 
                medicalPlatform.getSickAppointment(appointmentId, userId);
            if (appointment == null) {
                outPut.put("code", 400);
                outPut.put("msg", "获取挂号预约失败");
                return outPut;
            }else{
                outPut.put("code", 200);
                outPut.put("appointmentId", appointmentId);
                // Map the fields correctly
                outPut.put("patientId", appointment.getValue2());
                outPut.put("patientName", appointment.getValue3());
                outPut.put("doctorId", appointment.getValue4());
                outPut.put("doctorName", appointment.getValue5());
                outPut.put("hospitalName", appointment.getValue6());
                outPut.put("department", appointment.getValue7());
                outPut.put("remark", appointment.getValue8());
                outPut.put("createTime", appointment.getValue9());
                outPut.put("isCompleted", appointment.getValue10());
                return outPut;
            }
        } catch (Exception e) {
            outPut.put("code", 400);
            outPut.put("msg", e.getMessage());
            return outPut;
        }
    }

    @Override
    public List<Object> getAppointmentsList(BigInteger userId) {
        List<Object> outPut = new ArrayList<>();
        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            List<BigInteger> appointmentIds = medicalPlatform.getAppointmentsList(userId);
            for (BigInteger appointmentId : appointmentIds) {
                JSONObject appointments = getSickAppointment(appointmentId, userId);
                outPut.add(appointments);
            }
        } catch (Exception e) {
            outPut.add(new JSONObject().put("msg", "查询失败"));
            return outPut;
        }
        return outPut;
    }

    @Override
    public JSONObject isCompleteMedicalRecord(BigInteger recordId) {
        JSONObject outPut = new JSONObject();
        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            boolean isComplete = medicalPlatform.iscompleteMedicalRecord(recordId);
            if (isComplete){
                outPut.put("code", 200);
                outPut.put("data", isComplete);
            } else {
                outPut.put("code", 400);
                outPut.put("data", isComplete);
            }
        } catch (Exception e) {
            outPut.put("code", 400);
            outPut.put("msg", e.getMessage());
        }
        return outPut;
    }

    @Override
    public List<JSONObject> getMedicalRecordList(BigInteger userId) {
        List<JSONObject> outPut = new ArrayList<>();
        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            List<BigInteger> medicalPlatformId = medicalPlatform.getMedicalRecordList(userId);
            for (BigInteger medicalRecordId : medicalPlatformId) {
                JSONObject medicalRecord = getMedicalRecordByIdentityNumber(medicalRecordId, userId);
                outPut.add(medicalRecord);
            }
            return outPut;
        } catch (Exception ex) {
            ex.printStackTrace();
            return outPut;
        }
    }

    @Override
    public JSONObject getMedicalRecordByIdentityNumber(BigInteger recordId, BigInteger userId) {
        JSONObject outPut = new JSONObject();
        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            /* 
             * The Java interface for getMedicalRecord returns a Tuple10 with the following types:
             * Tuple10<BigInteger, BigInteger, BigInteger, String, String, String, String, String, BigInteger, Boolean>
             */
            Tuple10<BigInteger, String, String, String, String, String, String, String, BigInteger, Boolean> medicalRecord =
                medicalPlatform.getMedicalRecord(recordId, userId);
            if (medicalRecord == null){
                outPut.put("msg", "病历查询失败");
                outPut.put("code", 400);
                return outPut;
            }else{
                outPut.put("code", 200);
                outPut.put("msg", "病历查询成功!");
                outPut.put("recordId", recordId);
                // Map the fields correctly
                outPut.put("patientName", medicalRecord.getValue2());
                outPut.put("doctorName", medicalRecord.getValue3());
                outPut.put("hospitalName", medicalRecord.getValue4());
                outPut.put("department", medicalRecord.getValue5());
                outPut.put("registrationInfo", medicalRecord.getValue6());
                outPut.put("pastMedicalHistory", medicalRecord.getValue7());
                outPut.put("currentMedicalHistory", medicalRecord.getValue8());
                outPut.put("createTime", medicalRecord.getValue9());
                outPut.put("isFilled", medicalRecord.getValue10());
                return outPut;
            }
        } catch (Exception e) {
            e.printStackTrace();
            outPut.put("msg", recordId + "病历查询失败");
            return outPut;
        }
    }

    @Override
    public Map<String, Object> getAll(BigInteger userId) throws ContractException {
        Map<String, Object> response = new HashMap<>();
        MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
        System.out.println(medicalPlatform.getAllPatients());

//        List patientsList = patients.getValue1();

        List appointmentsList = medicalPlatform.getAppointmentsList(userId);
        List medicalRecordList = medicalPlatform.getMedicalRecordList(userId);
        if (appointmentsList == null || medicalRecordList == null) {
            response.put("code", 400);
            response.put("msg", "查询失败");
            return response;
        }else {
            response.put("appointment", appointmentsList.size());
            response.put("medicalRecord", medicalRecordList.size());
            return response;
        }
//        response.put("patient", patientsList.size());
    }

    @Override
    public ResponseEntity<Map<String, Object>> getImage(String imagePath) {
        return null;
    }

    @Override
    public List<JSONObject> getPatientsList() {
        return Collections.emptyList();
    }

    @Override
    public Map<String, Object> getBlockchainInfo() {
        BigInteger latestBlockNumber = client.getBlockNumber().getBlockNumber();
        BcosBlock block = client.getBlockByNumber(latestBlockNumber, true);
        List<BcosBlock.TransactionResult> transactions = block.getBlock().getTransactions();
        String latestTransactionHash = null;
        if (transactions != null && transactions.size() > 0) {
            BcosBlock.TransactionResult transactionResult = transactions.get(transactions.size() - 1);
            if (transactionResult instanceof BcosBlock.TransactionObject) {
                BcosBlock.TransactionObject transactionObject = (BcosBlock.TransactionObject) transactionResult;
                latestTransactionHash = transactionObject.getHash();
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("blockNumber", latestBlockNumber);
        TotalTransactionCount totalTransactionCount = client.getTotalTransactionCount();
        String hexString = totalTransactionCount.getTotalTransactionCount().getTxSum();
        hexString = hexString.startsWith("0x") ? hexString.substring(2) : hexString;
        int txSum = Integer.parseInt(hexString, 16);
        response.put("transactionCount", txSum);

        return response;
    }

    @Override
    public JSONObject isCompletedAppointment(BigInteger appointmentId) {
        JSONObject outPut = new JSONObject();
        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            boolean isCompleted = medicalPlatform.isCompletedApoointment(appointmentId);
            outPut.put("code", 200);
            outPut.put("data", isCompleted);
        } catch (Exception e) {
            outPut.put("code", 400);
            outPut.put("msg", e.getMessage());
        }
        return outPut;
    }

    @Override
    public JSONObject createMedicalRecord(MedicalRecord medicalRecord) {
        JSONObject outPut = new JSONObject();
        try {
            BigInteger createdAt = BigInteger.valueOf(medicalRecord.getCreatedAt().getTime());
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            
            // Use the correct method signature
            TransactionReceipt receipt = medicalPlatform.createMedicalRecord(
                medicalRecord.getPatientName(),
                medicalRecord.getDoctorName(),
                medicalRecord.getHospitalName(),
                medicalRecord.getDepartment(),
                medicalRecord.getRegistrationInfo(),
                medicalRecord.getPastMedicalHistory(),
                medicalRecord.getCurrentMedicalHistory(),
                createdAt);
            
            if (receipt.isStatusOK()){
                medicalRecordMapper.insertMedicalRecord(medicalRecord);
                outPut.put("msg", "病历添加成功");
                outPut.put("code", 200);
            }else{
                outPut.put("msg", "病历添加失败");
                outPut.put("code", 400);
            }
            return outPut;
        } catch (Exception e) {
            e.printStackTrace();
            outPut.put("msg", "病历添加失败");
            return outPut;
        }
    }

    @Override
    public JSONObject updateMedicalRecord(MedicalRecord medicalRecord) {
        JSONObject outPut = new JSONObject();
        try {
            BigInteger recordId = BigInteger.valueOf(medicalRecord.getId());
            BigInteger createdAt = BigInteger.valueOf(medicalRecord.getCreatedAt().getTime());
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            
            // Use the correct method signature
            TransactionReceipt receipt = medicalPlatform.updateMedicalRecord(
                recordId,
                medicalRecord.getHospitalName(),
                medicalRecord.getDepartment(),
                medicalRecord.getRegistrationInfo(),
                medicalRecord.getPastMedicalHistory(),
                medicalRecord.getCurrentMedicalHistory(),
                createdAt);
            
            if (receipt.isStatusOK()){
                medicalRecordMapper.updateMedicalRecord(medicalRecord);
                outPut.put("msg", "病历信息更新成功");
                outPut.put("code", 200);
            }else{
                outPut.put("msg", "病历信息更新失败");
                outPut.put("code", 400);
            }
        } catch (Exception e) {
            outPut.put("code", 400);
            outPut.put("msg", "病历信息更新失败");
            throw new RuntimeException(e);
        }
        return outPut;
    }

    @Override
    public JSONObject completeMedicalRecord(BigInteger recordId) {
        JSONObject outPut = new JSONObject();
        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            medicalPlatform.completeMedicalRecord(recordId);
            outPut.put("msg", "病历信息已完成");
            outPut.put("code", 200);
        } catch (Exception e) {
            outPut.put("msg", "病历信息填写完毕,无需再次修改！");
            outPut.put("code", 400);
            throw new RuntimeException(e);
        }
        return outPut;
    }

    @Override
    public JSONObject deleteMedicalRecord(BigInteger recordId) {
        JSONObject outPut = new JSONObject();
        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            medicalPlatform.deleteMedicalRecord(recordId);
            medicalRecordMapper.deleteMedicalRecordById(recordId.intValue());
            outPut.put("msg", "病历信息删除成功");
            outPut.put("code", 200);
        } catch (Exception e) {
            outPut.put("msg", "病历信息删除失败");
            outPut.put("code", 400);
        }
        return outPut;
    }
} 