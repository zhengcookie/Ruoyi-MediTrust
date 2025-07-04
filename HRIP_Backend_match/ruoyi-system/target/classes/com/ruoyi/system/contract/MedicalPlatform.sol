<<<<<<< HEAD
pragma solidity ^0.4.25;
pragma experimental ABIEncoderV2; //添加就不会出现堆栈过深了
contract MedicalPlatform {
    enum UserType { None, Patient, Doctor }
    
    // 统一用户结构
    struct User {
=======
// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.4.25;
pragma experimental ABIEncoderV2;

contract MedicalPlatform {
    enum UserType { None, Patient, Doctor }
    
    // 用户结构（使用ID标识）
    struct User {
        uint id;
>>>>>>> ceece8c (实现多选删除功能)
        string name;
        string gender;
        uint age;
        UserType userType;
        address account;
        uint[] appointments;    // 关联预约
        uint[] medicalRecords;  // 关联病历
<<<<<<< HEAD
    }
    
    // 增强版预约结构
    struct Appointment {
        string patientName;
        address patient;
        string doctorName;
        address doctor;
=======
        string Email;
    }
    
    // 增强版预约结构（使用用户ID）
    struct Appointment {
        uint id;
        string patientName;
        uint patientId;
        string doctorName;
        uint doctorId;
>>>>>>> ceece8c (实现多选删除功能)
        string hospitalName;
        string department;
        string remark;
        uint createTime;
        bool isCompleted;
    }
    
<<<<<<< HEAD
    // 完整病历结构
    struct MedicalRecord {
        string patientName;
        address patient;
        string doctorName;
        address doctor;
=======
    // 完整病历结构（使用用户ID）
    struct MedicalRecord {
        uint id;
        string patientName;
        uint patientId;
        string doctorName;
        uint doctorId;
>>>>>>> ceece8c (实现多选删除功能)
        string hospitalName;
        string department;
        string registrationInfo;
        string pastMedicalHistory;
        string currentMedicalHistory;
        uint createTime;        
<<<<<<< HEAD
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
=======
        bool isFilled;
    }
    
    // 数据存储
    mapping(uint => User) public users;          // 用户ID => 用户
    mapping(string => uint) nameToUserId; // 姓名 => 用户ID
    mapping(uint => Appointment) public appointments;
    mapping(uint => MedicalRecord) public medicalRecords;
    mapping(uint => mapping(uint => bool)) public accessPermissions; // 病历访问权限（用户ID => 病历ID）
    
    // ID计数器
    uint private userIdCounter = 1;
    uint private appointmentCounter = 1;
    uint private recordCounter = 1;
    uint[] allPatientIds;    // 所有患者ID
    uint[] allDoctorIds;     // 所有医生ID
    
    // 事件
    event UserRegistered(uint indexed userId, address account, UserType userType);
    event AppointmentCreated(uint indexed id, uint patientId, uint doctorId);
    event RecordCreated(uint indexed id, uint patientId, uint doctorId);
    event AccessGranted(uint patientId, uint doctorId, uint recordId);
 

    // 用户注册（返回用户ID）
    function register(string memory _name, string memory _gender, uint _age, UserType _userType,string memory _Email) public returns (uint) {
        uint userId = userIdCounter++;
        users[userId] = User({
            id: userId,
            name: _name,
            gender: _gender,
            age: _age,
            userType: _userType,
            account: msg.sender,
            appointments: new uint[](0),
            medicalRecords: new uint[](0),
            Email:_Email
        });
        if (users[userId].userType == UserType.Patient){
            allPatientIds.push(userId);
        } else if(_userType == UserType.Doctor) {
            allDoctorIds.push(userId);
        }
        nameToUserId[_name] = userId;
        emit UserRegistered(userId, msg.sender, _userType);
        return userId;
    }
     // 获取全部医生简要信息（适合前端展示）
    function getAllDoctors() public view returns (
        uint[] memory ids,
        string[] memory names,
        string[] memory genders,
        uint[] memory ages,
        string[] memory Emails
    ) {
        uint length = allDoctorIds.length;
        ids = new uint[](length);
        names = new string[](length);
        genders = new string[](length);
        ages = new uint[](length);
        Emails = new string[](length);

        for(uint i = 0; i < length; i++) {
            uint id = allDoctorIds[i];
            User storage user = users[id];
            ids[i] = id;
            names[i] = user.name;
            genders[i] = user.gender;
            ages[i] = user.age;
            Emails[i] = user.Email;
        }
        return (ids, names, genders, ages,Emails);
    }

    // 获取全部患者简要信息  
    function getAllPatients() public view returns (
        uint[] memory ids,
        string[] memory names,
        string[] memory genders,
        uint[] memory ages,
        string[] memory Emails
    ) {
        uint length = allPatientIds.length;
        ids = new uint[](length);
        names = new string[](length);
        genders = new string[](length);
        ages = new uint[](length);
        Emails = new string[](length);
        for(uint i = 0; i < length; i++) {
            uint id = allPatientIds[i];
            User storage user = users[id];
            ids[i] = id;
            names[i] = user.name;
            genders[i] = user.gender;
            ages[i] = user.age;
            Emails[i] = user.Email;
        }
        return (ids, names, genders, ages,Emails);
    }

    // 获取单个用户完整信息
    function getUserDetails(uint userId) public view returns (
        string memory name,
        string memory gender,
        uint age,
        UserType userType,
        address account,
        string memory email
    ) {
        User storage user = users[userId];
        return (
            user.name,
            user.gender,
            user.age,
            user.userType,
            user.account,
            user.Email
        );
    }
    // 获取所有医生id以及姓名等
    function getAllDoctorsName() public view returns (uint[] ids, string[] memory names){
        uint length = allDoctorIds.length;
        ids = new uint[](length);
        names = new string[](length);
       
        for(uint i = 0; i < length; i++) {
            uint id = allDoctorIds[i];
            User storage user = users[id];
            ids[i] = id;
            names[i] = user.name;
        }
        return (ids, names);
    }
     // 获取所有医生id以及姓名等
    function getAllPatientssName() public view returns (uint[] ids, string[] memory names){
        uint length = allPatientIds.length;
        ids = new uint[](length);
        names = new string[](length);
       
        for(uint i = 0; i < length; i++) {
            uint id = allPatientIds[i];
            User storage user = users[id];
            ids[i] = id;
            names[i] = user.name;
        }
        return (ids, names);
    }
    // 通过地址获取用户ID
    function getUserId(string name) public view returns (uint) {
        return nameToUserId[name];
    }
    function updateUser(uint userId,string memory name, string memory gender,uint age,string memory Email) public{
        User storage user = users[userId];
        user.name = name;
        user.gender = gender;
        user.age = age;
        user.Email = Email;
        nameToUserId[name] = userId;
    }

    // 创建预约（使用用户ID）
    function createAppointment(
        string memory _patientName,
        string memory _doctorName,
        string memory _hospitalName,
        string memory _department,
        string memory _remark,
        uint _createTime
    ) public {
        // 检查病人姓名是否存在
        require(nameToUserId[_patientName] != 0, "Patient name not found");
        // 检查医生姓名是否存在
        require(nameToUserId[_doctorName] != 0, "Doctor name not found");
        uint id = appointmentCounter++;
        uint patientId = nameToUserId[_patientName];
        uint doctorId = nameToUserId[_doctorName];
        
        appointments[id] = Appointment({
            id: id,
            patientName:_patientName,
            patientId: patientId,
            doctorName:_doctorName,
            doctorId: doctorId,
            hospitalName: _hospitalName,
            department: _department,
            remark: _remark,
            createTime: _createTime,
            isCompleted: false
        });
        
        users[patientId].appointments.push(id);
        users[doctorId].appointments.push(id);
        
        emit AppointmentCreated(id, patientId, doctorId);
    }
     // 完成预约
    function completeAppointment(uint appointmentId) public {
        require(!isCompletedApoointment(appointmentId),"Appointment is completed");
        appointments[appointmentId].isCompleted = true;
    }
    function isCompletedApoointment(uint appointmentId) public view returns (bool) {
        return appointments[appointmentId].isCompleted;
    }
    // 更新预约
    function updateAppointment(
        uint appointmentId,
        string memory _hospitalName,
        string memory _department,
        string memory _remark,
        uint _createTime
    ) public {
        
        // 更新数据结构（移除冗余的姓名存储）
        Appointment storage appt = appointments[appointmentId];
        appt.hospitalName = _hospitalName;
        appt.department = _department;
        appt.remark = _remark;
        appt.createTime = _createTime;
    }

    // 获取预约详情
    function getSickAppointment(uint _appointmentId,uint Id) public view returns (
        uint id,
        uint patientId,
        string memory patientName,
        uint doctorId,
        string memory doctorName,
        string memory hospitalName,
        string memory department,
        string memory remark,
        uint createTime,
        bool isCompleted
    ) {
        Appointment memory appt = appointments[_appointmentId];
        if (users[Id].userType == UserType.Patient){
            require(users[Id].id == appt.patientId,"Invaild Patient");
        }else if (users[Id].userType == UserType.Doctor){
            require(users[Id].id == appt.doctorId,"Invaild Doctor");
        }
       
        return (
            appt.id,
            appt.patientId,
            appt.patientName,
            appt.doctorId,
            appt.doctorName,
            appt.hospitalName,
            appt.department,
            appt.remark,
            appt.createTime,
            appt.isCompleted
        );
    }
      function getAppointmentsList(uint Id) public view returns (uint[] memory){
        return users[Id].appointments;
    }
     function deleteAppointment(uint appointmentId) public {
        Appointment storage appointment = appointments[appointmentId];
        _removeFromArray(users[appointment.patientId].appointments, appointmentId);
        _removeFromArray(users[appointment.doctorId].appointments, appointmentId);
        delete appointments[appointmentId];
    }

    // 创建病历（增强权限控制）
    function createMedicalRecord(
        string memory _patientName,
        string memory _doctorName,
        string memory _hospitalName,
        string memory _department,
        string memory _registrationInfo,
        string memory _pastMedicalHistory,
        string memory _currentMedicalHistory,
        uint _createTime
    ) public {
         // 检查病人姓名是否存在
        require(nameToUserId[_patientName] != 0, "Patient name not found");
        // 检查医生姓名是否存在
        require(nameToUserId[_doctorName] != 0, "Doctor name not found");
        uint patientId = nameToUserId[_patientName];
        uint doctorId = nameToUserId[_doctorName];
        uint id = recordCounter++;
        medicalRecords[id] = MedicalRecord({
            id: id,
            patientName:_patientName,
            patientId: patientId,
            doctorName:_doctorName,
            doctorId: doctorId,
            hospitalName: _hospitalName,
            department: _department,
            registrationInfo: _registrationInfo,
            pastMedicalHistory: _pastMedicalHistory,
            currentMedicalHistory: _currentMedicalHistory,
            createTime: _createTime,
            isFilled: false
        });
        
        users[patientId].medicalRecords.push(id);
        users[doctorId].medicalRecords.push(id);
        emit RecordCreated(id, patientId, doctorId);
    }

    // 完成病历填写
    function completeMedicalRecord(uint recordId) public {
        MedicalRecord storage record = medicalRecords[recordId];
        require(!iscompleteMedicalRecord(recordId), "Record already filled");
        record.isFilled = true;
    }
    function iscompleteMedicalRecord(uint RecordId) public view returns (bool) {
        return medicalRecords[RecordId].isFilled;
    }

    // 更新病历信息
    function updateMedicalRecord(
        uint recordId,
        string memory _hospitalName,
        string memory _department,
        string memory _registrationInfo,
        string memory _pastMedicalHistory,
        string memory _currentMedicalHistory,
        uint _createTime
    ) public {
        MedicalRecord storage record = medicalRecords[recordId];
        // 更新字段（保留关键ID信息）
        record.hospitalName = _hospitalName;
        record.department = _department;
        record.registrationInfo = _registrationInfo;
        record.pastMedicalHistory = _pastMedicalHistory;
        record.currentMedicalHistory = _currentMedicalHistory;
        record.createTime = _createTime;
    }

    // 删除病历（严格权限控制）
    function deleteMedicalRecord(uint recordId) public {
        MedicalRecord storage record = medicalRecords[recordId];
        // 从关联用户中删除
        _removeFromArray(users[record.patientId].medicalRecords, recordId);
        _removeFromArray(users[record.doctorId].medicalRecords, recordId);
        // 清除数据
        delete medicalRecords[recordId];
    }

    // 获取病历详情
    function getMedicalRecord(uint _recordId,uint Id) public view returns (
        uint id,
        string memory patientName,
        string memory doctorName,
        string memory hospitalName,
        string memory department,
        string memory registrationInfo,
        string memory pastMedicalHistory,
        string memory currentMedicalHistory,
        uint createTime,
        bool isFilled
    ) {
         MedicalRecord memory record = medicalRecords[_recordId];
        if (users[Id].userType == UserType.Patient){
            require(users[Id].id == record.patientId,"Invaild Patient");
        }else if (users[Id].userType == UserType.Doctor){
            require(users[Id].id == record.doctorId,"Invaild Doctor");
        }
       
        return (
            record.id,
            record.patientName,
            record.doctorName,
            record.hospitalName,
            record.department,
            record.registrationInfo,
            record.pastMedicalHistory,
            record.currentMedicalHistory,
            record.createTime,
            record.isFilled
        );
    }
     function getMedicalRecordList(uint Id) public view returns (uint[] memory){
        return users[Id].medicalRecords;
    }
 

    // 原有_removeFromArray函数保持不变...

    // 其他辅助函数（权限检查、数组操作等保持类似逻辑）
>>>>>>> ceece8c (实现多选删除功能)
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
<<<<<<< HEAD

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
=======
>>>>>>> ceece8c (实现多选删除功能)
}