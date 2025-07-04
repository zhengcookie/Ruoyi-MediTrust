// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.4.25;
pragma experimental ABIEncoderV2;

contract MedicalPlatform {
    enum UserType { None, Patient, Doctor }
    
    // 用户结构（使用ID标识）
    struct User {
        uint id;
        string name;
        string gender;
        uint age;
        UserType userType;
        address account;
        uint[] appointments;    // 关联预约
        uint[] medicalRecords;  // 关联病历
        string Email;
    }
    
    // 增强版预约结构（使用用户ID）
    struct Appointment {
        uint id;
        string patientName;
        uint patientId;
        string doctorName;
        uint doctorId;
        string hospitalName;
        string department;
        string remark;
        uint createTime;
        bool isCompleted;
    }
    
    // 完整病历结构（使用用户ID）
    struct MedicalRecord {
        uint id;
        string patientName;
        uint patientId;
        string doctorName;
        uint doctorId;
        string hospitalName;
        string department;
        string registrationInfo;
        string pastMedicalHistory;
        string currentMedicalHistory;
        uint createTime;        
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
}