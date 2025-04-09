<template>
    <div class="reserve-info">

        <h2 class="page-title">挂号预约</h2>
        <div class="search-section">
            <div class="search-input">
                <el-input v-model="searchText" placeholder="请输入挂号预约编号">
                </el-input>
                <el-button type="primary" @click="handleSearch" class="search-button">搜索挂号预约</el-button>
                <el-button type="primary" @click="showRegisterDialog" class="search-button">
                    <i class="el-icon-plus"></i> 我要挂号
                </el-button>
            </div>
        </div>



        <el-table :data="tableData" border style="width: 100%">
            <el-table-column prop="appointmentId" label="挂号预约编号" />
            <el-table-column prop="patientName" label="患者姓名" />
            <el-table-column prop="createTime" label="挂号预约时间" />
            <el-table-column prop="doctorName" label="医生姓名" />
            <el-table-column prop="hospitalName" label="医院名称" />
            <el-table-column prop="department" label="挂号预约科室" />
            <el-table-column prop="status" label="挂号预约状态">
                <template #default="scope">
                    <el-tag :type="scope.row.status === 'true' ? 'success' : 'warning'">
                        {{ scope.row.status === 'true' ? '已完成' : '未完成' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="remark" label="病情描述" />
            <el-table-column label="操作" fixed="right" width="300">
                <template #default="scope">
                    <el-button type="primary" size="small" @click="showModalDialog(scope.row)">挂号预约详情</el-button>
                    <el-button type="danger" size="small" @click="cancelAppointment(scope.row)"
                        :disabled="scope.row.status === 'true'">取消挂号预约</el-button>
                </template>
            </el-table-column>
        </el-table>

        <div class="pagination-container">
            <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 15, 20]"
                :total="total" layout="total, sizes, prev, pager, next" @size-change="handleSizeChange"
                @current-change="handleCurrentChange" />
        </div>

        <el-dialog v-model="dialogVisible" title="挂号预约详情" width="50%" :before-close="handleClose">
            <div class="appointment-details" v-loading="loading">
                <h2 class="hospital-title">{{ patientInfo.hospitalName }}</h2>
                <h3 class="record-type">挂号预约信息单</h3>
                <el-descriptions :column="2" border>
                    <el-descriptions-item label="编号">{{ patientInfo.appointmentId }}</el-descriptions-item>
                    <el-descriptions-item label="患者姓名">{{ patientInfo.patientName }}</el-descriptions-item>
                    <el-descriptions-item label="挂号预约医生">{{ patientInfo.doctorName }}</el-descriptions-item>
                    <el-descriptions-item label="医院名称">{{ patientInfo.hospitalName }}</el-descriptions-item>
                    <el-descriptions-item label="挂号预约科室">{{ patientInfo.department }}</el-descriptions-item>
                    <el-descriptions-item label="挂号预约时间">{{ patientInfo.createTime }}</el-descriptions-item>

                    <el-descriptions-item label="挂号预约状态">{{ patientInfo.status === 'true' ? '已完成' : '未完成'
                        }}</el-descriptions-item>
                </el-descriptions>

                <el-card class="record-content">
                    <el-descriptions direction="vertical" :column="1" border>
                        <el-descriptions-item label="病情描述">{{ patientInfo.remark }}</el-descriptions-item>
                    </el-descriptions>
                </el-card>

                <div class="print-time">
                    打印时间: {{ new Date().toLocaleDateString() }}
                </div>
            </div>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="handleClose">关闭</el-button>
                    <el-button type="primary" @click="printAppointment">打印挂号预约单</el-button>
                </span>
            </template>
        </el-dialog>

        <el-dialog v-model="registerDialogVisible" title="挂号挂号预约" width="60%">
            <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px" class="form-container">
                <el-card class="form-card" shadow="hover">
                    <div class="form-row">
                        <el-form-item label="医院名称：" required prop="hospitalName">
                            <el-input v-model="formData.hospitalName" placeholder="请输入医院 " class="form-select">
                            </el-input>
                        </el-form-item>
                        <el-form-item label="挂号科室：" required prop="department">
                            <el-input v-model="formData.department" placeholder="请输入科室" class="form-select">
                            </el-input>
                        </el-form-item>

                        <el-form-item label="医生姓名：" required prop="doctorName">
                            <el-input v-model="formData.doctorName" placeholder="请输入医生姓名" class="form-select">
                            </el-input>
                        </el-form-item>
                    </div>

                    <div class="form-row">
                        <el-form-item label="医生地址：" required prop="doctor">
                            <el-input v-model="formData.doctor" placeholder="请输入医生区块链地址" class="form-select">
                            </el-input>
                        
                        </el-form-item>

                        <el-form-item label="挂号时间：" required prop="createTime">
                            <el-date-picker v-model="formData.createTime" type="datetime" placeholder="选择日期时间"
                                class="form-date-picker" :disabled-date="disabledDate" :disabled-time="disabledTime">
                            </el-date-picker>
                        </el-form-item>
                    </div>

                    <div class="form-row">
                        <el-form-item label="病情描述：" prop="remark">
                            <el-input v-model="formData.remark" type="textarea" placeholder="请输入病情描述（选填）" :rows="3"
                                class="form-textarea">
                            </el-input>
                        </el-form-item>
                    </div>
                </el-card>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="registerDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="handleRegister">确认挂号</el-button>
                </span>
            </template>
        </el-dialog>

        <el-dialog v-model="confirmDialogVisible" title="挂号预约信息确认" width="50%" :before-close="handleConfirmClose">
            <el-descriptions :column="1" border>
                <el-descriptions-item label="病人地址">{{ formData.patientName }}</el-descriptions-item>
                <el-descriptions-item label="医院名称">{{ formData.hospitalName }}</el-descriptions-item>
                <el-descriptions-item label="科室">{{ formData.department }}</el-descriptions-item>
                <el-descriptions-item label="医生">{{ formData.doctorName }}</el-descriptions-item>
                <el-descriptions-item label="挂号预约时间">{{ formData.createTime }}</el-descriptions-item>
                <el-descriptions-item label="病情描述">{{ formData.remark }}</el-descriptions-item>
            </el-descriptions>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="confirmDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="confirmAppointment">确认挂号预约</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import axios from 'axios';
import { onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import type { FormInstance } from 'element-plus';

const searchText = ref('');
const dialogVisible = ref(false);
const registerDialogVisible = ref(false);
const confirmDialogVisible = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const loading = ref(false);
const tableData = ref<any[]>([]);
const formRef = ref<FormInstance>();

const accountAddress = localStorage.getItem("accountAddress") || "";
const name = localStorage.getItem("name") || "";

const formData = reactive({
    patientName: name,
    patient: accountAddress,
    doctorName: '',
    doctor: '',
    hospitalName: '',
    department: '',
    remark: '',
    createTime: '',
    status: false
});

// 添加区块链地址验证规则
const validateBlockchainAddress = (rule: any, value: string, callback: any) => {
    // 如果值不存在，直接返回错误
    if (!value) {
        callback(new Error(rule.message || '请输入区块链地址'));
        return;
    }
    
    // 验证地址格式
    const addressRegex = /^0x[a-fA-F0-9]{40}$/;
    if (!addressRegex.test(value)) {
        callback(new Error(rule.message || '请输入正确的0x开头的42位十六进制地址'));
        return;
    }
    
    // 验证通过
    callback();
};

// 表单验证规则
const rules = {
    hospitalName: [
        { required: true, message: '请输入医院名称', trigger: 'blur' }
    ],
    department: [
        { required: true, message: '请输入科室', trigger: 'blur' }
    ],
    doctorName: [
        { required: true, message: '请输入医生姓名', trigger: 'blur' }
    ],
    doctor: [
        { required: true, message: '请输入医生区块链地址', trigger: 'blur' },
        { 
            validator: validateBlockchainAddress, 
            message: '请输入正确的医生区块链地址格式',
            trigger: 'blur' 
        }
    ],
    createTime: [
        { required: true, message: '请选择挂号时间', trigger: 'change' }
    ]
};

const patientInfo = reactive({
    patientName: "",
    appointmentId: "",
    hospitalName: "",
    department: "",
    doctorName: "",
    remark: "",
    createTime: "",
    status: "",
});

const handleSearch = () => {
    if (searchText.value == "") {
        ElMessage.error('请输入挂号预约编号');
        return;
    }
    SearchAppointment();
};

const SearchAppointment = async () => {
    try {
        const response = await axios.get('http://localhost:8085/getSickAppointment', {
            params: {
                appointmentId: searchText.value,
                address: accountAddress
            }
        });

        if (response.data.code == 200) {
            console.log(response.data.patientName);
            console.log(response.data.patient);
            console.log(accountAddress);
            patientInfo.patientName = response.data.patientName;
            patientInfo.appointmentId = response.data.appointmentId;
            patientInfo.department = response.data.department;
            patientInfo.hospitalName = response.data.hospitalName;
            patientInfo.doctorName = response.data.doctorName;
            patientInfo.createTime = new Date(response.data.time).toLocaleString();
            patientInfo.status = response.data.status;
            patientInfo.remark = response.data.remark;
            dialogVisible.value = true;
        } else {
            ElMessage.error('查询失败');
        }
    } catch (error) {
        ElMessage.error('查询失败');
    }
};

const handleSizeChange = (val: number) => {
    pageSize.value = val;
    getAppointment();
};

const handleCurrentChange = (val: number) => {
    currentPage.value = val;
    getAppointment();
};

const showModalDialog = (row: any) => {
    dialogVisible.value = true;
    loading.value = true;
    Object.assign(patientInfo, row);
    loading.value = false;
};


const cancelAppointment = (row: any) => {
    // alert("暂未开通取消挂号预约服务");
    ElMessageBox.confirm('确定要取消该挂号预约吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            const response = await axios.post(`http://localhost:8085/deleteAppointment?appointmentId=${row.appointmentId}`);
            if (response.data.code == 200) {
                ElMessage.success('取消成功');
                getAppointment();
            } else {
                ElMessage.error('取消失败');
            }
        } catch (error) {
            ElMessage.error('取消失败');
        }
    })
};

