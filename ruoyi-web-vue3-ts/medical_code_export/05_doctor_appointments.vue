<template>
    <div class="appointment-management">
        
            <h2 class="page-title">挂号预约</h2>
            <div class="search-section">

                <el-input v-model="appointmentId" placeholder="请输入挂号预约编号" class="search-input">
                    <template #append>
                        <el-button type="primary" @click="handleSearch" class="search-btn">搜索挂号预约</el-button>
                    </template>
                </el-input>
            </div>
            <el-table :data="tableData" border style="min-width: 100%">
                <el-table-column prop="patientName" label="患者姓名" width="120" />
                <!-- <el-table-column prop="patient" label="患者地址" width="120" /> -->
                <el-table-column fixed prop="appointmentId" label="挂号预约编号" width="120" />
                <el-table-column prop="hospitalName" label="医院名称" width="120" />
                <el-table-column prop="department" label="科室" width="120" />
                <el-table-column prop="doctorName" label="医生姓名" width="120" />
                <!-- <el-table-column prop="doctor" label="医生地址" width="120" /> -->
                <el-table-column prop="remark" label="备注" width="120" />
                <el-table-column prop="createTime" label="挂号预约时间" width="120" />
                <el-table-column prop="isCompleted" label="挂号预约状态">
                    <template #default="scope">
                        <el-tag :type="scope.row.isCompleted ? 'success' : 'warning'">
                            {{ scope.row.isCompleted ? '已完成' : '未完成' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="300">
                    <template #default="{ row }">
                        <el-button type="primary" @click="handleCompleteAppointment(row)">完成</el-button>
                        <el-button type="info" @click="EditAppointment(row)">编辑</el-button>
                        <el-button type="danger" @click="handleDeleteAppointment(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div class="pagination">
                <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                    :page-sizes="[10, 20, 30, 50]" :total="total" layout="total, sizes, prev, pager, next"
                    @size-change="handleSizeChange" @current-change="handleCurrentChange" />
            </div>
            <el-dialog v-model="dialogVisible" :title="dialogTitle" width="50%">
                <el-descriptions :column="2" border>
                    <el-descriptions-item label="挂号预约编号">
                        <el-input v-model="appointmentForm.appointmentId" :disabled="true" />
                    </el-descriptions-item>
                    <el-descriptions-item label="患者姓名">
                        <el-input v-model="appointmentForm.patientName" :disabled="viewOnly"/>
                    </el-descriptions-item>
                    <el-descriptions-item label="医生姓名">
                        <el-input v-model="appointmentForm.doctorName" :disabled="viewOnly"/>
                    </el-descriptions-item>
                    <el-descriptions-item label="医院名称">
                        <el-input v-model="appointmentForm.hospitalName" :disabled="viewOnly"/>
                    </el-descriptions-item>
                    <el-descriptions-item label="挂号预约科室">
                        <el-input v-model="appointmentForm.department" :disabled="viewOnly"/>
                    </el-descriptions-item>
                    <el-descriptions-item label="挂号预约时间">
                        <el-input v-model="appointmentForm.createTime" :disabled="viewOnly"/>
                    </el-descriptions-item>
                    <el-descriptions-item label="状态">{{ appointmentForm.isCompleted ? '已完成' : '未完成'
                    }}</el-descriptions-item>
                    <el-descriptions-item label="备注" :span="2">
                        <el-input v-model="appointmentForm.remark" type="textarea" :rows="3" :disabled="viewOnly"/>
                    </el-descriptions-item>
                </el-descriptions>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button @click="dialogVisible = false">关闭</el-button>
                        <el-button type="primary" @click="saveAppointment">保存</el-button>
                    </span>
                </template>
            </el-dialog>
        
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSickAppointment, getAppointmentsList, completeAppointment, updateAppointment, deleteAppointment } from '../../api/medical'
const dialogVisible = ref(false)
const dialogTitle = ref('挂号预约详情')
const viewOnly = ref(true)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref<any[]>([])

const appointmentForm = reactive({
    appointmentId: '',
    patientName: '',
    patient: "",
    doctorName: '',
    doctor: "",
    hospitalName: '',
    department: '',
    remark: '',
    createTime: 0,
    isCompleted: false
})
const appointmentId = ref();

const handleSearch = async () => {
    if (!appointmentId.value) {
        ElMessage.error('请输入挂号预约编号');
        return;
    }
    try {
        const response = await getSickAppointment(appointmentId.value, parseInt(localStorage.getItem('userId') || '0'));
        if (response.code == 200) {
            appointmentForm.appointmentId = response.appointmentId.toString();
            appointmentForm.patientName = response.patientName;
            appointmentForm.doctorName = response.doctorName;
            appointmentForm.hospitalName = response.hospitalName;
            appointmentForm.department = response.department;
            appointmentForm.createTime = response.createTime;  
            appointmentForm.isCompleted = response.isCompleted;
            appointmentForm.remark = response.remark;
            tableData.value = [response];
            dialogVisible.value = true;
        } else {
            ElMessage.error(response.msg)
        }
    } catch (error) {
        ElMessage.error('搜索失败');
    }
}

const EditAppointment = (row: any) => {
    appointmentForm.appointmentId = row.appointmentId;
    appointmentForm.patient = row.patient;
    appointmentForm.doctor = row.doctor;
    appointmentForm.patientName = row.patientName;
    appointmentForm.appointmentId = row.appointmentId;
    appointmentForm.department = row.department;
    appointmentForm.hospitalName = row.hospitalName;
    appointmentForm.doctorName = row.doctorName;
    appointmentForm.createTime = row.createTime;
    appointmentForm.isCompleted = row.isCompleted;
    appointmentForm.remark = row.remark;
    if (appointmentForm.isCompleted) {
        viewOnly.value = true;
    }
    else{
        viewOnly.value = false;
    }
    dialogVisible.value = true;
}

const saveAppointment = async () => {
    try {
        // 将日期字符串转换为时间戳或ISO格式
        let appointmentTime: any = appointmentForm.createTime;
        
        // 如果是字符串格式且不是ISO格式，则转换为时间戳
        if (typeof appointmentTime === 'string' && appointmentTime.indexOf('T') === -1) {
            const date = new Date(appointmentTime);
            if (!isNaN(date.getTime())) {
                appointmentTime = date.getTime();
            }
        }
        
        const response = await updateAppointment(
            parseInt(appointmentForm.appointmentId),
            appointmentForm.hospitalName,
            appointmentForm.department,
            appointmentForm.remark,
            appointmentTime
        );
        if (response.code === 200){
            ElMessage.success('挂号预约保存成功');
        }else{
            ElMessage.error("挂号预约保存失败!")
        }
        dialogVisible.value = false;
        getAppointmentInfoByDoctor();
    } catch (error) {
        ElMessage.error('保存挂号预约失败');
    }
}

const handleSizeChange = (val: number) => {
    pageSize.value = val;
    getAppointmentInfoByDoctor(); // 重新获取数据
}

const handleCurrentChange = (val: number) => {
    currentPage.value = val;
    getAppointmentInfoByDoctor(); // 重新获取数据
}

const handleCompleteAppointment = (row: any) => {
    ElMessageBox.confirm('确认完成此挂号预约?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            const response = await completeAppointment(row.appointmentId);
            console.log("完成挂号预约",response);
            ElMessage.success(response.msg || '操作成功')
            getAppointmentInfoByDoctor()
        } catch (error) {
            ElMessage.error('操作失败')
        }
    })
}

