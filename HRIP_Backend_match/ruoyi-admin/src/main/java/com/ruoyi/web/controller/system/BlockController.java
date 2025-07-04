package com.ruoyi.web.controller.system;

import com.alibaba.fastjson2.JSONObject;
<<<<<<< HEAD
import com.ruoyi.system.domain.Appointment;
import com.ruoyi.system.domain.Info;
import com.ruoyi.system.domain.MedicalRecord;
import com.ruoyi.system.service.IBlockService;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.springframework.beans.factory.annotation.Autowired;
=======
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.system.domain.Appointments;
import com.ruoyi.system.domain.Info;
import com.ruoyi.system.domain.MedicalRecord;
import com.ruoyi.system.mapper.InfoMapper;
import com.ruoyi.system.service.IBlockService;

import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
>>>>>>> ceece8c (实现多选删除功能)
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
<<<<<<< HEAD

=======
import org.springframework.web.multipart.MultipartFile;

import javax.xml.transform.Result;
import java.io.File;
import java.io.IOException;
>>>>>>> ceece8c (实现多选删除功能)
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

<<<<<<< HEAD
@Controller
@CrossOrigin
public class BlockController {

    @Autowired
    private IBlockService blockService;

    @ResponseBody
    @CrossOrigin
    @RequestMapping(path="/login", produces= MediaType.APPLICATION_JSON_VALUE)
    public JSONObject login(String address, BigInteger type, String password) throws ContractException {
        return blockService.login(address, type, password);
    }

    @ResponseBody
    @CrossOrigin
    @RequestMapping(path="/getInfo", produces= MediaType.APPLICATION_JSON_VALUE)
    public JSONObject getInfo(String address) throws ContractException {
        return blockService.getInfo(address);
    }

=======
@RestController
@CrossOrigin

public class BlockController {
    @Autowired
    private IBlockService blockService;
    @Autowired
    private InfoMapper infoMapper;
    /**
     * 发送验证码
     */
    @ResponseBody
    @CrossOrigin
    @RequestMapping(path="/sendCode", produces= MediaType.APPLICATION_JSON_VALUE)
    public JSONObject code(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        return blockService.sendCode(email);
    }
    /**
     * 登录
     * @param userName
     * @param userType
     * @param password
     * @return
     * @throws ContractException
     */
    @ResponseBody
    @CrossOrigin
    @RequestMapping(path="/login", produces= MediaType.APPLICATION_JSON_VALUE)
    public JSONObject login(String userName, BigInteger userType, String password) throws ContractException {
        JSONObject outPut = blockService.login(userName, userType, password);
        return outPut;
    }
    /**
     * 获取个人信息
     * @param userId
     * @return JSONObject
     * @throws ContractException
     */
    @ResponseBody
    @CrossOrigin
    @RequestMapping(path="/getInfo", produces= MediaType.APPLICATION_JSON_VALUE)
    public JSONObject getInfo(BigInteger userId) throws ContractException {
        return blockService.getInfo(userId);
    }
    /**
     * 注册用户
     * @param info
     * @return JSONObject
     */
>>>>>>> ceece8c (实现多选删除功能)
    @ResponseBody
    @CrossOrigin
    @RequestMapping(path="/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject register(@RequestBody Info info) {
        return blockService.register(info);
    }
<<<<<<< HEAD

