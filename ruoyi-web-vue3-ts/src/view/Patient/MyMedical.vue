<template>
    <div class="my-medical">

        <h2 class="page-title">我的病历</h2>
        <div class="search-section">
            <el-input v-model="searchText" placeholder="请输入病历ID" />
            <el-button type="primary" @click="handleSearch" class="search-btn">搜索病历</el-button>
        </div>
        <el-table :data="medicalRecords" border style="width: 100%">
            <el-table-column prop="recordId" label="病历ID" width="120" />
            <el-table-column prop="patientName" label="患者姓名" width="120" />
            <el-table-column prop="hospitalName" label="医院名称" width="180" />
            <el-table-column prop="department" label="科室" width="120" />
            <el-table-column prop="doctorName" label="医生姓名" width="120" />
            <el-table-column prop="isFilled" label="状态" width="100">
                <template #default="scope">
                    <el-tag :type="scope.row.isFilled ? 'success' : 'info'">
                        {{ scope.row.isFilled ? '已完成' : '未完成' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180" />
            <el-table-column label="操作" width="180">
                <template #default="scope">
                    <el-button type="primary" size="small" @click="viewRecord(scope.row)">查看</el-button>
                </template>
            </el-table-column>
        </el-table>
        <div class="pagination">
            <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[10, 20, 30, 50]"
                :total="total"
                layout="total, sizes, prev, pager, next"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
            />
        </div>
        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="60%">
            <el-descriptions :column="2" border>
                <el-descriptions-item label="病历编号">
                    <el-input v-model="selectedRecord.recordId" :disabled="viewOnly" />
                </el-descriptions-item>
                <el-descriptions-item label="患者姓名">
                    <el-input v-model="selectedRecord.patientName" :disabled="viewOnly" />
                </el-descriptions-item>
                <el-descriptions-item label="医院名称">
                    <el-input v-model="selectedRecord.hospitalName" :disabled="viewOnly" />
                </el-descriptions-item>
                <el-descriptions-item label="科室">
                    <el-input v-model="selectedRecord.department" :disabled="viewOnly" />
                </el-descriptions-item>
                <el-descriptions-item label="医生姓名">
                    <el-input v-model="selectedRecord.doctorName" :disabled="viewOnly" />
                </el-descriptions-item>
                <el-descriptions-item label="挂号信息">
                    <el-input v-model="selectedRecord.registrationInfo" type="textarea" :rows="2"
                        :disabled="viewOnly" />
                </el-descriptions-item>
                <el-descriptions-item label="既往病史">
                    <el-input v-model="selectedRecord.pastMedicalHistory" type="textarea" :rows="3"
                        :disabled="viewOnly" />
                </el-descriptions-item>
                <el-descriptions-item label="现病史">
                    <el-input v-model="selectedRecord.currentMedicalHistory" type="textarea" :rows="3"
                        :disabled="viewOnly" />
                </el-descriptions-item>
                <el-descriptions-item label="状态">{{ selectedRecord.isFilled ? '已完成' : '未完成' }}</el-descriptions-item>
            </el-descriptions>
            <template #footer>
                <el-button @click="dialogVisible = false">关闭</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { getMedicalRecordList, getMedicalRecordByIdentityNumber } from "../../api/medical";

interface MedicalRecord {
    recordId: number;
    patientName: string;
    hospitalName: string;
    department: string;
    doctorName: string;
    pastMedicalHistory: string;
    currentMedicalHistory: string;
    createTime: string;
    isFilled: boolean;
    registrationInfo: string;
}

const searchText = ref('')
const medicalRecords = ref<MedicalRecord[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('病历详情')
const selectedRecord = reactive({
    recordId: 0,
    patientName: '',
    hospitalName: '',
    department: '',
    doctorName: '',
    pastMedicalHistory: '',
    currentMedicalHistory: '',
    createTime: '',
    isFilled: false,
    registrationInfo: '',
})
const viewOnly = ref(true)
const userId = parseInt(localStorage.getItem('userId') || '0');

// 添加分页相关的响应式变量
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const formatDateTime = (timestamp: number): string => {
    if (!timestamp) return '';
    const date = new Date(timestamp);
    return date.toLocaleString() !== "Invalid Date" ? date.toLocaleString() : '';
};

const fetchMedicalRecords = async () => {
    try {
        const response = await getMedicalRecordList(userId);
        console.log('获取病历列表响应:', response); // 查看响应结构
        if (response) {
            medicalRecords.value = response.map((record: any) => ({
                ...record,
                createTime: formatDateTime(record.createTime)
            }));
            total.value = response.length || 0;
        }
    } catch (error) {
        console.error('获取病历失败', error);
    }
}

const handleSizeChange = (val: number) => {
    pageSize.value = val;
    fetchMedicalRecords();
}

const handleCurrentChange = (val: number) => {
    currentPage.value = val;
    fetchMedicalRecords();
}

const handleSearch = async () => {
    if (!searchText.value) {
        fetchMedicalRecords();
        return;
    }
    
    try {
        const recordId = parseInt(searchText.value);
        if (isNaN(recordId)) {
            console.error('病历ID必须是数字');
            return;
        }
        
        const response = await getMedicalRecordByIdentityNumber(recordId, userId);
        console.log('查询病历响应:', response); // 查看响应结构

        if (response) {
            // 重置选中记录对象，确保没有旧数据
            resetSelectedRecord();
            
            // 直接设置属性
            selectedRecord.recordId = response.recordId;
            selectedRecord.patientName = response.patientName || '未知';
            selectedRecord.doctorName = response.doctorName || '未知';
            selectedRecord.hospitalName = response.hospitalName || '';
            selectedRecord.department = response.department || '';
            selectedRecord.pastMedicalHistory = response.pastMedicalHistory || '';
            selectedRecord.currentMedicalHistory = response.currentMedicalHistory || '';
            selectedRecord.registrationInfo = response.registrationInfo || '';
            selectedRecord.createTime = formatDateTime(response.createTime);
            selectedRecord.isFilled = response.isFilled || false;
            
            // 更新单条数据的视图
            medicalRecords.value = [{
                recordId: response.recordId,
                patientName: response.patientName || '未知',
                doctorName: response.doctorName || '未知',
                hospitalName: response.hospitalName || '',
                department: response.department || '',
                pastMedicalHistory: response.pastMedicalHistory || '',
                currentMedicalHistory: response.currentMedicalHistory || '',
                registrationInfo: response.registrationInfo || '',
                createTime: formatDateTime(response.createTime),
                isFilled: response.isFilled || false
            }];
            total.value = 1;
            
            // 设置视图状态
            viewOnly.value = response.isFilled || true;
            dialogTitle.value = '病历详情';
            
            // 显示对话框
            dialogVisible.value = true;
        } else {
            console.error('未找到病历信息');
        }
    } catch (error) {
        console.error('搜索失败', error);
    }
}

// 重置选中记录的方法
const resetSelectedRecord = () => {
    selectedRecord.recordId = 0;
    selectedRecord.patientName = '';
    selectedRecord.hospitalName = '';
    selectedRecord.department = '';
    selectedRecord.doctorName = '';
    selectedRecord.pastMedicalHistory = '';
    selectedRecord.currentMedicalHistory = '';
    selectedRecord.createTime = '';
    selectedRecord.isFilled = false;
    selectedRecord.registrationInfo = '';
};

const viewRecord = (row: MedicalRecord) => {
    // 重置选中记录
    resetSelectedRecord();
    
    // 复制数据
    Object.assign(selectedRecord, row);
    
    // 设置视图状态
    viewOnly.value = row.isFilled;
    dialogTitle.value = '病历详情';
    
    // 显示对话框
    dialogVisible.value = true;
}

onMounted(() => {
    fetchMedicalRecords()
})
</script>

<style lang="scss" scoped>
.my-medical {
    padding: 20px;
    background: linear-gradient(135deg, #ffffff 0%, #f8faff 100%);
    border-radius: 15px;
    position: relative;
    overflow: hidden;
}


.my-medical::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, #4a9e5c, #2196F3);
    border-radius: 3px;
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

.search-section {
    margin-bottom: 25px;
    padding: 20px;
    background: rgba(255, 255, 255, 0.8);
    border-radius: 12px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.8);
    display: flex;
    gap: 15px;
    align-items: center;
}

:deep(.el-input__wrapper) {
    background: rgba(255, 255, 255, 0.9);
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05) !important;
    transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08) !important;
}

:deep(.el-input__wrapper.is-focus) {
    box-shadow: 0 0 0 1px #4a9e5c !important;
    transform: translateY(-1px);
}

.search-btn {
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
}

.search-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 5px 15px rgba(74, 158, 92, 0.3);
}

