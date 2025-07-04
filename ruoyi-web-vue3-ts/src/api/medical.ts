import server from "@/utils/request";

// 接口定义
export interface UserDetail {
  id: number;
  name: string;
  gender: string;
  age: number;
  userType: number;
  email: string;
  avatar: string;
}

export interface UserUpdateParams {
  userId: number;
  name: string;
  gender: string;
  age: number;
  email: string;
  avatar: string;
}

export interface AppointmentDetail {
  appointmentId: number;
  patientId: number;
  doctorId: number;
  patientName: string;
  doctorName: string;
  hospitalName: string;
  department: string;
  remark: string;
  createTime: number;
  isCompleted: boolean;
}

export interface MedicalRecordDetail {
  recordId: number;
  patientName: string;
  doctorName: string;
  hospitalName: string;
  department: string;
  registrationInfo: string;
  pastMedicalHistory: string;
  currentMedicalHistory: string;
  createTime: number;
  isFilled: boolean;
}

export interface StatisticsInfo {
  patient: number;
  appointment: number;
  medicalRecord: number;
}

export interface BlockchainInfo {
  blockNumber: number;
  transactionCount: number;
}

// 用户管理API
// 发送验证码
export function sendCode(email: string) {
  return server.request({
    url: "/sendCode",
    method: "post",
    data: { email }
  });
}

// 用户注册
export function register(name: string, gender: string, age: number, password: string, userType: number, email: string) {
  return server.request({
    url: "/register",
    method: "post",
    headers: {
      // 设置标志，告诉拦截器这是登录相关请求，不应该进行会话检查
      isLogin: true
    },
    data: {
      name,
      gender,
      age,
      userType,
      password,
      email
    }
  });
}

// 用户登录
export function login(userName: string, userType: number, password: string) {
  return server.request({
    url: `/login?userName=${userName}&userType=${userType}&password=${password}`,
    method: "post",
    headers: {
      // 设置标志，告诉拦截器这是登录请求，不应该进行会话检查
      isLogin: true
    }
  });
}

// 忘记密码
export function forgetPassword(userName: string, userType: number, password: string) {
  return server.request({
    url: `/forgetPassword?userName=${userName}&userType=${userType}&password=${password}`,
    method: "post",
    headers: {
      // 设置标志，告诉拦截器这是登录相关请求，不应该进行会话检查
      isLogin: true
    }
  });
}

// 获取用户信息
export function getUserDetails(userId: number) {
  return server.requestT<UserDetail>({
    url: "/getInfo",
    method: "get",
    params: { userId }
  });
}

// 更新用户信息
export function updateInfo(params: UserUpdateParams) {
  return server.request({
    url: "/updateInfo",
    method: "post",
    data: {
      id: params.userId,
      name: params.name,
      gender: params.gender,
      age: params.age,
      email: params.email,
      avatar: params.avatar
    }
  });
}

// 获取所有患者
export function getAllPatients() {
  return server.requestT<Array<{userId: number, name: string, gender: string, age: number, email: string}>>({
    url: "/getPatientsList",
    method: "get"
  });
}

// 获取所有医生姓名
export interface DoctorInfo {
  doctorId: number;
  doctorName: string;
}
// 获取所有病人姓名
export interface PatientInfo {
  doctorId: number;
  doctorName: string;
}

export function getAllDoctorsName() {
  return server.requestT<DoctorInfo[]>({
    url: "/getAllDoctorsName",
    method: "get"
  });
}

export function getAllPatientsName() {
  return server.requestT<PatientInfo[]>({
    url: "/getAllPatientsName",
    method: "get"
  });
}

// 预约管理API
// 创建预约
export function createAppointment(
  patientName: string, 
  doctorName: string, 
  hospitalName: string, 
  department: string, 
  remark: string, 
  createdAt: number
) {
  return server.request({
    url: "/createAppointment",
    method: "post",
    data: {
      patientName,
      doctorName,
      hospitalName,
      department,
      remark,
      createdAt
    }
  });
}

// 完成预约
export function completeAppointment(appointmentId: number) {
  return server.request({
    url: `/completeAppointment?appointmentId=${appointmentId}`,
    method: "post",
  });
}

// 更新预约
export function updateAppointment(
  id: number, 
  hospitalName: string, 
  department: string, 
  remark: string, 
  createdAt: number
) {
  return server.request({
    url: "/updateAppointment",
    method: "post",
    data: {
      id,
      hospitalName,
      department,
      remark,
      createdAt
    }
  });
}

// 获取预约详情
export function getSickAppointment(appointmentId: number, userId: number) {
  return server.requestT<AppointmentDetail>({
    url: "/getSickAppointment",
    method: "get",
    params: { appointmentId, userId }
  });
}

// 获取用户关联的所有预约
export function getAppointmentsList(userId: number) {
  return server.requestT<AppointmentDetail[]>({
    url: "/getAppointmentsList",
    method: "get",
    params: { userId }
  });
}

// 删除预约
export function deleteAppointment(appointmentId: number) {
  return server.request({
    url: "/deleteAppointment",
    method: "post",
    params: { appointmentId }
  });
}

// 病历管理API
// 创建病历
export function createMedicalRecord(
  patientName: string, 
  doctorName: string, 
  hospitalName: string, 
  department: string, 
  registrationInfo: string, 
  pastMedicalHistory: string, 
  currentMedicalHistory: string, 
  createdAt: number
) {
  return server.request({
    url: "/createMedicalRecord",
    method: "post",
    data: {
      patientName,
      doctorName,
      hospitalName,
      department,
      registrationInfo,
      pastMedicalHistory,
      currentMedicalHistory,
      createdAt
    }
  });
}

// 完成病历填写
export function completeMedicalRecord(recordId: number) {
  return server.request({
    url: "/completeMedicalRecord",
    method: "post",
    params: { recordId }
  });
}

// 更新病历
export function updateMedicalRecord(
  id: number, 
  hospitalName: string, 
  department: string, 
  registrationInfo: string, 
  pastMedicalHistory: string, 
  currentMedicalHistory: string, 
  createdAt: number
) {
  return server.request({
    url: "/updateMedicalRecord",
    method: "post",
    data: {
      id,
      hospitalName,
      department,
      registrationInfo,
      pastMedicalHistory,
      currentMedicalHistory,
      createdAt
    }
  });
}

// 删除病历
export function deleteMedicalRecord(recordId: number) {
  return server.request({
    url: "/deleteMedicalRecord",
    method: "post",
    params: { recordId }
  });
}

// 获取病历详情
export function getMedicalRecordByIdentityNumber(recordId: number, userId: number) {
  return server.requestT<MedicalRecordDetail>({
    url: `/getMedicalRecordByIdentityNumber?recordId=${recordId}&userId=${userId}`,
    method: "get",
  });
}

// 获取用户关联的所有病历
export function getMedicalRecordList(userId: number) {
  return server.requestT<MedicalRecordDetail[]>({
    url: "/getMedicalRecordList",
    method: "get",
    params: { userId }
  });
}

// 系统统计API
// 获取区块链信息
export function getBlockchainInfo() {
  return server.requestT<BlockchainInfo>({
    url: "/blockchainInfo",
    method: "get"
  });
}

// 获取用户所有信息统计
export function getUserStatistics(userId: number) {
  return server.requestT<StatisticsInfo>({
    url: "/getAll",
    method: "get",
    params: { userId }
  });
} 