    @ResponseBody
    @CrossOrigin
    @RequestMapping(path="/createAppointment", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject createAppointment(@RequestBody Appointment appointment) {
        return blockService.createAppointment(appointment);
    }
=======
    /**
     * 忘记密码
     * @param userName
     * @param userType
     * @param password
     * @return JSONObject
     * @throws ContractException
     */
    @ResponseBody
    @CrossOrigin
    @RequestMapping(path="/forgetPassword", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject forgetPassword(String userName, BigInteger userType, String password) throws ContractException {
        return blockService.forgetPassword(userName, userType, password);
    }
    /**
     * 更新个人信息
     * @param info
     * @return JSONObject
     */
    @ResponseBody
    @CrossOrigin
    @RequestMapping(path="/updateInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject updateInfo(@RequestBody Info info) {
        return blockService.updateInfo(info);
    }
    /**
     * 获取图片
     * @param imagePath
     * @return JSONObject
     */
    @ResponseBody
    @CrossOrigin
    @RequestMapping(path = "/getImage", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getImage(String imagePath) {
        return blockService.getImage(imagePath);
    }
    /**
     *
     * 获取所有医生姓名
     * @param
     * @return List<JSONObject>
     */

    @ResponseBody
    @CrossOrigin
    @GetMapping("/getAllDoctorsName")
    public List<JSONObject> getAllDoctorsName(){
        return blockService.getAllDoctorsName();
    }

    /**
     * 获取所有病人姓名
     * @param
     * @return
     */
    @ResponseBody
    @CrossOrigin
    @RequestMapping(path="/getAllPatientsName", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JSONObject> getAllPatientsName(){
        return blockService.getAllPatientsName();
    }

    /**
     * 创建预约
     * @param appointment
     * @return JSONObject
     */
    @ResponseBody
    @CrossOrigin
    @RequestMapping(path="/createAppointment", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject createAppointment(@RequestBody Appointments appointment) {
        return blockService.createAppointment(appointment);
    }
    /**
     * 更新预约
     * @param appointment
     * @return JSONObject
     */
>>>>>>> ceece8c (实现多选删除功能)

    @ResponseBody
    @CrossOrigin
    @RequestMapping(path = "/updateAppointment", produces = MediaType.APPLICATION_JSON_VALUE)
<<<<<<< HEAD
    public JSONObject updateAppointment(@RequestBody Appointment appointment) {
=======
    public JSONObject updateAppointment(@RequestBody Appointments appointment) {
>>>>>>> ceece8c (实现多选删除功能)
        return blockService.updateAppointment(appointment);
    }

    @ResponseBody
    @CrossOrigin
    @RequestMapping(path="/completeAppointment", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject completeAppointment(@RequestParam("appointmentId") BigInteger appointmentId) {
        return blockService.completeAppointment(appointmentId);
    }

    @ResponseBody
    @CrossOrigin
    @RequestMapping(path="/deleteAppointment", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject deleteAppointment(@RequestParam("appointmentId") BigInteger appointmentId) {
        return blockService.deleteAppointment(appointmentId);
    }

    @ResponseBody
    @CrossOrigin
    @RequestMapping(path="/getSickAppointment", produces = MediaType.APPLICATION_JSON_VALUE)
<<<<<<< HEAD
    public JSONObject getSickAppointment(@RequestParam("appointmentId") BigInteger appointmentId, @RequestParam("address") String address) {
        return blockService.getSickAppointment(appointmentId,address);
=======
    public JSONObject getSickAppointment(@RequestParam("appointmentId") BigInteger appointmentId, @RequestParam("userId") BigInteger userId) {
        return blockService.getSickAppointment(appointmentId, userId);
>>>>>>> ceece8c (实现多选删除功能)
    }

    @ResponseBody
    @CrossOrigin
    @RequestMapping(path="/getAppointmentsList", produces = MediaType.APPLICATION_JSON_VALUE)
<<<<<<< HEAD
    public List<Object> getAppointmentsList(String address) {
        return blockService.getAppointmentsList(address);
=======
    public List<Object> getAppointmentsList(BigInteger userId) {
        return blockService.getAppointmentsList(userId);
>>>>>>> ceece8c (实现多选删除功能)
    }

    @ResponseBody
    @CrossOrigin
    @RequestMapping(path="/createMedicalRecord", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject createMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        return blockService.createMedicalRecord(medicalRecord);
    }

    @ResponseBody
    @CrossOrigin
    @RequestMapping(path = "/updateMedicalRecord", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject updateMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        return blockService.updateMedicalRecord(medicalRecord);
    }

    @ResponseBody
    @CrossOrigin
    @RequestMapping(path="/completeMedicalRecord", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject completeMedicalRecord(@RequestParam("recordId") BigInteger recordId) {
        return blockService.completeMedicalRecord(recordId);
    }

    @ResponseBody
    @CrossOrigin
    @RequestMapping(path="/deleteMedicalRecord", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject deleteMedicalRecord(@RequestParam("recordId") BigInteger recordId) {
        return blockService.deleteMedicalRecord(recordId);
    }

    @ResponseBody
    @CrossOrigin
    @RequestMapping(path="/getMedicalRecordList", produces = MediaType.APPLICATION_JSON_VALUE)
<<<<<<< HEAD
    public List<JSONObject> getMedicalRecordList(@RequestParam("address") String address) {
        return blockService.getMedicalRecordList(address);
=======
    public List<JSONObject> getMedicalRecordList(@RequestParam("userId") BigInteger userId) {
        return blockService.getMedicalRecordList(userId);
>>>>>>> ceece8c (实现多选删除功能)
    }

    @ResponseBody
    @CrossOrigin
    @RequestMapping(path="/getMedicalRecordByIdentityNumber", produces = MediaType.APPLICATION_JSON_VALUE)
<<<<<<< HEAD
    public JSONObject getMedicalRecordByIdentityNumber(BigInteger recordId,String address) {
        return blockService.getMedicalRecordByIdentityNumber(recordId,address);
=======
    public JSONObject getMedicalRecordByIdentityNumber(BigInteger recordId, BigInteger userId) {
        return blockService.getMedicalRecordByIdentityNumber(recordId, userId);
>>>>>>> ceece8c (实现多选删除功能)
    }

    @ResponseBody
    @CrossOrigin
    @RequestMapping(path="/getPatientsList", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JSONObject> getPatientsList() {
        return blockService.getPatientsList();
    }

    @GetMapping("/blockchainInfo")
    public ResponseEntity<Map<String, Object>> getBlockchainInfo() {
        return ResponseEntity.ok(blockService.getBlockchainInfo());
    }

    @GetMapping("/getAll")
<<<<<<< HEAD
    public ResponseEntity<Map<String, Object>> getAll(String address) throws ContractException {
        return ResponseEntity.ok(blockService.getAll(address));
=======
    public ResponseEntity<Map<String, Object>> getAll(BigInteger userId) throws ContractException {
        return ResponseEntity.ok(blockService.getAll(userId));
>>>>>>> ceece8c (实现多选删除功能)
    }
}