const printAppointment = () => {
    window.print();
};

const handleClose = () => {
    dialogVisible.value = false;
    Object.assign(patientInfo, {
        doctor: "",
        department: "",
        createTime: "",
        status: "",
        mainComplaint: "",
        remark: ""
    });
};

const getAppointment = async () => {
    try {
        const response = await axios.get('http://localhost:8085/getAppointmentsList', {
            params: {
                address: accountAddress,
                page: currentPage.value,
                pageSize: pageSize.value
            }
        });

        // 直接使用接口返回的数据
        const parsedData = response.data.map((item: any) => {
            if (item.code == 200) {
                return {
                    ...item,
                    createTime: new Date(item.time).toLocaleString(),
                    status: item.status.toString()
                }
            }
        });

        tableData.value = parsedData;
        total.value = response.data.total || parsedData.length;
    } catch (error) {
        console.error("Error during API call:", error);
        ElMessage.error('获取挂号预约列表失败');
    }
};

const showRegisterDialog = () => {
    registerDialogVisible.value = true;
};

const disabledDate = (time: Date) => {
    return time.getTime() < Date.now() - 8.64e7;
};

const disabledTime = (date: Date) => {
    const now = new Date();
    if (date.getDate() === now.getDate() &&
        date.getMonth() === now.getMonth() &&
        date.getFullYear() === now.getFullYear()) {
        return {
            hours: Array.from({ length: now.getHours() }, (_, i) => i),
            minutes: date.getHours() === now.getHours() ? Array.from({ length: now.getMinutes() }, (_, i) => i) : []
        };
    }
    return {
        hours: [],
        minutes: []
    };
};

