import server from "@/utils/request";

// 鎺ュ彛瀹氫箟
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

// 鐢ㄦ埛绠＄悊API
// 鍙戦€侀獙璇佺爜
export function sendCode(email: string) {
  return server.request({
    url: "/sendCode",
    method: "post",
    data: { email }
  });
}

// 鐢ㄦ埛娉ㄥ唽
export function register(name: string, gender: string, age: number, password: string, userType: number, email: string) {
  return server.request({
    url: "/register",
    method: "post",
    headers: {
      // 璁剧疆鏍囧織锛屽憡璇夋嫤鎴櫒杩欐槸鐧诲綍鐩稿叧璇锋眰锛屼笉搴旇杩涜浼氳瘽妫€鏌?
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

// 鐢ㄦ埛鐧诲綍
export function login(userName: string, userType: number, password: string) {
  return server.request({
    url: `/login?userName=${userName}&userType=${userType}&password=${password}`,
    method: "post",
    headers: {
      // 璁剧疆鏍囧織锛屽憡璇夋嫤鎴櫒杩欐槸鐧诲綍璇锋眰锛屼笉搴旇杩涜浼氳瘽妫€鏌?
      isLogin: true
    }
  });
}

// 蹇樿瀵嗙爜
export function forgetPassword(userName: string, userType: number, password: string) {
  return server.request({
    url: `/forgetPassword?userName=${userName}&userType=${userType}&password=${password}`,
    method: "post",
    headers: {
      // 璁剧疆鏍囧織锛屽憡璇夋嫤鎴櫒杩欐槸鐧诲綍鐩稿叧璇锋眰锛屼笉搴旇杩涜浼氳瘽妫€鏌?
      isLogin: true
    }
  });
}

// 鑾峰彇鐢ㄦ埛淇℃伅
export function getUserDetails(userId: number) {
  return server.requestT<UserDetail>({
    url: "/getInfo",
    method: "get",
    params: { userId }
  });
}

// 鏇存柊鐢ㄦ埛淇℃伅
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

// 鑾峰彇鎵€鏈夋偅鑰?
export function getAllPatients() {
  return server.requestT<Array<{userId: number, name: string, gender: string, age: number, email: string}>>({
    url: "/getPatientsList",
    method: "get"
  });
}

// 鑾峰彇鎵€鏈夊尰鐢熷鍚?
export interface DoctorInfo {
  doctorId: number;
  doctorName: string;
}
// 鑾峰彇鎵€鏈夌梾浜哄鍚?
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

// 棰勭害绠＄悊API
// 鍒涘缓棰勭害
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

// 瀹屾垚棰勭害
export function completeAppointment(appointmentId: number) {
  return server.request({
    url: `/completeAppointment?appointmentId=${appointmentId}`,
    method: "post",
  });
}

// 鏇存柊棰勭害
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

// 鑾峰彇棰勭害璇︽儏
export function getSickAppointment(appointmentId: number, userId: number) {
  return server.requestT<AppointmentDetail>({
    url: "/getSickAppointment",
    method: "get",
    params: { appointmentId, userId }
  });
}

// 鑾峰彇鐢ㄦ埛鍏宠仈鐨勬墍鏈夐绾?
export function getAppointmentsList(userId: number) {
  return server.requestT<AppointmentDetail[]>({
    url: "/getAppointmentsList",
    method: "get",
    params: { userId }
  });
}

// 鍒犻櫎棰勭害
export function deleteAppointment(appointmentId: number) {
  return server.request({
    url: "/deleteAppointment",
    method: "post",
    params: { appointmentId }
  });
}

// 鐥呭巻绠＄悊API
// 鍒涘缓鐥呭巻
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

// 瀹屾垚鐥呭巻濉啓
export function completeMedicalRecord(recordId: number) {
  return server.request({
    url: "/completeMedicalRecord",
    method: "post",
    params: { recordId }
  });
}

// 鏇存柊鐥呭巻
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

// 鍒犻櫎鐥呭巻
export function deleteMedicalRecord(recordId: number) {
  return server.request({
    url: "/deleteMedicalRecord",
    method: "post",
    params: { recordId }
  });
}

// 鑾峰彇鐥呭巻璇︽儏
export function getMedicalRecordByIdentityNumber(recordId: number, userId: number) {
  return server.requestT<MedicalRecordDetail>({
    url: `/getMedicalRecordByIdentityNumber?recordId=${recordId}&userId=${userId}`,
    method: "get",
  });
}

// 鑾峰彇鐢ㄦ埛鍏宠仈鐨勬墍鏈夌梾鍘?
export function getMedicalRecordList(userId: number) {
  return server.requestT<MedicalRecordDetail[]>({
    url: "/getMedicalRecordList",
    method: "get",
    params: { userId }
  });
}

// 绯荤粺缁熻API
// 鑾峰彇鍖哄潡閾句俊鎭?
export function getBlockchainInfo() {
  return server.requestT<BlockchainInfo>({
    url: "/blockchainInfo",
    method: "get"
  });
}

// 鑾峰彇鐢ㄦ埛鎵€鏈変俊鎭粺璁?
export function getUserStatistics(userId: number) {
  return server.requestT<StatisticsInfo>({
    url: "/getAll",
    method: "get",
    params: { userId }
  });
} 
