<template>
    <div class="medical-records">
        <h2 class="page-title">病历管理</h2>
        <div class="search-section">

            <el-input v-model="searchText" placeholder="请输入病历ID" class="search-input">
            </el-input>
            <el-button type="primary" @click="handleSearch" class="search-button">搜索病历</el-button>
            <el-button type="primary" @click="openFillInDialog" class="add-button">填写病历</el-button>
        </div>

        <el-table :data="tableData" border style="min-width: 100%">
            <el-table-column fixed prop="recordId" label="病历ID" width="120" />
            <el-table-column prop="patientName" label="患者姓名" width="120" />
            <el-table-column prop="hospitalName" label="医院名称" width="180" />
            <el-table-column prop="department" label="科室" width="120" />
            <el-table-column prop="doctorName" label="医生姓名" width="120" />
            <el-table-column prop="createTime" label="创建时间" width="120" />
            <el-table-column prop="isFilled" label="病历状态" width="100">
                <template #default="scope">
                    <el-tag :type="scope.row.isFilled ? 'success' : 'info'">
                        {{ scope.row.isFilled ? '已完成' : '未完成' }}
                    </el-tag>
                </template>
            </el-table-column>

            <el-table-column label="操作">
                <template #default="scope">
                    <el-button type="primary" @click="completeRecord(scope.row)">完成</el-button>
                    <el-button type="info" @click="EditRecord(scope.row)">编辑</el-button>
                    <el-button type="danger" @click="deleteRecord(scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <div class="pagination">
            <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                :page-sizes="[10, 20, 30, 50]" :total="total" layout="total, sizes, prev, pager, next"
                @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        </div>

        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="60%" @close="resetRecordForm">
            <el-form :model="recordForm" :rules="rules" ref="recordFormRef">
                <el-descriptions :column="2" border>
                    <el-descriptions-item label="病历编号" v-if="dialogTitle !== '填写病历'">
                        <el-input v-model="recordForm.recordId" :disabled="true" />
                    </el-descriptions-item>
                    <el-descriptions-item label="患者姓名">
                        <el-input v-model="recordForm.patientName" :disabled="viewOnly" />
                    </el-descriptions-item>
                    <el-descriptions-item label="患者地址">
                        <el-form-item prop="patient" class="no-margin">
                            <el-input v-model="recordForm.patient" :disabled="viewOnly" />
                           
                        </el-form-item>
                    </el-descriptions-item>
                    <el-descriptions-item label="医院名称">
                        <el-input v-model="recordForm.hospitalName" :disabled="viewOnly" />
                    </el-descriptions-item>
                    <el-descriptions-item label="科室">
                        <el-input v-model="recordForm.department" :disabled="viewOnly" />
                    </el-descriptions-item>
                    <el-descriptions-item label="医生姓名">
                        <el-input v-model="recordForm.doctorName" :disabled="viewOnly" />
                    </el-descriptions-item>
                    <el-descriptions-item label="医生地址">
                        <el-form-item prop="doctor" class="no-margin">
                            <el-input v-model="recordForm.doctor" :disabled="viewOnly" />
                            
                        </el-form-item>
                    </el-descriptions-item>
                    <el-descriptions-item label="挂号信息">
                        <el-input v-model="recordForm.registrationInfo" type="textarea" :rows="2" :disabled="viewOnly" />
                    </el-descriptions-item>
                    <el-descriptions-item label="既往病史">
                        <el-input v-model="recordForm.pastMedicalHistory" type="textarea" :rows="3" :disabled="viewOnly" />
                    </el-descriptions-item>
                    <el-descriptions-item label="现病史">
                        <el-input v-model="recordForm.currentMedicalHistory" type="textarea" :rows="3"
                            :disabled="viewOnly" />
                    </el-descriptions-item>
                    <el-descriptions-item label="状态">{{ recordForm.isFilled ? '已完成' : '未完成'
                        }}</el-descriptions-item>
                </el-descriptions>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="saveRecord">保存</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- <el-dialog v-model="infoDialogVisible" title="病历详情" width="60%">
            <div>
                <p><strong>病历ID:</strong> {{ medicalRecordDetails.recordId }}</p>
                <p><strong>患者姓名:</strong> {{ medicalRecordDetails.patientName }}</p>
                <p><strong>医院名称:</strong> {{ medicalRecordDetails.hospitalName }}</p>
                <p><strong>科室:</strong> {{ medicalRecordDetails.department }}</p>
                <p><strong>医生姓名:</strong> {{ medicalRecordDetails.doctorName }}</p>
                <p><strong>挂号信息:</strong> {{ medicalRecordDetails.registrationInfo }}</p>
                <p><strong>既往病史:</strong> {{ medicalRecordDetails.pastMedicalHistory }}</p>
                <p><strong>现病史:</strong> {{ medicalRecordDetails.currentMedicalHistory }}</p>
            </div>
            <template #footer>
                <el-button @click="infoDialogVisible = false">关闭</el-button>
            </template>
        </el-dialog> -->
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import type { FormInstance } from 'element-plus';

