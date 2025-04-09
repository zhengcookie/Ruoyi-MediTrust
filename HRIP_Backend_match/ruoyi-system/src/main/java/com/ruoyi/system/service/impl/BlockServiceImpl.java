package com.ruoyi.system.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.system.contract.MedicalPlatform;
import com.ruoyi.system.domain.Appointment;
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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.sql.Timestamp;
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

    @Override
    public JSONObject register(Info info) {
        JSONObject outPut = new JSONObject();
        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            TransactionReceipt receipt = medicalPlatform.register(info.getName(), info.getGender(), info.getAge(), info.getUserType(), info.getAddress());
            infoMapper.insertUser(info);
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
    public JSONObject createAppointment(Appointment appointment) {
        JSONObject outPut = new JSONObject();
        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            BigInteger createdAt = BigInteger.valueOf(appointment.getCreatedAt().getTime());
//            BigInteger CreatedAt = new BigInteger(appointment.getCreatedAt().toString());
            TransactionReceipt receipt = medicalPlatform.createAppointment(appointment.getPatientName(), appointment.getPatient(), appointment.getDoctorName(), appointment.getDoctor(), appointment.getHospitalName(), appointment.getDepartment(), appointment.getRemark(),createdAt);
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
    public JSONObject updateAppointment(Appointment appointment) {
        JSONObject outPut = new JSONObject();

        try {
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            BigInteger createdAt = BigInteger.valueOf(appointment.getCreatedAt().getTime());
            BigInteger appointmentId = new BigInteger(appointment.getId().toString());
            TransactionReceipt receipt = medicalPlatform.updateAppointment(appointmentId, appointment.getPatientName(), appointment.getPatient(), appointment.getDoctorName(), appointment.getDoctor(), appointment.getHospitalName(), appointment.getDepartment(), appointment.getRemark(),createdAt);
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
        } catch (Exception e) {
            outPut.put("code", 400);
            outPut.put("msg", e.getMessage());
            return outPut;
        }
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
                outPut.add(appointments);
            }
        } catch (Exception e) {
            outPut.add(new JSONObject().put("msg", "查询失败"));
            return outPut;
        }
        return outPut;
    }

    @Override
    public JSONObject createMedicalRecord(MedicalRecord medicalRecord) {
        JSONObject outPut = new JSONObject();
        try {

            BigInteger createdAt = BigInteger.valueOf(medicalRecord.getCreatedAt().getTime());
            MedicalPlatform medicalPlatform = MedicalPlatform.load(contractAddress, client, cryptoKeyPair);
            TransactionReceipt receipt = medicalPlatform.createMedicalRecord(medicalRecord.getPatientName(), medicalRecord.getPatient(), medicalRecord.getDoctorName(), medicalRecord.getDoctor(), medicalRecord.getHospitalName(), medicalRecord.getDepartment(), medicalRecord.getRegistrationInfo(), medicalRecord.getPastMedicalHistory(), medicalRecord.getCurrentMedicalHistory(),createdAt);
            if (receipt.isStatusOK()){
                medicalRecordMapper.insertMedicalRecord(medicalRecord);
                outPut.put("msg", "病历添加成功");
                outPut.put("code", 200);
//                outPut.put("data", receipt);
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
            TransactionReceipt receipt = medicalPlatform.updateMedicalRecord(recordId,medicalRecord.getPatientName(), medicalRecord.getPatient(), medicalRecord.getDoctorName(), medicalRecord.getDoctor(), medicalRecord.getHospitalName(), medicalRecord.getDepartment(), medicalRecord.getRegistrationInfo(), medicalRecord.getPastMedicalHistory(), medicalRecord.getCurrentMedicalHistory(),createdAt );
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
            medicalRecordMapper.deleteMedicalRecordById(recordId.longValue());
            outPut.put("msg", "病历信息删除成功");
            outPut.put("code", 200);
        } catch (Exception e) {
            outPut.put("msg", "病历信息删除失败");
            outPut.put("code", 400);
        }
        return outPut;
    }

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
} 