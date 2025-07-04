# 区块链医疗平台API接口文档

## 基础信息

- 基础URL：`/api`
- 响应格式：JSON
- 通用响应字段：
  - `code`: 状态码，200表示成功，400表示失败
  - `msg`: 响应消息

## 用户管理

### 发送验证码

- **URL**: `/sendCode`
- **方法**: POST
- **请求体**:
  ```json
  {
    "email": "string"   // 用户邮箱
  }
  ```
- **响应参数**:
  ```json
  {
    "code": 200,
    "msg": "验证码发送成功"
  }
  ```

### 用户登录

- **URL**: `/login`
- **方法**: GET/POST
- **请求参数**:
  ```
  userName: string       // 用户名
  userType: number       // 用户类型
  password: string       // 密码
  ```
- **响应参数**:
  ```json
  {
    "code": 200,
    "msg": "用户登录成功",
    "id": "number",
    "name": "string",
    "gender": "string",
    "age": "number",
    "userType": "number",
    "address": "string"
  }
  ```

### 获取用户信息

- **URL**: `/getInfo`
- **方法**: GET
- **请求参数**:
  ```
  userId: number        // 用户ID
  ```
- **响应参数**:
  ```json
  {
    "id": "number",
    "name": "string",
    "gender": "string",
    "age": "number",
    "userType": "number",
    "address": "string"
  }
  ```

### 用户注册

- **URL**: `/register`
- **方法**: POST
- **请求体**:
  ```json
  {
    "name": "string",
    "gender": "string",
    "age": "number",
    "userType": "number",
    "password": "string",
    "email": "string"
  }
  ```
- **响应参数**:
  ```json
  {
    "code": 200,
    "msg": "用户创建成功!"
  }
  ```

### 忘记密码

- **URL**: `/forgetPassword`
- **方法**: POST
- **请求参数**:
  ```
  userName: string       // 用户名
  userType: number       // 用户类型
  password: string       // 新密码
  ```
- **响应参数**:
  ```json
  {
    "code": 200,
    "msg": "密码重置成功"
  }
  ```

### 更新用户信息

- **URL**: `/updateInfo`
- **方法**: POST
- **请求体**:
  ```json
  {
    "id": "number",
    "name": "string",
    "gender": "string",
    "age": "number",
    "userType": "number",
    "email": "string",
    "avatar": "string"
  }
  ```
- **响应参数**:
  ```json
  {
    "code": 200,
    "msg": "用户信息更新成功"
  }
  ```

### 获取图片

- **URL**: `/getImage`
- **方法**: GET
- **请求参数**:
  ```
  imagePath: string     // 图片路径
  ```
- **响应参数**:
  ```json
  {
    "imageData": "base64string",
    "contentType": "string"
  }
  ```

### 获取所有医生姓名

- **URL**: `/getAllDoctorsName`
- **方法**: GET
- **响应参数**:
  ```json
  [
    {
      "id": "number",
      "name": "string"
    }
  ]
  ```

## 挂号预约管理

### 创建挂号预约

- **URL**: `/createAppointment`
- **方法**: POST
- **请求体**:
  ```json
  {
    "patientName": "string",
    "doctorName": "string",
    "hospitalName": "string",
    "department": "string",
    "remark": "string",
    "createdAt": "timestamp"
  }
  ```
- **响应参数**:
  ```json
  {
    "code": 200,
    "msg": "挂号预约成功，请前往挂号预约的科室就诊"
  }
  ```

### 更新挂号预约

- **URL**: `/updateAppointment`
- **方法**: POST
- **请求体**:
  ```json
  {
    "id": "number",
    "hospitalName": "string",
    "department": "string",
    "remark": "string",
    "createdAt": "timestamp"
  }
  ```
- **响应参数**:
  ```json
  {
    "code": 200,
    "msg": "更新成功"
  }
  ```

### 完成挂号预约

- **URL**: `/completeAppointment`
- **方法**: GET/POST
- **请求参数**:
  ```
  appointmentId: number    // 预约ID
  ```
- **响应参数**:
  ```json
  {
    "code": 200,
    "msg": "完成挂号预约成功"
  }
  ```

### 删除预约信息

- **URL**: `/deleteAppointment`
- **方法**: GET/POST
- **请求参数**:
  ```
  appointmentId: number    // 预约ID
  ```
- **响应参数**:
  ```json
  {
    "code": 200,
    "msg": "删除挂号预约成功"
  }
  ```

### 获取单个挂号预约信息

- **URL**: `/getSickAppointment`
- **方法**: GET
- **请求参数**:
  ```
  appointmentId: number    // 预约ID
  userId: number           // 用户ID
  ```
- **响应参数**:
  ```json
  {
    "code": 200,
    "appointmentId": "number",
    "patientId": "number",
    "doctorId": "number",
    "patientName": "string",
    "doctorName": "string",
    "hospitalName": "string",
    "department": "string",
    "remark": "string",
    "createTime": "number",
    "isCompleted": "boolean"
  }
  ```