.search-btn::after {
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

:deep(.el-button--primary) {
    background: linear-gradient(135deg, #4a9e5c 0%, #2196F3 100%);
    border: none;
}

:deep(.el-tag--info) {
    background: linear-gradient(135deg, #90A4AE 0%, #B0BEC5 100%);
    border: none;
    color: white;
}

.operation-btn {
    padding: 8px 15px;
    border-radius: 6px;
    font-size: 14px;
    transition: all 0.3s ease;
    background: transparent;
    border: 1px solid #4a9e5c;
    color: #4a9e5c;
}

.operation-btn:hover {
    background: linear-gradient(135deg, #4a9e5c 0%, #2196F3 100%);
    color: white;
    border-color: transparent;
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(74, 158, 92, 0.2);
}

:deep(.el-dialog) {
    border-radius: 15px;
    overflow: hidden;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
    background: rgba(255, 255, 255, 0.95);
}

:deep(.el-dialog__header) {
    background: linear-gradient(90deg, rgba(74, 158, 92, 0.1), rgba(33, 150, 243, 0.1));
    padding: 20px;
    margin: 0;
}

:deep(.el-dialog__title) {
    color: #2c3e50;
    font-weight: 600;
    font-size: 18px;
}

:deep(.el-dialog__body) {
    padding: 30px;
}

:deep(.el-dialog__footer) {
    border-top: 1px solid rgba(74, 158, 92, 0.1);
    padding: 15px 20px;
}

.dialog-footer button {
    padding: 10px 20px;
    border-radius: 8px;
    font-size: 14px;
    transition: all 0.3s ease;
}

.dialog-footer .confirm-btn {
    background: linear-gradient(135deg, #4a9e5c 0%, #2196F3 100%);
    border: none;
    color: white;
}

.dialog-footer .confirm-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 5px 15px rgba(74, 158, 92, 0.3);
}

/* 添加分页样式 */
.pagination {
    margin-top: 20px;
    padding: 15px;
    display: flex;
    justify-content: flex-end;
    background: linear-gradient(135deg, #ffffff 0%, #f8faff 100%);
    border-radius: 12px;
    box-shadow: 0 4px 15px rgba(74, 158, 92, 0.08);
}

:deep(.el-pagination) {
    .el-pagination__total {
        margin-right: 16px;
    }
    
    .el-pagination__sizes {
        margin-right: 16px;
    }
    
    .el-pager li {
        background: transparent;
        &.is-active {
            background: linear-gradient(135deg, #4a9e5c 0%, #2196F3 100%);
            color: white;
        }
    }
}
</style>