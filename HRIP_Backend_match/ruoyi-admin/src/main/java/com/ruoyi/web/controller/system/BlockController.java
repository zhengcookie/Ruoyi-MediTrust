package com.ruoyi.web.controller.system;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.system.domain.Appointment;
import com.ruoyi.system.domain.Info;
import com.ruoyi.system.domain.MedicalRecord;
import com.ruoyi.system.service.IBlockService;
import org.fisco.bcos.sdk.transaction.model.exception.ContractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

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

    @ResponseBody
    @CrossOrigin
    @RequestMapping(path="/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject register(@RequestBody Info info) {
        return blockService.register(info);
    }

    @ResponseBody
    @CrossOrigin
    @RequestMapping(path="/createAppointment", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject createAppointment(@RequestBody Appointment appointment) {
        return blockService.createAppointment(appointment);
    }

    @ResponseBody
    @CrossOrigin
    @RequestMapping(path = "/updateAppointment", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject updateAppointment(@RequestBody Appointment appointment) {
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
    public JSONObject getSickAppointment(@RequestParam("appointmentId") BigInteger appointmentId, @RequestParam("address") String address) {
        return blockService.getSickAppointment(appointmentId,address);
    }

    @ResponseBody
    @CrossOrigin
    @RequestMapping(path="/getAppointmentsList", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Object> getAppointmentsList(String address) {
        return blockService.getAppointmentsList(address);
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
    public List<JSONObject> getMedicalRecordList(@RequestParam("address") String address) {
        return blockService.getMedicalRecordList(address);
    }

    @ResponseBody
    @CrossOrigin
    @RequestMapping(path="/getMedicalRecordByIdentityNumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject getMedicalRecordByIdentityNumber(BigInteger recordId,String address) {
        return blockService.getMedicalRecordByIdentityNumber(recordId,address);
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
    public ResponseEntity<Map<String, Object>> getAll(String address) throws ContractException {
        return ResponseEntity.ok(blockService.getAll(address));
    }
}