// 修改挂号函数，增加表单验证
const handleRegister = async () => {
    if (!formRef.value) return;
    
    formRef.value.validate((valid) => {
        if (valid) {
            // 额外验证医生地址格式
            if (!/^0x[a-fA-F0-9]{40}$/.test(formData.doctor)) {
                ElMessage.error('请输入正确的医生区块链地址格式');
                return;
            }
            
            confirmDialogVisible.value = true;
            registerDialogVisible.value = false;
        } else {
            ElMessage.warning('请填写完整挂号预约信息并确保格式正确');
        }
    });
};

const handleConfirmClose = () => {
    confirmDialogVisible.value = false;
    registerDialogVisible.value = true;
};

// 修改确认预约函数，处理正确的时间戳格式
const confirmAppointment = async () => {
    if (!formRef.value) return;
    
    // 验证医生区块链地址格式
    if (!/^0x[a-fA-F0-9]{40}$/.test(formData.doctor)) {
        ElMessage.error('请输入正确的医生区块链地址格式');
        return;
    }
    
    // 验证病人区块链地址格式
    if (!/^0x[a-fA-F0-9]{40}$/.test(formData.patient)) {
        ElMessage.error('病人区块链地址格式不正确，请检查您的账户');
        return;
    }
    
    await formRef.value.validate((valid) => {
        if (valid) {
            // 确保 createTime 是 Timestamp 格式
            const timestamp = new Date(formData.createTime).getTime();
                
            // 构建请求数据，将日期转换为时间戳
            const appointmentData = {
                patientName: formData.patientName,
                patient: formData.patient,
                doctorName: formData.doctorName,
                doctor: formData.doctor,
                hospitalName: formData.hospitalName,
                department: formData.department,
               
                remark: formData.remark,
                createdAt: timestamp // 使用时间戳格式，并确认字段名称正确
            };

            axios.post("http://localhost:8085/createAppointment", appointmentData).then((res) => {
                if (res.data.code != 200) {
                    ElMessage.error("挂号预约失败！");
                }
                else {
                    ElMessage.success("挂号预约成功");
                    confirmDialogVisible.value = false;
                    getAppointment(); // 刷新挂号预约列表
                }
            });
        }
    });
};

