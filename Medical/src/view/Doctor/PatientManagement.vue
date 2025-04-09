<template>
    <div class="patient-management">
        <div class="search-section">
            <div class="search-label">搜索条件：</div>
            <div class="search-input">
                <el-input v-model="searchText" placeholder="请输入患者地址" />
                <el-button type="primary" @click="handleSearch">搜索患者</el-button>
            </div>
        </div>

        <el-table :data="tableData" border style="width: 100%">
            <el-table-column prop="username" label="姓名" width="120" />
            <el-table-column prop="gender" label="性别" width="80" />
            <el-table-column prop="age" label="年龄" width="80" />
            <el-table-column prop="accountAddress" label="区块链地址" width="300" />
            <el-table-column label="操作" width="200" fixed="right">
                <template #default="scope">
                    <!-- <el-button type="primary" size="small" @click="viewPatientDetails(scope.row)">查看详情</el-button> -->
                    <el-button type="success" size="small" @click="editPatient(scope.row)">编辑</el-button>
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

        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="50%">
            <el-form :model="patientForm" label-width="100px">
                <el-form-item label="姓名">
                    <el-input v-model="patientForm.username" :disabled="viewOnly" />
                </el-form-item>
                <el-form-item label="性别">
                    <el-select v-model="patientForm.gender" :disabled="viewOnly">
                        <el-option label="男" value="男" />
                        <el-option label="女" value="女" />
                    </el-select>
                </el-form-item>
                <el-form-item label="年龄">
                    <el-input-number v-model="patientForm.age" :min="0" :max="150" :disabled="viewOnly" />
                </el-form-item>
                <el-form-item label="区块链地址">
                    <el-input v-model="patientForm.accountAddress" :disabled="viewOnly" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="savePatient" v-if="!viewOnly">保存</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const searchText = ref('')
const dialogVisible = ref(false)
const dialogTitle = ref('')
const viewOnly = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])

interface PatientForm {
    username: string;
    gender: string;
    age: number;
    accountAddress: string;
    appointmentNumber: number[];
    appointmentCounter: number;
}

const patientForm = reactive<PatientForm>({
    username: '',
    gender: '',
    age: 0,
    accountAddress: '',
    appointmentNumber: [],
    appointmentCounter: 0
})

const doctor = ref()
const handleSearch = async () => {
    const response = await axios.get('http://localhost:8085/patient/getPatientInfo', {
        params: {
            address: searchText.value,
        }
    })
    if (response.status === 200) {
        const data = response.data
        patientForm.username = data[0];
        patientForm.gender = data[1];
        patientForm.age = data[2];
        patientForm.accountAddress = data[3];
        dialogTitle.value = '患者详情';
        viewOnly.value = true;
        dialogVisible.value = true;
    } else {
        ElMessage.error('获取患者信息失败')
    }
}

const handleSizeChange = (val: number) => {
    pageSize.value = val
    handleSearch()
}

const handleCurrentChange = (val: number) => {
    currentPage.value = val
    handleSearch()
}

const editPatient = (row: any) => {
    Object.assign(patientForm, row)
    dialogTitle.value = '编辑患者信息'
    viewOnly.value = false
    dialogVisible.value = true
}

const savePatient = async () => {
    try {
        await axios.post('http://localhost:8085/doctor/updatePatient', patientForm)
        ElMessage.success('保存成功')
        dialogVisible.value = false
        handleSearch()
    } catch (error) {
        ElMessage.error('保存失败')
    }
}

const getPatientsInfoByDoctor = async () => {
    doctor.value = JSON.parse(localStorage.getItem('doctorinfo')!).accountAddress
    const response = await axios.get('http://localhost:8085/doctor/getPatientsInfoByDoctor', {
        params: {
            doctorAddress: doctor.value
        }
    })
    if (response.status === 200) {
        const data = response.data
        const parsedData = data.flatMap((item: any) => {
            try {
                const outerParsed = JSON.parse(item);
                return outerParsed.map((innerArray: any[]) => ({
                    username: innerArray[0],
                    gender: innerArray[1],
                    age: innerArray[2],
                    accountAddress: innerArray[3],
                    appointmentNumber: innerArray[5] || [],
                    appointmentCounter: innerArray[6] || 0
                }));
            } catch (error) {
                console.error("Error parsing item:", item, error);
                return [];
            }
        });
        tableData.value = parsedData;
        total.value = parsedData.length;
    }
};

onMounted(() => {
    getPatientsInfoByDoctor();
});

</script>

<style scoped>
.patient-management {
    padding: 20px;
    background: #fff;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.search-section {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
    padding: 15px;
    background: #f5f7fa;
    border-radius: 4px;
}

.search-label {
    margin-right: 10px;
    font-weight: bold;
    color: #606266;
}

.search-input {
    display: flex;
    gap: 10px;
    flex: 1;
}

.search-input :deep(.el-input) {
    width: 200px;
}

.pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
    padding: 10px 0;
}

.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    padding-top: 20px;
    border-top: 1px solid #dcdfe6;
}

:deep(.el-table) {
    margin-top: 15px;
    border-radius: 4px;
    overflow: hidden;
}

:deep(.el-button) {
    padding: 8px 15px;
}

:deep(.el-dialog__body) {
    padding: 20px 30px;
}

:deep(.el-form-item) {
    margin-bottom: 22px;
}

:deep(.el-select) {
    width: 100%;
}
</style>