const searchText = ref('')
const dialogVisible = ref(false)
// const infoDialogVisible = ref(false)
const dialogTitle = ref('')
const viewOnly = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref<any[]>([])
// const medicalRecordDetails = reactive({
//     recordId: '',
//     patientName: '',
//     hospitalName: '',
//     department: '',
//     doctorName: '',
//     registrationInfo: '',
//     pastMedicalHistory: '',
//     currentMedicalHistory: '',

// })

// Define recordForm as a reactive object
const recordForm = reactive({
    recordId: '',
    patientName: '',
    patient: "",
    doctorName: '',
    doctor: "",
    hospitalName: '',
    department: '',
    registrationInfo: '',
    pastMedicalHistory: '',
    currentMedicalHistory: '',
    isFilled: false,
    createdAt: 0
});

const recordFormRef = ref<FormInstance>();

const handleSearch = async () => {
    const doctorAddress = localStorage.getItem("accountAddress")
    if (searchText.value == '') {
        ElMessage.error('请输入病历ID');
        return;
    }
    try {
        const response = await axios.get('http://localhost:8085/getMedicalRecordByIdentityNumber', {
            params: {
                recordId: searchText.value,
                address: doctorAddress,
                page: currentPage.value,
                pageSize: pageSize.value
            }
        })
        if (response.data.code == 200) {
            Object.assign(recordForm, response.data);
            viewOnly.value = response.data.isFilled; // Set viewOnly based on the searched record's isFilled
            dialogTitle.value = '病历详情';
            dialogVisible.value = true;
        }

    } catch (error) {
        // ElMessage.error('搜索失败');
    }
}

const handleSizeChange = (val: number) => {
    pageSize.value = val
    // handleSearch()
}

const handleCurrentChange = (val: number) => {
    currentPage.value = val
    // handleSearch()
}

const completeRecord = (row: any) => {
    ElMessageBox.confirm('确认完成此病历?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            const response = await axios.post(`http://localhost:8085/completeMedicalRecord?recordId=${row.recordId}`)
            ElMessage.success(response.data.msg)
            getMedicalRecord()
        } catch (error) {
            ElMessage.error('操作失败')
        }
    })
}

const openFillInDialog = () => {
    resetRecordForm()
    dialogTitle.value = '填写病历'
    viewOnly.value = false
    dialogVisible.value = true
}

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
    patient: [
        { required: true, message: '请输入患者区块链地址', trigger: 'blur' },
        { 
            validator: validateBlockchainAddress, 
            message: '请输入正确的患者区块链地址格式',
            trigger: 'blur' 
        }
    ],
    doctor: [
        { required: true, message: '请输入医生区块链地址', trigger: 'blur' },
        { 
            validator: validateBlockchainAddress, 
            message: '请输入正确的医生区块链地址格式',
            trigger: 'blur' 
        }
    ],
    patientName: [
        { required: true, message: '请输入患者姓名', trigger: 'blur' }
    ],
    doctorName: [
        { required: true, message: '请输入医生姓名', trigger: 'blur' }
    ],
    hospitalName: [
        { required: true, message: '请输入医院名称', trigger: 'blur' }
    ],
    department: [
        { required: true, message: '请输入科室', trigger: 'blur' }
    ]
};

