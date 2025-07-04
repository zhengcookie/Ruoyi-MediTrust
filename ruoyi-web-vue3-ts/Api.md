
# 医疗系统API文档

## 基础信息
- 基础URL: `http://localhost:8085`
- 响应格式: 所有API返回JSON格式，标准响应结构如下:
```json
{
  "code": 200,     // 状态码：200成功，其他表示失败
  "msg": "消息",   // 响应消息
  "data": {}       // 响应数据，可选
}
```

## 1. 用户管理API

### 1.1 用户注册
- **URL**: `/user/register`
- **方法**: POST
- **请求体**:
```json
{
  "name": "用户名",
  "gender": "性别",
  "age": 年龄,
  "password": "密码",
  "userType": "用户类型" // "1"表示患者，"2"表示医生
}
```
- **响应**: 标准响应格式

### 1.2 用户登录
- **URL**: `/user/login`
- **方法**: POST
- **请求体**:
```json
{
  "name": "用户名",
  "password": "密码",
  "userType": "用户类型" // "1"表示患者，"2"表示医生
}
```
- **响应**: 包含用户信息的标准响应

### 1.3 获取所有医生
- **URL**: `/user/doctors`
- **方法**: GET
- **响应**: 包含医生列表的标准响应

### 1.4 获取所有患者
- **URL**: `/user/patients`
- **方法**: GET
- **响应**: 包含患者列表的标准响应

### 1.5 获取用户详情
- **URL**: `/user/details/{userId}`
- **方法**: GET
- **路径参数**: `userId` - 用户ID
- **响应**: 包含用户详情的标准响应

### 1.6 通过姓名获取用户ID
- **URL**: `/user/id`
- **方法**: GET
- **查询参数**: `name` - 用户姓名
- **响应**: 包含用户ID的标准响应

### 1.7 更新用户信息
- **URL**: `/user/update/{userId}`
- **方法**: PUT
- **路径参数**: `userId` - 用户ID
- **请求体**:
```json
{
  "name": "用户名",
  "gender": "性别",
  "age": 年龄
}
```
- **响应**: 标准响应格式

## 2. 预约管理API

### 2.1 创建预约
- **URL**: `/appointment/create`
- **方法**: POST
- **请求体**:
```json
{
  "patientName": "患者姓名",
  "doctorName": "医生姓名",
  "hospitalName": "医院名称",
  "department": "科室",
  "remark": "备注",
  "createTime": "创建时间"
}
```
- **响应**: 标准响应格式

### 2.2 完成预约
- **URL**: `/appointment/complete/{appointmentId}`
- **方法**: POST
- **路径参数**: `appointmentId` - 预约ID
- **响应**: 标准响应格式

### 2.3 检查预约是否完成
- **URL**: `/appointment/isCompleted/{appointmentId}`
- **方法**: GET
- **路径参数**: `appointmentId` - 预约ID
- **响应**: 包含完成状态(boolean)的标准响应

### 2.4 更新预约
- **URL**: `/appointment/update/{appointmentId}`
- **方法**: PUT
- **路径参数**: `appointmentId` - 预约ID
- **请求体**:
```json
{
  "hospitalName": "医院名称",
  "department": "科室",
  "remark": "备注",
  "createTime": "创建时间"
}
```
- **响应**: 标准响应格式

### 2.5 获取预约详情
- **URL**: `/appointment/details/{appointmentId}`
- **方法**: GET
- **路径参数**: `appointmentId` - 预约ID
- **查询参数**: `userId` - 用户ID
- **响应**: 包含预约详情的标准响应

### 2.6 获取用户关联的所有预约
- **URL**: `/appointment/list/{userId}`
- **方法**: GET
- **路径参数**: `userId` - 用户ID
- **响应**: 包含预约列表的标准响应

### 2.7 删除预约
- **URL**: `/appointment/delete/{appointmentId}`
- **方法**: DELETE
- **路径参数**: `appointmentId` - 预约ID
- **响应**: 标准响应格式

## 3. 病历管理API

### 3.1 创建病历
- **URL**: `/medicalRecord/create`
- **方法**: POST
- **请求体**:
```json
{
  "patientName": "患者姓名",
  "doctorName": "医生姓名",
  "hospitalName": "医院名称",
  "department": "科室",
  "registrationInfo": "挂号信息",
  "pastMedicalHistory": "既往病史",
  "currentMedicalHistory": "现病史",
  "createTime": "创建时间"
}
```
- **响应**: 标准响应格式

### 3.2 完成病历填写
- **URL**: `/medicalRecord/complete/{recordId}`
- **方法**: POST
- **路径参数**: `recordId` - 病历ID
- **响应**: 标准响应格式

### 3.3 检查病历是否填写完成
- **URL**: `/medicalRecord/isComplete/{recordId}`
- **方法**: GET
- **路径参数**: `recordId` - 病历ID
- **响应**: 包含完成状态(boolean)的标准响应

### 3.4 更新病历
- **URL**: `/medicalRecord/update/{recordId}`
- **方法**: PUT
- **路径参数**: `recordId` - 病历ID
- **请求体**:
```json
{
  "hospitalName": "医院名称",
  "department": "科室",
  "registrationInfo": "挂号信息",
  "pastMedicalHistory": "既往病史",
  "currentMedicalHistory": "现病史",
  "createTime": "创建时间"
}
```
- **响应**: 标准响应格式

### 3.5 删除病历
- **URL**: `/medicalRecord/delete/{recordId}`
- **方法**: DELETE
- **路径参数**: `recordId` - 病历ID
- **响应**: 标准响应格式

### 3.6 获取病历详情
- **URL**: `/medicalRecord/details/{recordId}`
- **方法**: GET
- **路径参数**: `recordId` - 病历ID
- **查询参数**: `userId` - 用户ID
- **响应**: 包含病历详情的标准响应

### 3.7 获取用户关联的所有病历
- **URL**: `/medicalRecord/list/{userId}`
- **方法**: GET
- **路径参数**: `userId` - 用户ID
- **响应**: 包含病历列表的标准响应
