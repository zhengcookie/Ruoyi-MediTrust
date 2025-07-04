package com.ruoyi.system.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.system.contract.MedicalPlatform;
<<<<<<< HEAD
import com.ruoyi.system.domain.Appointment;
=======
import com.ruoyi.system.domain.Appointments;
>>>>>>> ceece8c (实现多选删除功能)
import com.ruoyi.system.domain.Info;
import com.ruoyi.system.domain.MedicalRecord;
import com.ruoyi.system.mapper.AppointmentsMapper;
import com.ruoyi.system.mapper.InfoMapper;
import com.ruoyi.system.mapper.MedicalRecordMapper;
<<<<<<< HEAD
=======

>>>>>>> ceece8c (实现多选删除功能)
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
<<<<<<< HEAD
=======
import org.springframework.http.ResponseEntity;
>>>>>>> ceece8c (实现多选删除功能)
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;
<<<<<<< HEAD
import java.sql.Timestamp;
=======
>>>>>>> ceece8c (实现多选删除功能)
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
<<<<<<< HEAD

    @Override
    public JSONObject login(String address, BigInteger type, String password) throws ContractException {
        JSONObject outPut = new JSONObject();
        if (StringUtils.isNotBlank(String.valueOf(type)) && StringUtils.isNotBlank(address) && StringUtils.isNotBlank(password)) {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);

            Tuple5<String, String, BigInteger, BigInteger, String> AccountAddress = medicalPlatform.users(address);
            List<Info> list = infoMapper.selectUserAll();
            if (!AccountAddress.getValue4().equals(type)) {
                outPut.put("code", 401);
                outPut.put("msg", "用户类型不正确");
                return outPut;
            }
            for (Info info : list) {
                if (info.getAddress().equals(address) && info.getPassword().equals(password)) {
                    outPut.put("code", 200);
                    outPut.put("msg", "用户登录成功");
                    outPut.put("accountAddress", address);
                    outPut.put("name", AccountAddress.getValue1());
                    outPut.put("gender", AccountAddress.getValue2());
                    outPut.put("age", AccountAddress.getValue3());
                    outPut.put("UserType", AccountAddress.getValue4());
                    return outPut;
                } else {
                    outPut.put("code", 401);
                    outPut.put("msg", "地址或密码错误");
                }
            }
        }
        return outPut;
    }

    @Override
    public JSONObject getInfo(String address) throws ContractException {
        JSONObject outPut = new JSONObject();
        if (StringUtils.isNotBlank(address)) {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            Tuple5<String, String, BigInteger, BigInteger, String> AccountAddress = medicalPlatform.users(address);
            outPut.put("accountAddress", address);
            outPut.put("name", AccountAddress.getValue1());
            outPut.put("gender", AccountAddress.getValue2());
            outPut.put("age", AccountAddress.getValue3());
        }
        return outPut;
    }