### 获取用户所有挂号预约信息

- **URL**: `/getAppointmentsList`
- **方法**: GET
- **请求参数**:
  ```
  userId: number          // 用户ID
  ```
- **响应参数**:
  ```json
  [
    {
      "appointmentId": "number",
      "patientId": "number",
      "doctorId": "number",
      "patientName": "string",
      "doctorName": "string",
      "hospitalName": "string",
      "department": "string",
      "remark": "string",
      "createTime": "number",
      "isCompleted": "boolean"
    }
  ]
  ```

## 医疗记录管理

### 创建病历

- **URL**: `/createMedicalRecord`
- **方法**: POST
- **请求体**:
  ```json
  {
    "patientName": "string",
    "doctorName": "string",
    "hospitalName": "string",
    "department": "string",
    "registrationInfo": "string",
    "pastMedicalHistory": "string",
    "currentMedicalHistory": "string",
    "createdAt": "timestamp"
  }
  ```
- **响应参数**:
  ```json
  {
    "code": 200,
    "msg": "病历添加成功"
  }
  ```

### 更新病历信息

- **URL**: `/updateMedicalRecord`
- **方法**: POST
- **请求体**:
  ```json
  {
    "id": "number",
    "hospitalName": "string",
    "department": "string",
    "registrationInfo": "string",
    "pastMedicalHistory": "string",
    "currentMedicalHistory": "string",
    "createdAt": "timestamp"
  }
  ```
- **响应参数**:
  ```json
  {
    "code": 200,
    "msg": "病历信息更新成功"
  }
  ```

### 完成病历信息

- **URL**: `/completeMedicalRecord`
- **方法**: GET/POST
- **请求参数**:
  ```
  recordId: number        // 病历ID
  ```
- **响应参数**:
  ```json
  {
    "code": 200,
    "msg": "病历信息已完成"
  }
  ```

### 删除病历信息

- **URL**: `/deleteMedicalRecord`
- **方法**: GET/POST
- **请求参数**:
  ```
  recordId: number        // 病历ID
  ```
- **响应参数**:
  ```json
  {
    "code": 200,
    "msg": "病历信息删除成功"
  }
  ```

### 获取用户所有病历信息

- **URL**: `/getMedicalRecordList`
- **方法**: GET
- **请求参数**:
  ```
  userId: number          // 用户ID
  ```
- **响应参数**:
  ```json
  [
    {
      "recordId": "number",
      "patientId": "number",
      "doctorId": "number",
      "hospitalName": "string",
      "department": "string",
      "registrationInfo": "string",
      "pastMedicalHistory": "string", 
      "currentMedicalHistory": "string",
      "createTime": "number",
      "isFilled": "boolean"
    }
  ]
  ```

### 获取单个病历信息

- **URL**: `/getMedicalRecordByIdentityNumber`
- **方法**: GET
- **请求参数**:
  ```
  recordId: number        // 病历ID
  userId: number          // 用户ID
  ```
- **响应参数**:
  ```json
  {
    "code": 200,
    "msg": "病历查询成功!",
    "recordId": "number",
    "patientId": "number",
    "doctorId": "number",
    "hospitalName": "string",
    "department": "string",
    "registrationInfo": "string",
    "pastMedicalHistory": "string",
    "currentMedicalHistory": "string",
    "createTime": "number",
    "isFilled": "boolean"
  }
  ```

### 获取所有患者列表

- **URL**: `/getPatientsList`
- **方法**: GET
- **响应参数**:
  ```json
  [
    {
      "userId": "number",
      "name": "string",
      "gender": "string",
      "age": "number"
    }
  ]
  ```

## 系统统计信息

### 获取区块链信息

- **URL**: `/blockchainInfo`
- **方法**: GET
- **响应参数**:
  ```json
  {
    "blockNumber": "number",
    "transactionCount": "number"
  }
  ```

### 获取用户所有信息统计

- **URL**: `/getAll`
- **方法**: GET
- **请求参数**:
  ```
  userId: number         // 用户ID
  ```
- **响应参数**:
  ```json
  {
    "appointment": "number",
    "medicalRecord": "number"
  }
  ```

## 跨域支持

所有接口均支持跨域访问(CORS)，前端开发时可以直接调用这些接口而无需担心跨域问题。

## 数据格式

- 对于POST请求，请求体必须是JSON格式
- 所有响应均为JSON格式
- 时间戳格式为毫秒级UNIX时间戳

### 时间格式化处理

为解决时间显示乱码问题，请确保：

1. 发送时间数据时使用ISO 8601格式：`YYYY-MM-DDTHH:mm:ss.sssZ`
2. 前端渲染时使用以下格式化函数：

```javascript
function formatDate(dateStr) {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`;
}
```

## 错误处理

当请求发生错误时，API将返回以下格式的响应：

```json
{
  "code": 400,
  "msg": "错误信息说明"
}
```