onMounted(() => {
    getAppointment();
});

</script>

<style scoped>
.reserve-info {
    padding: 20px;
    background: linear-gradient(135deg, #ffffff 0%, #f8faff 100%);
    border-radius: 15px;
    position: relative;
    overflow: hidden;
}

/* 修改装饰性背景元素 */
.reserve-info::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, #4a9e5c, #2196F3);
    border-radius: 3px;
}



@keyframes gentleFloat {

    0%,
    100% {
        transform: translate(0, 0) rotate(0deg);
    }

    25% {
        transform: translate(10px, -10px) rotate(1deg);
    }

    50% {
        transform: translate(0, -15px) rotate(0deg);
    }

    75% {
        transform: translate(-10px, -5px) rotate(-1deg);
    }
}





.page-title {
    font-size: 24px;
    color: #2c3e50;
    margin-bottom: 25px;
    font-weight: 600;
    display: flex;
    align-items: center;
    position: relative;
    padding-left: 15px;
}

.page-title::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 4px;
    height: 24px;
    background: linear-gradient(180deg, #4a9e5c, #2196F3);
    border-radius: 2px;
}

/* 搜索区域样式优化 */
.search-section {
    display: flex;
    align-items: center;
    margin-bottom: 25px;
    padding: 20px;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 16px;
    position: relative;
    border: 1px solid rgba(33, 150, 243, 0.1);
    box-shadow: 0 4px 20px rgba(33, 150, 243, 0.05);
    backdrop-filter: blur(10px);
}

/* 科技感装饰线条 */
/* .search-section::before,
.search-section::after {
    content: '';
    position: absolute;
    left: 0;
    width: 100%;
    height: 2px;
    background: linear-gradient(90deg,
        transparent,
        rgba(74, 158, 92, 0.2),
        rgba(33, 150, 243, 0.2),
        transparent
    );
} */

.search-section::before {
    top: 0;
}

.search-section::after {
    bottom: 0;
}

.search-label {
    margin-right: 15px;
    font-size: 15px;
    font-weight: 500;
    color: #2c3e50;
    position: relative;
    padding-left: 12px;
}

.search-label::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 3px;
    height: 16px;
    background: linear-gradient(180deg, #4a9e5c, #2196F3);
    border-radius: 2px;
}

.search-input {
    display: flex;
    gap: 12px;
    flex: 1;
}

:deep(.el-input__wrapper) {
    background: rgba(255, 255, 255, 0.95);
    border-radius: 10px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.03) !important;
    transition: all 0.3s ease;
    border: 1px solid rgba(74, 158, 92, 0.1);
}

:deep(.el-input__wrapper:hover) {
    box-shadow: 0 4px 15px rgba(74, 158, 92, 0.08) !important;
    transform: translateY(-1px);
}

:deep(.el-input__wrapper.is-focus) {
    box-shadow: 0 0 0 1px rgba(74, 158, 92, 0.3) !important;
    border-color: rgba(74, 158, 92, 0.5);
}

/* 按钮样式优化 */
.el-button {
    border-radius: 10px;
    padding: 12px 24px;
    font-weight: 500;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
}

.el-button--primary {
    background: linear-gradient(135deg, #4a9e5c 0%, #2196F3 100%);
    border: none;
    color: white;
}

.el-button--primary:hover {
    transform: translateY(-2px);
    box-shadow: 0 5px 15px rgba(74, 158, 92, 0.2);
}

.el-button--primary::after {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: linear-gradient(45deg,
            transparent,
            rgba(255, 255, 255, 0.1),
            transparent);
    transform: rotate(45deg);
    animation: shine 3s infinite;
}

.search-button {
    padding: 12px 25px;
    border-radius: 8px;
    font-size: 15px;
    font-weight: 500;
    transition: all 0.3s ease;
    background: linear-gradient(135deg, #4a9e5c 0%, #2196F3 100%);
    border: none;
    color: white;
    position: relative;
    overflow: hidden;
    /* height: 100%; */
}

.search-button:hover {
    transform: translateY(-2px);
    box-shadow: 0 5px 15px rgba(74, 158, 92, 0.3);
    /* height: 100%; */
}

.search-button::after {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.1), transparent);
    transform: rotate(45deg);
    animation: shine 3s infinite;
}

@keyframes shine {
    0% {
        transform: translateX(-100%) rotate(45deg);
    }

    100% {
        transform: translateX(100%) rotate(45deg);
    }
}