=======
>>>>>>> ceece8c (实现多选删除功能)
    @Override
    public JSONObject register(Info info) {
        JSONObject outPut = new JSONObject();
        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
<<<<<<< HEAD
            TransactionReceipt receipt = medicalPlatform.register(info.getName(), info.getGender(), info.getAge(), info.getUserType(), info.getAddress());
            infoMapper.insertUser(info);
=======
            // Convert UserType from String to BigInteger
            TransactionReceipt receipt = medicalPlatform.register(
                    info.getName(),
                    info.getGender(),
                    info.getAge(),
                    info.getUserType(),
                    info.getEmail());
            infoMapper.insertInfo(info);
>>>>>>> ceece8c (实现多选删除功能)
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
<<<<<<< HEAD
    public JSONObject createAppointment(Appointment appointment) {
=======
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
>>>>>>> ceece8c (实现多选删除功能)
        JSONObject outPut = new JSONObject();
        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            BigInteger createdAt = BigInteger.valueOf(appointment.getCreatedAt().getTime());
<<<<<<< HEAD
//            BigInteger CreatedAt = new BigInteger(appointment.getCreatedAt().toString());
            TransactionReceipt receipt = medicalPlatform.createAppointment(appointment.getPatientName(), appointment.getPatient(), appointment.getDoctorName(), appointment.getDoctor(), appointment.getHospitalName(), appointment.getDepartment(), appointment.getRemark(),createdAt);
=======
            
            // Use the correct method signature
            TransactionReceipt receipt = medicalPlatform.createAppointment(
                appointment.getPatientName(),
                appointment.getDoctorName(),
                appointment.getHospitalName(),
                appointment.getDepartment(),
                appointment.getRemark(),
                createdAt);
>>>>>>> ceece8c (实现多选删除功能)
            System.out.println("receipt"+receipt);
            if (receipt.isStatusOK()){
                appointmentsMapper.insertAppointment(appointment);
                outPut.put("msg", "挂号预约成功，请前往挂号预约的科室就诊");
                outPut.put("code", 200);
<<<<<<< HEAD

=======
>>>>>>> ceece8c (实现多选删除功能)
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
<<<<<<< HEAD
    public JSONObject updateAppointment(Appointment appointment) {
=======
    public JSONObject updateAppointment(Appointments appointment) {
>>>>>>> ceece8c (实现多选删除功能)
        JSONObject outPut = new JSONObject();

        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            BigInteger createdAt = BigInteger.valueOf(appointment.getCreatedAt().getTime());
            BigInteger appointmentId = new BigInteger(appointment.getId().toString());
<<<<<<< HEAD
            TransactionReceipt receipt = medicalPlatform.updateAppointment(appointmentId, appointment.getPatientName(), appointment.getPatient(), appointment.getDoctorName(), appointment.getDoctor(), appointment.getHospitalName(), appointment.getDepartment(), appointment.getRemark(),createdAt);
=======
            
            // Use the correct method signature based on contract
            TransactionReceipt receipt = medicalPlatform.updateAppointment(
                appointmentId,
                appointment.getHospitalName(),
                appointment.getDepartment(),
                appointment.getRemark(),
                createdAt);
            
>>>>>>> ceece8c (实现多选删除功能)
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
<<<<<<< HEAD
    public JSONObject getSickAppointment(BigInteger appointmentId,String address) {
        JSONObject outPut = new JSONObject();
        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            Tuple9<String, String, String, String, String, String, String, BigInteger, Boolean> appointment = medicalPlatform.getSickAppointment(appointmentId,address);
            outPut.put("code", 200);
            outPut.put("appointmentId", appointmentId);
            outPut.put("patientName", appointment.getValue1());
            outPut.put("patient", appointment.getValue2());
            outPut.put("doctorName", appointment.getValue3());
            outPut.put("doctor", appointment.getValue4());
            outPut.put("hospitalName", appointment.getValue5());
            outPut.put("department", appointment.getValue6());
            outPut.put("remark", appointment.getValue7());
            outPut.put("time", appointment.getValue8());
            outPut.put("status", appointment.getValue9());
=======
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
>>>>>>> ceece8c (实现多选删除功能)
        } catch (Exception e) {
            outPut.put("code", 400);
            outPut.put("msg", e.getMessage());
            return outPut;
        }
<<<<<<< HEAD
        return outPut;
    }

    @Override
    public List<Object> getAppointmentsList(String address) {
        List<Object> outPut = new ArrayList<>();
        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            List<BigInteger> appointmentIds = medicalPlatform.getAppointmentsList(address);
            for (BigInteger appointmentId : appointmentIds) {
                JSONObject appointments = getSickAppointment(appointmentId, address);
=======
    }

    @Override
    public List<Object> getAppointmentsList(BigInteger userId) {
        List<Object> outPut = new ArrayList<>();
        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            List<BigInteger> appointmentIds = medicalPlatform.getAppointmentsList(userId);
            for (BigInteger appointmentId : appointmentIds) {
                JSONObject appointments = getSickAppointment(appointmentId, userId);
>>>>>>> ceece8c (实现多选删除功能)
                outPut.add(appointments);
            }
        } catch (Exception e) {
            outPut.add(new JSONObject().put("msg", "查询失败"));
            return outPut;
        }
        return outPut;
    }

    @Override
<<<<<<< HEAD
    public JSONObject createMedicalRecord(MedicalRecord medicalRecord) {
        JSONObject outPut = new JSONObject();
        try {

            BigInteger createdAt = BigInteger.valueOf(medicalRecord.getCreatedAt().getTime());
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            TransactionReceipt receipt = medicalPlatform.createMedicalRecord(medicalRecord.getPatientName(), medicalRecord.getPatient(), medicalRecord.getDoctorName(), medicalRecord.getDoctor(), medicalRecord.getHospitalName(), medicalRecord.getDepartment(), medicalRecord.getRegistrationInfo(), medicalRecord.getPastMedicalHistory(), medicalRecord.getCurrentMedicalHistory(),createdAt);
=======
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
            
>>>>>>> ceece8c (实现多选删除功能)
            if (receipt.isStatusOK()){
                medicalRecordMapper.insertMedicalRecord(medicalRecord);
                outPut.put("msg", "病历添加成功");
                outPut.put("code", 200);
<<<<<<< HEAD
//                outPut.put("data", receipt);
=======
>>>>>>> ceece8c (实现多选删除功能)
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
<<<<<<< HEAD
            TransactionReceipt receipt = medicalPlatform.updateMedicalRecord(recordId,medicalRecord.getPatientName(), medicalRecord.getPatient(), medicalRecord.getDoctorName(), medicalRecord.getDoctor(), medicalRecord.getHospitalName(), medicalRecord.getDepartment(), medicalRecord.getRegistrationInfo(), medicalRecord.getPastMedicalHistory(), medicalRecord.getCurrentMedicalHistory(),createdAt );
=======
            
            // Use the correct method signature
            TransactionReceipt receipt = medicalPlatform.updateMedicalRecord(
                recordId,
                medicalRecord.getHospitalName(),
                medicalRecord.getDepartment(),
                medicalRecord.getRegistrationInfo(),
                medicalRecord.getPastMedicalHistory(),
                medicalRecord.getCurrentMedicalHistory(),
                createdAt);
            
>>>>>>> ceece8c (实现多选删除功能)
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
<<<<<<< HEAD
            medicalRecordMapper.deleteMedicalRecordById(recordId.longValue());
=======
            medicalRecordMapper.deleteMedicalRecordById(recordId.intValue());
>>>>>>> ceece8c (实现多选删除功能)
            outPut.put("msg", "病历信息删除成功");
            outPut.put("code", 200);
        } catch (Exception e) {
            outPut.put("msg", "病历信息删除失败");
            outPut.put("code", 400);
        }
        return outPut;
    }
<<<<<<< HEAD

    @Override
    public List<JSONObject> getMedicalRecordList(String address) {
        List<JSONObject> outPut = new ArrayList<>();
        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            List<BigInteger> medicalPlatformId = medicalPlatform.getMedicalRecordList(address);
            for (BigInteger medicalRecordId : medicalPlatformId) {
                JSONObject medicalRecord = getMedicalRecordByIdentityNumber(medicalRecordId,address);
                outPut.add(medicalRecord);
            }
            return outPut;
        } catch (Exception ex) {
            ex.printStackTrace();
            return outPut;
        }
    }

    @Override
    public JSONObject getMedicalRecordByIdentityNumber(BigInteger recordId,String address) {
        JSONObject outPut = new JSONObject();
        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            Tuple11<String, String, String, String, String, String, String, String, String, BigInteger, Boolean> medicalRecord = medicalPlatform.getMedicalRecord(recordId,address);

            outPut.put("code", 200);
            outPut.put("msg", "病历查询成功!");
            outPut.put("recordId", recordId);
            outPut.put("patientName", medicalRecord.getValue1());
            outPut.put("patient", medicalRecord.getValue2());
            outPut.put("doctorName", medicalRecord.getValue3());
            outPut.put("doctor", medicalRecord.getValue4());
            outPut.put("hospitalName", medicalRecord.getValue5());
            outPut.put("department", medicalRecord.getValue6());
            outPut.put("registrationInfo", medicalRecord.getValue7());
            outPut.put("pastMedicalHistory", medicalRecord.getValue8());
            outPut.put("currentMedicalHistory", medicalRecord.getValue9());
            outPut.put("createTime", medicalRecord.getValue10());
            outPut.put("isFilled", medicalRecord.getValue11());

            return outPut;
        } catch (Exception e) {
            e.printStackTrace();
            outPut.put("msg", recordId + "病历查询失败");
            return outPut;
        }
    }

    @Override
    public List<JSONObject> getPatientsList() {
        return Collections.emptyList();
    }

//    @Override
//    public List<JSONObject> getPatientsList() {
//        try {
//            Main mainContract = Main.load(contractAddress, client, cryptoKeyPair);
//            List<BigInteger> patientsList = mainContract.getSicksList();
//            List<JSONObject> jsonObjects = new ArrayList<>();
//            for (BigInteger id : patientsList) {
//                try {
//                    Tuple2<String, String> patientAppointment = mainContract.getSickAppointment(id);
//                    Boolean isFilled = mainContract.isMedicalRecordFilled(id);
//                    if (StringUtils.isNotBlank(patientAppointment.getValue1())) {
//                        if (!isFilled) {
//                            JSONObject outPut = new JSONObject();
//                            outPut.put("status", 200);
//                            outPut.put("hospitalName", patientAppointment.getValue1());
//                            outPut.put("department", patientAppointment.getValue2());
//                            outPut.put("id", id);
//                            Tuple5<String, String, String, BigInteger, BigInteger> patientByIdentityNumber = mainContract.getSickByIdentityNumber(id);
//                            outPut.put("accountAddress", patientByIdentityNumber.getValue1());
//                            outPut.put("name", patientByIdentityNumber.getValue2());
//                            outPut.put("gender", patientByIdentityNumber.getValue3());
//                            outPut.put("age", patientByIdentityNumber.getValue4());
//                            outPut.put("isFilled", isFilled);
//                            jsonObjects.add(outPut);
//                        }
//                    }
//                } catch (Exception err) {
//                    System.out.println("当前账号没有挂号");
//                }
//            }
//            return jsonObjects;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

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
    public Map<String, Object> getAll(String address) throws ContractException {
        Map<String, Object> response = new HashMap<>();
        MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress,client,cryptoKeyPair);
        List appointmentsList = medicalPlatform.getAppointmentsList(address);
        List medicalRecordList = medicalPlatform.getMedicalRecordList(address);
        response.put("appointment", appointmentsList.size());
        response.put("medicalRecord", medicalRecordList.size());
        return response;
    }
=======
>>>>>>> ceece8c (实现多选删除功能)
} 