const saveRecord = async () => {
    if (!recordFormRef.value) return;
    
    // 验证区块链地址格式
    if (!/^0x[a-fA-F0-9]{40}$/.test(recordForm.doctor)) {
        ElMessage.error('请输入正确的医生区块链地址格式');
        return;
    }
    
    if (!/^0x[a-fA-F0-9]{40}$/.test(recordForm.patient)) {
        ElMessage.error('请输入正确的患者区块链地址格式');
        return;
    }
    
    try {
        await recordFormRef.value.validate(async (valid) => {
            if (valid) {
                if (dialogTitle.value == "填写病历") {
                    recordForm.createdAt = Date.now(); // 设置创建时间戳
                    const response = await axios.post('http://localhost:8085/createMedicalRecord', {
                        patientName: recordForm.patientName,
                        patient: recordForm.patient,
                        doctorName: recordForm.doctorName,
                        doctor: recordForm.doctor,
                        hospitalName: recordForm.hospitalName,
                        department: recordForm.department,
                        registrationInfo: recordForm.registrationInfo,
                        pastMedicalHistory: recordForm.pastMedicalHistory,
                        currentMedicalHistory: recordForm.currentMedicalHistory,
                        createdAt: recordForm.createdAt
                    });
                    
                    if (response.data.code == 200) {
                        ElMessage.success(response.data.msg);
                        dialogVisible.value = false;
                        getMedicalRecord();
                        resetRecordForm();
                    } else {
                        ElMessage.error("病历填写失败");
                    }
                } else {
                    recordForm.createdAt = Date.now(); 
                    const response = await axios.post('http://localhost:8085/updateMedicalRecord', {
                        id: recordForm.recordId,
                        patientName: recordForm.patientName,
                        patient: recordForm.patient,
                        doctorName: recordForm.doctorName,
                        doctor: recordForm.doctor,
                        hospitalName: recordForm.hospitalName,
                        department: recordForm.department,
                        registrationInfo: recordForm.registrationInfo,
                        pastMedicalHistory: recordForm.pastMedicalHistory,
                        currentMedicalHistory: recordForm.currentMedicalHistory,
                        createdAt: recordForm.createdAt
                    });
                    if (response.data.code == 200){
                        ElMessage.success("病历详情保存成功")
                    }else{
                        ElMessage.error("病历详情保存失败")
                    }
                    
                    dialogVisible.value = false;
                    getMedicalRecord();
                    resetRecordForm();
                }
            } else {
                ElMessage.warning('请填写完整病历信息并确保格式正确');
            }
        });
    } catch (error) {
        ElMessage.error('填写病历失败');
    }
};

const EditRecord = (row: any) => {
    Object.assign(recordForm, row);
    viewOnly.value = row.isFilled; // Set viewOnly based on the record's isFilled
    dialogTitle.value = '病历详情';
    dialogVisible.value = true;
}
const deleteRecord = async (row: any) => {
    ElMessageBox.confirm('确认删除此病历?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        const response = await axios.post(`http://localhost:8085/deleteMedicalRecord?recordId=${row.recordId}`);
        if (response.data.code == 200) {
            ElMessage.success(response.data.msg)
            getMedicalRecord()
        } else {
            ElMessage.error(response.data.msg)
        }
    })
}

const formatDateTime = (dateTimeStr: string) => {
    const date = new Date(dateTimeStr)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const seconds = String(date.getSeconds()).padStart(2, '0')
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}
const getMedicalRecord = async () => {
    const doctorAddress = localStorage.getItem('accountAddress') || '{}';
    console.log(doctorAddress)

    const response = await axios.get('http://localhost:8085/getMedicalRecordList?address=' + doctorAddress)

    const parsedData = response.data.map((item: any) => (
        {
            ...item,
            createTime: formatDateTime(item.createTime)
        }
    ))

    tableData.value = parsedData;

    total.value = response.data.total || parsedData.length;


}

const resetRecordForm = () => {
    recordForm.patientName = '';
    recordForm.patient = '';
    recordForm.doctorName = '';
    recordForm.doctor = '';
    recordForm.hospitalName = '';
    recordForm.department = '';
    recordForm.registrationInfo = '';
    recordForm.pastMedicalHistory = '';
    recordForm.currentMedicalHistory = '';
    recordForm.createdAt = 0;
}

// onBeforeMount(() => {
//     getMedicalRecord()
// })
onMounted(() => {
    getMedicalRecord()
})
// 初始加载
// handleSearch()
</script>

<style scoped>
.medical-records {
    padding: 20px;
    background: linear-gradient(135deg, #ffffff 0%, #f8faff 100%);
    border-radius: 15px;
    position: relative;
    overflow: hidden;
}

.medical-records::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, #4a9e5c, #2196F3);
    border-radius: 3px;
}

/* 搜索区域样式 */
.search-section {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
    padding: 20px;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 12px;
    position: relative;
    border: 1px solid rgba(33, 150, 243, 0.1);
    box-shadow: 0 4px 20px rgba(149, 157, 165, 0.1);
}

/* 添加科技感装饰线条 */
/* .search-section::before {
    content: '';
    position: absolute;
    top: 0;
    left: 20px;
    width: 60px;
    height: 3px;
    background: linear-gradient(90deg, #2196F3, transparent);
}

.search-section::after {
    content: '';
    position: absolute;
    bottom: 0;
    right: 20px;
    width: 60px;
    height: 3px;
    background: linear-gradient(360deg, transparent, #2196F3);
} */
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

.search-label {
    margin-right: 15px;
    font-size: 15px;
    font-weight: 500;
    color: #2c3e50;
    position: relative;
    padding-left: 12px;
}

.search-input {
    display: flex;
    gap: 10px;
    flex: 1;
}

