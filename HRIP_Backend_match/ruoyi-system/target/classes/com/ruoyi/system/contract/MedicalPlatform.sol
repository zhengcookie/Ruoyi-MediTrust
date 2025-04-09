pragma solidity ^0.4.25;
pragma experimental ABIEncoderV2; //添加就不会出现堆栈过深了
contract MedicalPlatform {
    enum UserType { None, Patient, Doctor }
    
    // 统一用户结构
    struct User {
        string name;
        string gender;
        uint age;
        UserType userType;
        address account;
        uint[] appointments;    // 关联预约
        uint[] medicalRecords;  // 关联病历
    }
    
    // 增强版预约结构
    struct Appointment {
        string patientName;
        address patient;
        string doctorName;
        address doctor;
        string hospitalName;
        string department;
        string remark;
        uint createTime;
        bool isCompleted;
    }
    
    // 完整病历结构
    struct MedicalRecord {
        string patientName;
        address patient;
        string doctorName;
        address doctor;
        string hospitalName;
        string department;
        string registrationInfo;
        string pastMedicalHistory;
        string currentMedicalHistory;
        uint createTime;        
        bool isFilled;          // 填写病历
    }
    
    // 核心数据存储
    mapping(address => User) public users;
    mapping(uint => Appointment) public appointments;
    mapping(uint => MedicalRecord) public medicalRecords;
    mapping(address => mapping(uint => bool)) public accessPermissions; // 病历访问权限
    
    // 自动ID生成器
    uint private appointmentCounter = 1;
    uint private recordCounter = 1;
    
    // 事件系统
    event UserRegistered(address indexed user, UserType userType);
    event AppointmentCreated(uint indexed id, address patient, address doctor);
    event RecordCreated(uint indexed id, address patient, address doctor);
    event AccessGranted(address patient, address doctor, uint recordId);

   
    // 用户注册（统一入口）
    function register(string memory  _name,string memory _gender, uint _age, UserType _userType, address account) public {
        // require(users[msg.sender].userType == UserType.None, "Already registered");
        
        users[account] = User({
            name: _name,
            gender:_gender,
            age:_age,
            userType: _userType,
            account:account,
            appointments: new uint[](0),
            medicalRecords: new uint[](0)
        });
        
        emit UserRegistered(account, _userType);
    }
    function _removeFromArray(uint[] storage arr, uint targetId) internal {
        uint targetIndex = arr.length; // 初始化为无效值
        // 1. 查找目标索引
        for (uint i = 0; i < arr.length; i++) {
            if (arr[i] == targetId) {
                targetIndex = i;
                break;
            }
        }
        require(targetIndex < arr.length, "Element not found");
        
        // 2. 将后续元素前移（保持顺序）
        for (uint j = targetIndex; j < arr.length - 1; j++) {
            arr[j] = arr[j + 1];
        }
        // 3. 减少数组长度
        arr.length--;
    }

    // 创建预约（患者操作）
    function createAppointment(
        string memory _patientName,address _patient,string memory _doctorName,
        address _doctor,string memory _hospitalName, string memory  _department, string memory _remark
        ) {
        require(users[_patient].userType == UserType.Patient, "Invalid Patient");
        require(users[_doctor].userType == UserType.Doctor, "Invalid Doctor");
        
        uint id = appointmentCounter++;
        require(!isCompletedApoointment(id),"Appointment is completed");
        appointments[id] = Appointment({
            patientName:_patientName,
            patient: _patient,
            doctorName:_doctorName,
            doctor: _doctor,
            hospitalName:_hospitalName,
            department: _department,
            remark:_remark,
            createTime: block.timestamp,
            isCompleted: false
        });
        
        _addAppointmentToUser(_patient, id);
        _addAppointmentToUser(_doctor, id);
        
        emit AppointmentCreated(id, _patient, _doctor);
    }
    // 完成预约
    function completeAppointment(uint appointmentId){
        require(!isCompletedApoointment(appointmentId),"Appointment is completed");
        appointments[appointmentId].isCompleted = true;
    }
    function isCompletedApoointment(uint appointmentId) public view returns (bool) {
        return appointments[appointmentId].isCompleted;
    }
    // 更新预约
    function  updateAppointment(
    uint appointmentId,string memory _patientName,address _patient,string memory _doctorName,
    address _doctor,string memory _hospitalName, string memory  _department, 
    string memory _remark
    ){
        require(users[_patient].userType == UserType.Patient,"Invalid Patient");
        require(users[_doctor].userType == UserType.Doctor,"Invalid Doctor");
        require(!isCompletedApoointment(appointmentId),"Appointment is completed");
        appointments[appointmentId] = Appointment({
            patientName:_patientName,
            patient: _patient,
            doctorName:_doctorName,
            doctor: _doctor,
            hospitalName:_hospitalName,
            department: _department,
            remark:_remark,
            createTime: block.timestamp,
            isCompleted: false
        });
    }
    function deleteAppointment(uint appointmentId){
        Appointment storage appointment = appointments[appointmentId];
        _removeFromArray(users[appointment.patient].appointments, appointmentId);
        _removeFromArray(users[appointment.doctor].appointments, appointmentId);
        delete appointments[appointmentId];
    }
    
    
    // 获取预约信息
    function getSickAppointment(uint appointmentId)public view returns (string memory,address,string memory,address,string memory,string memory,string memory,uint,bool)
    {
        Appointment storage appointment =  appointments[appointmentId];
        return (appointment.patientName,appointment.patient,appointment.doctorName,appointment.doctor,appointment.hospitalName,appointment.department, 
        appointment.remark,appointment.createTime,appointment.isCompleted);
    }
    
    function getAppointmentsList(address _user) public view returns (uint[]){
        return users[_user].appointments;
    }
    
    // 创建病历（医生操作）
    function createMedicalRecord(  
        string memory patientName,
        address patient,
        string memory doctorName,
        address doctor,
        string memory hospitalName,
        string memory department,
        string memory registrationInfo,
        string memory pastMedicalHistory,
        string memory currentMedicalHistory
    ) public {
        require(users[doctor].userType == UserType.Doctor, "Invalid Doctor");
        require(users[patient].userType == UserType.Patient, "Invalid Patient");
        uint id = recordCounter++;
        require(!isMedicalRecordFilled(id),"MedicalRecord is Filled");
        medicalRecords[id] = MedicalRecord(patientName,patient,doctorName,doctor,hospitalName,department,registrationInfo,pastMedicalHistory,currentMedicalHistory,block.timestamp,false);
        users[patient].medicalRecords.push(id);
        users[doctor].medicalRecords.push(id);
        emit RecordCreated(id, patient, doctor);
    }
    
    function isMedicalRecordFilled(uint _recordId) public view returns(bool) {
        return medicalRecords[_recordId].isFilled;
    }
    function updateMedicalRecord(
        uint  recordId,
        string memory patientName,
        address  patient,
        string memory doctorName,
        address  doctor,
        string memory hospitalName,
        string memory department,
        string memory registrationInfo,
        string memory pastMedicalHistory,
        string memory currentMedicalHistory     
    ){
         require(users[doctor].userType == UserType.Doctor, "Invalid Doctor");
         require(users[patient].userType == UserType.Patient, "Invalid Patient");
         require(!isMedicalRecordFilled(recordId),"MedicalRecord is Filled");
         medicalRecords[recordId] = MedicalRecord(patientName,patient,doctorName,doctor,hospitalName,department,registrationInfo,pastMedicalHistory,currentMedicalHistory,block.timestamp,false);
    }
    
    function completeMedicalRecord(uint recordId){
        require(!isMedicalRecordFilled(recordId),"MedicalRecord is Filled");
        medicalRecords[recordId].isFilled = true;
    }
    
     function deleteMedicalRecord(uint recordId){
        MedicalRecord medicalRecord = medicalRecords[recordId];
        _removeFromArray(users[medicalRecord.patient].medicalRecords, recordId);
        _removeFromArray(users[medicalRecord.doctor].medicalRecords, recordId);
        delete medicalRecords[recordId];
    }

    // 获取加密病历
    function getMedicalRecord(uint _recordId) public view returns(
        string memory,address,string memory,address,string memory,string memory,string memory,string memory,
        string memory,uint,bool) {
        MedicalRecord memory record = medicalRecords[_recordId];
        return (
            record.patientName,record.patient,record.doctorName,record.doctor,record.hospitalName,record.department,record.registrationInfo,record.pastMedicalHistory,record.currentMedicalHistory,record.createTime,record.isFilled);
    }
    function getMedicalRecordList(address _user) public view returns (uint[]){
        return users[_user].medicalRecords;
    }
    
    // 内部工具函数
    function _addAppointmentToUser(address _user, uint _id) private {
        users[_user].appointments.push(_id);
    }

    function appointmentExists(address _doctor, address _patient) private view returns(bool) {
        for(uint i=0; i<users[_doctor].appointments.length; i++){
            if(appointments[users[_doctor].appointments[i]].patient == _patient){
                return true;
            }
        }
        return false;
    }
}