// const doctor = localStorage.getItem('accountAddress') || '{}';

const formatDateTime = (timestamp: any) => {
    // 处理无效或空值
    if (!timestamp) {
        return '未设置时间';
    }
    
    let dateValue: number;
    
    // 处理不同类型的时间戳
    if (typeof timestamp === 'number') {
        dateValue = timestamp;
    } else if (typeof timestamp === 'string') {
        // 尝试将字符串解析为数字
        const parsedNum = parseInt(timestamp);
        if (!isNaN(parsedNum)) {
            dateValue = parsedNum;
        } else {
            // 尝试作为日期字符串解析
            dateValue = new Date(timestamp).getTime();
            // 检查是否为有效日期
            if (isNaN(dateValue)) {
                return '无效时间格式';
            }
        }
    } else {
        return '无效时间格式';
    }
    
    // 其余格式化代码保持不变
    const date = new Date(dateValue);
    
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

const handleDeleteAppointment = async (row:any) => {
    ElMessageBox.confirm('确认删除此挂号预约?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            const response = await deleteAppointment(row.appointmentId);
            if(response.code === 200){
                ElMessage.success(response.msg || '删除成功')
                getAppointmentInfoByDoctor()
            } else {
                ElMessage.error(response.msg || '删除失败')
            }
        } catch (error) {
            ElMessage.error('删除失败')
        }
    })
}

const getAppointmentInfoByDoctor = async () => {
    try {
        const response = await getAppointmentsList(parseInt(localStorage.getItem('userId') || '0'));
        
        console.log(response);
        const records = response;
        const totalCount = records.length;
        
        const parsedData = records.map((item: any) => {
            // 优先使用创建时间作为挂号预约的固定时间
            const appointmentTime = item.createTime || item.time;
            console.log('Original appointment time:', appointmentTime);
            
            return {
                ...item,
                createTime: formatDateTime(appointmentTime)
            };
        });
        
        tableData.value = parsedData;
        total.value = totalCount;
    } catch (error) {
        ElMessage.error('获取数据失败');
    }
}

onMounted(() => {
    getAppointmentInfoByDoctor()
})
</script>

<style scoped>
.appointment-management {
    padding: 20px;
    background: linear-gradient(135deg, #ffffff 0%, #f0f7ff 100%);
    border-radius: 15px;
    position: relative;
    overflow: hidden;
    box-shadow: 0 0 30px rgba(33, 150, 243, 0.08);
}


.appointment-management::before {
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
    background: linear-gradient(90deg, transparent, #2196F3);
} */
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
    gap: 15px;
    flex: 1;
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

/* 表格样式 */
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

:deep(.el-tag--info) {
    background: linear-gradient(135deg, #90A4AE 0%, #B0BEC5 100%);
    border: none;
    color: white;
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
/* .appointment-management::after {
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
</style>