.add-button {
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
    margin-left: 10px;
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

.add-button:hover,
.search-button:hover {
    transform: translateY(-2px);
    box-shadow: 0 5px 15px rgba(74, 158, 92, 0.3);
    /* height: 100%; */
}

.add-button::after,
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

:deep(.el-input-group__append) {
    background: linear-gradient(135deg, #4a9e5c 0%, #2196F3 100%);
    border: none;
    padding: 0;
}

:deep(.el-input-group__append .el-button) {
    border: none;
    margin: 0;
    background: transparent;
    color: white;
    padding: 0 20px;
    height: 100%;
}

:deep(.el-input-group__append .el-button:hover) {
    background: rgba(255, 255, 255, 0.1);
}

:deep(.el-input__wrapper) {
    box-shadow: 0 4px 15px rgba(74, 158, 92, 0.08);
    width: 100%;
}

:deep(.el-input__inner) {
    height: 40px;
    transition: all 0.3s ease;
}

:deep(.el-input__inner:focus) {
    border-color: #4a9e5c;
    box-shadow: 0 0 8px rgba(74, 158, 92, 0.2);
}

:deep(.el-table) {
    background: transparent;
    border-radius: 12px;
    overflow: hidden;
}

:deep(.el-table__header) {
    background: linear-gradient(90deg, rgba(74, 158, 92, 0.1), rgba(33, 150, 243, 0.1));
}

:deep(.el-table__header-wrapper th) {
    background: transparent;
    color: #2c3e50;
    font-weight: 600;
    border-bottom: 2px solid rgba(74, 158, 92, 0.2);
}

:deep(.el-table__row) {
    background: rgba(255, 255, 255, 0.8);
    transition: all 0.3s ease;
}

:deep(.el-table__row:hover) {
    background: rgba(255, 255, 255, 0.95);
    transform: translateY(-2px);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
}

:deep(.el-table__cell) {
    border-bottom: 1px solid rgba(74, 158, 92, 0.1);
}

/* 按钮样式 */
:deep(.el-button) {
    border-radius: 6px;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
}

:deep(.el-button--primary) {
    background: linear-gradient(135deg, #4a9e5c 0%, #2196F3 100%);
    border: none;
}

:deep(.el-button--primary:hover) {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(74, 158, 92, 0.2);
}

/* 分页样式 */
.pagination {
    margin-top: 20px;
    padding: 15px;
    display: flex;
    justify-content: flex-end;
    background: linear-gradient(135deg, #ffffff 0%, #f8faff 100%);
    border-radius: 12px;
    box-shadow: 0 4px 15px rgba(74, 158, 92, 0.08);
}

/* 对话框样式 */
:deep(.el-dialog) {
    border-radius: 15px;
    overflow: hidden;
}

:deep(.el-dialog__header) {
    background: linear-gradient(135deg, #f8faff 0%, #f0f2f5 100%);
    padding: 20px;
    margin: 0;
    position: relative;
}

:deep(.el-dialog__header::before) {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, #4a9e5c, #2196F3);
}

/* 描述列表样式 */
:deep(.el-descriptions) {
    padding: 20px;
}

:deep(.el-descriptions__cell) {
    background: linear-gradient(135deg, #ffffff 0%, #f8faff 100%);
}

/* 添加科技感装饰元素 */
/* .medical-records::after {
    content: '';
    position: absolute;
    right: -50px;
    bottom: -50px;
    width: 200px;
    height: 200px;
    background: linear-gradient(45deg, rgba(74, 158, 92, 0.05), rgba(33, 150, 243, 0.05));
    clip-path: polygon(50% 0%, 100% 25%, 100% 75%, 50% 100%, 0% 75%, 0% 25%);
    z-index: 0;
} */

/* 标签样式 */
:deep(.el-tag) {
    border-radius: 4px;
}

:deep(.el-tag--success) {
    background: linear-gradient(135deg, #4a9e5c 0%, #81C784 100%);
    border: none;
    color: white;
}

:deep(.el-tag--info) {
    background: linear-gradient(135deg, #90A4AE 0%, #B0BEC5 100%);
    border: none;
    color: white;
}

.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}

/* 添加区块链地址格式提示样式 */
.form-tip {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
    line-height: 1.2;
}

/* 去除内嵌表单项的边距 */
.no-margin {
    margin: 0 !important;
}

/* 表单验证状态样式 */
:deep(.el-form-item.is-error .el-input__wrapper) {
    box-shadow: 0 0 0 1px #f56c6c !important;
}

:deep(.el-form-item.is-success .el-input__wrapper) {
    box-shadow: 0 0 0 1px #67c23a !important;
}
</style>