/* 表格样式优化 */
:deep(.el-table) {
    background: transparent;
    border-radius: 16px;
    overflow: hidden;
    margin-top: 20px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
}

:deep(.el-table__header) {
    background: linear-gradient(90deg,
            rgba(74, 158, 92, 0.05),
            rgba(33, 150, 243, 0.05));
}

:deep(.el-table__header-wrapper th) {
    background: transparent;
    color: #2c3e50;
    font-weight: 600;
    border-bottom: 2px solid rgba(74, 158, 92, 0.1);
    padding: 16px 0;
}

:deep(.el-table__row) {
    background: rgba(255, 255, 255, 0.8);
    transition: all 0.3s ease;
}

:deep(.el-table__row:hover) {
    background: rgba(255, 255, 255, 0.95);
    transform: translateY(-2px);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.03);
}

:deep(.el-table__cell) {
    border-bottom: 1px solid rgba(74, 158, 92, 0.05);
}

/* 状态标签样式优化 */
:deep(.el-tag) {
    border-radius: 6px;
    padding: 6px 12px;
    font-weight: 500;
    border: none;
}

:deep(.el-tag--success) {
    background: rgba(74, 158, 92, 0.1);
    color: #4a9e5c;
}

:deep(.el-tag--warning) {
    background: rgba(230, 162, 60, 0.1);
    color: #e6a23c;
}

/* 分页样式优化 */
.pagination-container {
    margin-top: 25px;
    padding: 20px;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 16px;
    display: flex;
    justify-content: flex-end;
    align-items: center;
    border: 1px solid rgba(33, 150, 243, 0.1);
}

:deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
    background: linear-gradient(135deg, #4a9e5c 0%, #2196F3 100%);
    color: white;
    font-weight: 600;
}

:deep(.el-pagination.is-background .el-pager li:not(.is-disabled):hover) {
    color: #4a9e5c;
}

/* 对话框样式优化 */
:deep(.el-dialog) {
    border-radius: 20px;
    overflow: hidden;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
    background: rgba(255, 255, 255, 0.95);
}

:deep(.el-dialog__header) {
    background: linear-gradient(90deg,
            rgba(74, 158, 92, 0.05),
            rgba(33, 150, 243, 0.05));
    padding: 20px;
    margin: 0;
    border-bottom: 1px solid rgba(74, 158, 92, 0.1);
}

:deep(.el-dialog__title) {
    color: #2c3e50;
    font-weight: 600;
    font-size: 18px;
    background: linear-gradient(135deg, #4a9e5c, #2196F3);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
}

:deep(.el-dialog__body) {
    padding: 30px;
}

:deep(.el-dialog__footer) {
    border-top: 1px solid rgba(74, 158, 92, 0.1);
    padding: 15px 20px;
    background: rgba(255, 255, 255, 0.9);
}

/* 挂号预约详情样式优化 */
.appointment-details {
    padding: 25px;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 16px;
}

.hospital-title {
    text-align: center;
    font-size: 24px;
    margin-bottom: 15px;
    color: #2c3e50;
    font-weight: 600;
    position: relative;
    padding-bottom: 15px;
}

.hospital-title::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 60px;
    height: 3px;
    background: linear-gradient(90deg, #4a9e5c, #2196F3);
    border-radius: 3px;
}

.record-type {
    text-align: center;
    font-size: 20px;
    margin-bottom: 25px;
    color: #606266;
}

.record-content {
    margin: 25px 0;
    border-radius: 12px;
    border: 1px solid rgba(74, 158, 92, 0.1);
}

.print-time {
    text-align: right;
    margin-top: 25px;
    color: #909399;
    font-size: 14px;
}

/* 表单样式优化 */
.form-container {
    padding: 20px;
}

.form-card {
    background: rgba(255, 255, 255, 0.9);
    border-radius: 16px;
    padding: 25px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
    border: 1px solid rgba(74, 158, 92, 0.1);
}

.form-row {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
    margin-bottom: 20px;
}

:deep(.el-form-item__label) {
    font-weight: 500;
    color: #2c3e50;
}

:deep(.el-date-editor.el-input__wrapper) {
    width: 100%;
}

/* 添加区块链地址格式提示样式 */
.form-tip {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
    line-height: 1.2;
}

/* 表单验证状态样式 */
:deep(.el-form-item.is-error .el-input__wrapper) {
    box-shadow: 0 0 0 1px #f56c6c !important;
}

:deep(.el-form-item.is-success .el-input__wrapper) {
    box-shadow: 0 0 0 1px #67c23a !important;
}
</style>