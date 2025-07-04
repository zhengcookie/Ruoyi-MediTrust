<template>
    <div class="medical-records">
        <h2 class="page-title">病历管理</h2>
        <div class="search-section">

            <el-input v-model="searchText" placeholder="请输入病历ID" class="search-input">
            </el-input>
            <el-button type="primary" @click="handleSearch" class="search-button">搜索病历</el-button>
            <el-button type="primary" @click="openFillInDialog" class="add-button">填写病历</el-button>
            <el-button type="danger" @click="handleBatchDelete" class="search-button" :disabled="!selectedRows.length">
                <i class="el-icon-delete"></i> 批量删除
            </el-button>
        </div>

        <el-table :data="tableData" border style="min-width: 100%" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" />
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

            <el-table-column label="操作" width="200">
                <template #default="scope">
                    <el-button type="primary" size="small" @click="completeRecord(scope.row)">完成</el-button>
                    <el-button type="info" size="small" @click="EditRecord(scope.row)">编辑</el-button>
                    <el-button type="danger" size="small" @click="deleteRecord(scope.row)" 
                        :disabled="!scope.row.isFilled">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <div class="pagination">
            <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                :page-sizes="[10, 20, 30, 50]" :total="total" layout="total, sizes, prev, pager, next"
                @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        </div>

        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="60%" @close="closeDialog">
            <el-form :model="recordForm" :rules="rules" ref="recordFormRef">
                <el-descriptions :column="2" border>
                    <el-descriptions-item label="病历编号" v-if="dialogTitle !== '填写病历'">
                        <el-input v-model="recordForm.recordId" :disabled="true" />
                    </el-descriptions-item>
                    <el-descriptions-item label="患者姓名">
                        <el-autocomplete
                            v-model="recordForm.patientName"
                            :fetch-suggestions="queryPatientNames"
                            placeholder="请输入患者姓名"
                            :trigger-on-focus="true"
                            clearable
                            :disabled="viewOnly"
                            @select="handlePatientSelect"
                            @focus="onPatientInputFocus"
                        ></el-autocomplete>
                    </el-descriptions-item>

                    <el-descriptions-item label="医院名称">
                        <el-autocomplete
                            v-model="recordForm.hospitalName"
                            :fetch-suggestions="queryHospitals"
                            placeholder="请输入或选择医院"
                            clearable
                            :disabled="viewOnly"
                            @select="handleHospitalSelect"
                        ></el-autocomplete>
                    </el-descriptions-item>
                    <el-descriptions-item label="科室">
                        <el-autocomplete
                            v-model="recordForm.department"
                            :fetch-suggestions="queryDepartments"
                            placeholder="请输入或选择科室"
                            clearable
                            :disabled="viewOnly"
                            @select="handleDepartmentSelect"
                        ></el-autocomplete>
                    </el-descriptions-item>
                    <el-descriptions-item label="医生姓名">
                        <el-input v-model="recordForm.doctorName" :disabled="viewOnly" />
                    </el-descriptions-item>

                    <el-descriptions-item label="挂号信息">
                        <el-input v-model="recordForm.registrationInfo" type="textarea" :rows="2"
                            :disabled="viewOnly" />
                    </el-descriptions-item>
                    <el-descriptions-item label="既往病史">
                        <el-input v-model="recordForm.pastMedicalHistory" type="textarea" :rows="3"
                            :disabled="viewOnly" />
                    </el-descriptions-item>
                    <el-descriptions-item label="现病史">
                        <el-input v-model="recordForm.currentMedicalHistory" type="textarea" :rows="3"
                            :disabled="viewOnly" />
                    </el-descriptions-item>
                    <el-descriptions-item label="状态">{{ recordForm.isFilled ? '已完成' : '未完成'
                    }}</el-descriptions-item>
                    <el-descriptions-item label="创建日期">
                        <template v-if="dialogTitle === '填写病历'">
                            <span>{{ currentDateTime }}</span>
                        </template>
                        <template v-else>
                            {{ formatDateTime(recordForm.createdAt) }}
                        </template>
                    </el-descriptions-item>
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
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus';
import {getMedicalRecordByIdentityNumber, getMedicalRecordList, completeMedicalRecord, createMedicalRecord, updateMedicalRecord, deleteMedicalRecord, getAllPatientsName } from "../../api/medical";

const searchText = ref('')
const dialogVisible = ref(false)
const dialogTitle = ref('')
const viewOnly = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref<any[]>([])
const loading = ref(false)
const selectedRows = ref<any[]>([])
const isDeleting = ref(false)

// Define recordForm as a reactive object
const recordForm = reactive({
    recordId: 0,
    patientName: '',
    doctorName: '',
    hospitalName: '',
    department: '',
    registrationInfo: '',
    pastMedicalHistory: '',
    currentMedicalHistory: '',
    isFilled: false,
    createdAt: 0
});

const recordFormRef = ref<FormInstance>();
const userId = parseInt(localStorage.getItem("userId") || "0");

// 添加当前流动的时间
const currentDateTime = ref('');
let timeInterval: ReturnType<typeof setInterval> | null = null;

// 更新当前时间的函数
const updateCurrentTime = () => {
    currentDateTime.value = formatDateTime(Date.now());
};

// 存储所有患者信息
const patientsList = ref<Array<{patientId: number, patientName: string}>>([]);

// 在输入框获得焦点时也尝试加载数据
const onPatientInputFocus = () => {
    console.log("患者输入框获得焦点");
    if (patientsList.value.length === 0) {
        console.log("尝试加载患者列表...");
        fetchPatients();
    }
};

// 获取所有患者信息
const fetchPatients = async () => {
    console.log("开始获取患者列表...");
    
    // 添加一些测试数据，以防API暂时不工作
    const testPatients = [
        { patientId: 1, patientName: "张三" },
        { patientId: 2, patientName: "李四" },
        { patientId: 3, patientName: "王五" }
    ];
    
    try {
        const response = await getAllPatientsName();
        console.log("API返回原始患者数据:", response);
        
        if (response && Array.isArray(response) && response.length > 0) {
            patientsList.value = response.map((patient: any) => ({
                patientId: patient.patientId,
                patientName: patient.patientName,
            }));
            console.log('获取患者列表成功，数量:', patientsList.value.length);
            return Promise.resolve(response);
        } else {
            console.warn('API未返回有效患者数据，使用测试数据');
            patientsList.value = testPatients;
            return Promise.resolve(testPatients);
        }
    } catch (error) {
        console.error('获取患者列表失败', error);
        console.warn('使用测试数据代替');
        patientsList.value = testPatients;
        return Promise.resolve(testPatients);
    }
};

// 查询过滤患者姓名
const queryPatientNames = (queryString: string, callback: (suggestions: any[]) => void) => {
    console.log("查询患者，查询字符串:", queryString, "当前列表长度:", patientsList.value.length);
    
    // 如果列表为空或长度为0，使用测试数据
    if (!patientsList.value || patientsList.value.length === 0) {
        console.log("患者列表为空，尝试加载...");
        fetchPatients().then(() => {
            console.log("加载后过滤患者");
            filterPatients(queryString, callback);
        });
    } else {
        console.log("使用现有患者列表过滤");
        filterPatients(queryString, callback);
    }
};

// 过滤患者数据
const filterPatients = (queryString: string, callback: (suggestions: any[]) => void) => {
    console.log("过滤患者，输入字符:", queryString);
    
    // 确保有至少一个患者数据
    if (!patientsList.value || patientsList.value.length === 0) {
        // 如果没有数据，返回测试数据
        const testPatients = [
            { patientId: 1, patientName: "张三" },
            { patientId: 2, patientName: "李四" },
            { patientId: 3, patientName: "王五" }
        ].map(patient => ({
            value: patient.patientName,
            id: String(patient.patientId),
            label: `${patient.patientName}`,
        }));
        console.log("没有患者数据，使用测试数据");
        callback(testPatients);
        return;
    }
    
    // 如果查询字符串为空或未定义，返回所有患者
    if (!queryString) {
        const allPatients = patientsList.value.map(patient => ({
            value: patient.patientName,
            id: String(patient.patientId || 0),
            label: `${patient.patientName}`,
        }));
        console.log("返回所有患者:", allPatients.length);
        callback(allPatients);
        return;
    }
    
    // 对患者名进行过滤，支持单个字符匹配
    const results = patientsList.value.filter(patient => {
        if (!patient || !patient.patientName) return false;
        // 对患者名进行字符匹配，忽略大小写
        return patient.patientName.toLowerCase().includes(queryString.toLowerCase());
    });
    
    console.log("过滤后的结果数量:", results.length);
    
    // 转换为autocomplete所需格式
    const suggestions = results.map(patient => ({
        value: patient.patientName,
        id: String(patient.patientId || 0),
        label: `${patient.patientName}`
    }));
    
    console.log("返回的患者建议列表数量:", suggestions.length);
    callback(suggestions);
};

// 选择患者处理
const handlePatientSelect = (item: any) => {
    recordForm.patientName = item.value;
    console.log('已选择患者:', item);
};

// 科室选项列表
const departmentOptions = [
    { value: '内科', label: '内科' },
    { value: '外科', label: '外科' },
    { value: '妇产科', label: '妇产科' },
    { value: '儿科', label: '儿科' },
    { value: '眼科', label: '眼科' },
    { value: '口腔科', label: '口腔科' },
    { value: '耳鼻喉科', label: '耳鼻喉科' },
    { value: '皮肤科', label: '皮肤科' },
    { value: '神经科', label: '神经科' },
    { value: '精神心理科', label: '精神心理科' },
    { value: '肿瘤科', label: '肿瘤科' },
    { value: '中医科', label: '中医科' },
    { value: '急诊科', label: '急诊科' },
    { value: '检验科', label: '检验科' },
    { value: '放射科', label: '放射科' },
    { value: '麻醉科', label: '麻醉科' },
    { value: '整形科', label: '整形科' },
    { value: '营养科', label: '营养科' },
    { value: '康复科', label: '康复科' }
];

// 查询过滤科室
const queryDepartments = (queryString: string, callback: (suggestions: any[]) => void) => {
    const results = queryString
        ? departmentOptions.filter(
            (dept) => dept.value.toLowerCase().includes(queryString.toLowerCase())
        )
        : departmentOptions;
    
    callback(results);
};

// 选择科室处理
const handleDepartmentSelect = (item: any) => {
    recordForm.department = item.value;
};

// 常见医院列表
const hospitalOptions = [
    { value: '北京协和医院', label: '北京协和医院' },
    { value: '首都医科大学附属北京天坛医院', label: '首都医科大学附属北京天坛医院' },
    { value: '北京大学第一医院', label: '北京大学第一医院' },
    { value: '北京大学人民医院', label: '北京大学人民医院' },
    { value: '北京大学第三医院', label: '北京大学第三医院' },
    { value: '中国医学科学院北京协和医院', label: '中国医学科学院北京协和医院' },
    { value: '中国医学科学院肿瘤医院', label: '中国医学科学院肿瘤医院' },
    { value: '首都医科大学附属北京友谊医院', label: '首都医科大学附属北京友谊医院' },
    { value: '首都医科大学宣武医院', label: '首都医科大学宣武医院' },
    { value: '北京中医药大学东直门医院', label: '北京中医药大学东直门医院' },
    { value: '复旦大学附属中山医院', label: '复旦大学附属中山医院' },
    { value: '复旦大学附属华山医院', label: '复旦大学附属华山医院' },
    { value: '上海交通大学医学院附属瑞金医院', label: '上海交通大学医学院附属瑞金医院' },
    { value: '上海交通大学医学院附属仁济医院', label: '上海交通大学医学院附属仁济医院' },
    { value: '中山大学附属第一医院', label: '中山大学附属第一医院' },
];

// 查询过滤医院
const queryHospitals = (queryString: string, callback: (suggestions: any[]) => void) => {
    const results = queryString
        ? hospitalOptions.filter(
            (hospital) => hospital.value.toLowerCase().includes(queryString.toLowerCase())
        )
        : hospitalOptions;
    
    callback(results);
};

// 选择医院处理
const handleHospitalSelect = (item: any) => {
    recordForm.hospitalName = item.value;
};

const handleSearch = async () => {
    if (searchText.value == '') {
        ElMessage.error('请输入病历ID');
        return;
    }

    try {
        const recordId = parseInt(searchText.value);
        if (isNaN(recordId)) {
            ElMessage.error('病历ID必须是数字');
            return;
        }
        
        // 临时尝试使用其他API方法获取病历详情
        loading.value = true;
        console.log(`尝试搜索病历，ID: ${recordId}, 用户ID: ${userId}`);
        
        // 首先尝试使用getMedicalRecord获取病历
        let res = await getMedicalRecordByIdentityNumber(recordId, userId);
        console.log('病历查询结果:', res); // 添加日志查看响应结构

        // 确保res不为空且有数据
        if (res.code === 200) {
            console.log('病历查询结果:', res); // 添加日志查看响应结构
            // 重置表单，确保没有旧数据
            resetRecordForm();
            
            // 直接设置属性而不是使用Object.assign
            recordForm.recordId = res.recordId;
            recordForm.patientName = res.patientName || '';
            recordForm.doctorName = res.doctorName || '';
            recordForm.hospitalName = res.hospitalName || '';
            recordForm.department = res.department || '';
            recordForm.registrationInfo = res.registrationInfo || '';
            recordForm.pastMedicalHistory = res.pastMedicalHistory || '';
            recordForm.currentMedicalHistory = res.currentMedicalHistory || '';
            recordForm.isFilled = res.isFilled || false;
            recordForm.createdAt = res.createTime || Date.now();

            // 设置视图状态
            viewOnly.value = res.isFilled;
            dialogTitle.value = '病历详情';
            
            // 确保对话框可见
            dialogVisible.value = true;
            
            // 通知用户查询成功
            ElMessage.success('查询病历成功');
        } else {
            ElMessage.error('搜索失败，未找到病历信息');
        }
    } catch (error) {
        console.error('搜索病历出错:', error);
        ElMessage.error('搜索失败');
    } finally {
        loading.value = false;
    }
}

const handleSizeChange = (val: number) => {
    pageSize.value = val;
    getMedicalRecord();
}

const handleCurrentChange = (val: number) => {
    currentPage.value = val;
    getMedicalRecord();
}

const completeRecord = (row: any) => {
    ElMessageBox.confirm('确认完成此病历?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            const recordId = parseInt(row.recordId);
            const response = await completeMedicalRecord(recordId);
            console.log('完成病历响应:', response); // 查看响应结构

            if (response && response.code === 200) {
                ElMessage.success('病历已完成');
                getMedicalRecord();
            } else {
                ElMessage.error(response?.msg || '操作失败');
            }
        } catch (error) {
            ElMessage.error('操作失败');
        }
    });
}

const openFillInDialog = () => {
    resetRecordForm();
    dialogTitle.value = '填写病历';
    viewOnly.value = false;
    dialogVisible.value = true;

    // 启动时间流逝效果
    updateCurrentTime(); // 立即更新一次
    if (timeInterval) clearInterval(timeInterval);
    timeInterval = setInterval(updateCurrentTime, 1000); // 每秒更新一次
}

// 表单验证规则
const rules = {

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

    try {
        await recordFormRef.value.validate(async (valid) => {
            if (valid) {
                // 确保patientName是字符串类型
                if (typeof recordForm.patientName !== 'string') {
                    recordForm.patientName = String(recordForm.patientName);
                }

                // 确保doctorName是字符串类型
                if (typeof recordForm.doctorName !== 'string') {
                    recordForm.doctorName = String(recordForm.doctorName);
                }

                // 获取当前时间戳，用于发送到后端
                const currentTimestamp = Date.now();

                if (dialogTitle.value === "填写病历") {
                    // 创建新病历，使用当前时间戳
                    recordForm.createdAt = currentTimestamp;
                    const response = await createMedicalRecord(
                        recordForm.patientName,
                        recordForm.doctorName,
                        recordForm.hospitalName,
                        recordForm.department,
                        recordForm.registrationInfo,
                        recordForm.pastMedicalHistory,
                        recordForm.currentMedicalHistory,
                        currentTimestamp // 发送时间戳到后端
                    );
                    console.log('创建病历响应:', response); // 查看响应结构

                    if (response && response.code === 200) {
                        ElMessage.success('病历填写成功');
                        dialogVisible.value = false;
                        getMedicalRecord();
                        resetRecordForm();
                    } else {
                        ElMessage.error("病历填写失败");
                    }
                } else {
                    // 更新现有病历，使用当前时间戳
                    recordForm.createdAt = currentTimestamp;
                    const response = await updateMedicalRecord(
                        recordForm.recordId,
                        recordForm.hospitalName,
                        recordForm.department,
                        recordForm.registrationInfo,
                        recordForm.pastMedicalHistory,
                        recordForm.currentMedicalHistory,
                        currentTimestamp // 发送时间戳到后端
                    );
                    console.log('更新病历响应:', response); // 查看响应结构

                    if (response && response.code === 200) {
                        ElMessage.success("病历详情保存成功");
                        dialogVisible.value = false;
                        getMedicalRecord();
                        resetRecordForm();
                    } else {
                        ElMessage.error("病历详情保存失败");
                    }
                }

                // 对话框关闭时清除定时器
                if (timeInterval) {
                    clearInterval(timeInterval);
                    timeInterval = null;
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
    // 创建副本避免直接引用
    Object.assign(recordForm, {
        recordId: row.recordId,
        patientName: typeof row.patientName === 'number' ? String(row.patientName) : row.patientName,
        doctorName: typeof row.doctorName === 'number' ? String(row.doctorName) : row.doctorName,
        hospitalName: row.hospitalName,
        department: row.department,
        registrationInfo: row.registrationInfo,
        pastMedicalHistory: row.pastMedicalHistory,
        currentMedicalHistory: row.currentMedicalHistory,
        isFilled: row.isFilled,
        createdAt: row.createTime && typeof row.createTime === 'string' && !isNaN(Date.parse(row.createTime))
            ? Date.parse(row.createTime)
            : (row.createdAt || Date.now())
    });
    viewOnly.value = row.isFilled;
    dialogTitle.value = '病历详情';
    dialogVisible.value = true;
}

const deleteRecord = async (row: any) => {
    if (!row.isFilled) {
        ElMessage.warning('只能删除已完成的病历');
        return;
    }

    ElMessageBox.confirm('确认删除此病历?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            const recordId = parseInt(row.recordId);
            const response = await deleteMedicalRecord(recordId);

            if (response.code === 200) {
                ElMessage.success('删除成功');
                getMedicalRecord();
            } else {
                ElMessage.error(response?.msg || '删除失败');
            }
        } catch (error) {
            ElMessage.error('删除失败');
        }
    });
}

const formatDateTime = (timestamp: number) => {
    if (!timestamp) return '';
    const date = new Date(timestamp);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

const getMedicalRecord = async () => {
    try {
        loading.value = true;
        const res = await getMedicalRecordList(
            userId
        );
        console.log('获取病历列表响应:', res);
        tableData.value = res.map((record: any) => ({
            ...record,
            createTime: formatDateTime(record.createTime)
           
        }));
    } catch (error) {
        console.error('获取病历列表失败:', error);
        ElMessage.error('获取病历列表失败');
    } finally {
        loading.value = false;
    }
}

const resetRecordForm = () => {
    // Reset form fields
    Object.assign(recordForm, {
        recordId: '',
        patientName: '',
        doctorName: localStorage.getItem("name") || '', // 默认为当前登录医生
        hospitalName: '',
        department: '',
        registrationInfo: '',
        pastMedicalHistory: '',
        currentMedicalHistory: '',
        isFilled: false,
        createdAt: 0
    });
};

// 在关闭对话框时清除定时器
const closeDialog = () => {
    if (timeInterval) {
        clearInterval(timeInterval);
        timeInterval = null;
    }
    resetRecordForm();
}

// 处理表格多选
const handleSelectionChange = (selection: any[]) => {
    selectedRows.value = selection;
};

// 批量删除
const handleBatchDelete = () => {
    if (selectedRows.value.length === 0) {
        ElMessage.warning('请选择要删除的病历');
        return;
    }

    // 过滤出已完成的病历
    const completedRecords = selectedRows.value.filter(row => row.isFilled);
    if (completedRecords.length === 0) {
        ElMessage.warning('只能删除已完成的病历');
        return;
    }

    if (isDeleting.value) {
        ElMessage.warning('正在处理删除请求，请稍候...');
        return;
    }

    ElMessageBox.confirm(`确定要删除选中的 ${completedRecords.length} 条已完成的病历记录吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        isDeleting.value = true;
        const failedDeletions: number[] = [];
        
        try {
            // 串行处理删除请求
            for (const row of completedRecords) {
                let retryCount = 0;
                const maxRetries = 3;
                
                while (retryCount < maxRetries) {
                    try {
                        await deleteMedicalRecord(row.recordId);
                        // 增加延迟时间到1秒
                        await new Promise(resolve => setTimeout(resolve, 1000));
                        break; // 如果成功，跳出重试循环
                    } catch (error) {
                        retryCount++;
                        if (retryCount === maxRetries) {
                            console.error(`删除病历 ${row.recordId} 失败，已重试${maxRetries}次:`, error);
                            failedDeletions.push(row.recordId);
                        } else {
                            // 如果失败且还有重试机会，等待2秒后重试
                            await new Promise(resolve => setTimeout(resolve, 2000));
                        }
                    }
                }
            }
            
            if (failedDeletions.length === 0) {
                ElMessage.success('批量删除成功');
            } else {
                ElMessage.warning(`部分删除成功，${failedDeletions.length} 条记录删除失败，请稍后重试`);
            }
            
            // 等待一段时间后再刷新列表
            await new Promise(resolve => setTimeout(resolve, 1000));
            getMedicalRecord(); // 刷新列表
            selectedRows.value = []; // 清空选中项
        } catch (error) {
            ElMessage.error('批量删除失败，请稍后重试');
            console.error('批量删除失败:', error);
        } finally {
            isDeleting.value = false;
        }
    }).catch(() => {
        // 用户取消删除操作
    });
};

onMounted(() => {
    getMedicalRecord();
    // fetchPatients(); // 不再需要在挂载时获取患者列表
    
    // 初始化重置表单
    resetRecordForm();
    
    // 设置更新时间的定时器
    updateCurrentTime(); 
});

// 在组件卸载时清除定时器
onUnmounted(() => {
    if (timeInterval) {
        clearInterval(timeInterval);
    }
});
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
    background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
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
    background: linear-gradient(180deg, var(--primary-color), var(--secondary-color));
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
    background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
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
    background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
    border: none;
    color: white;
    position: relative;
    overflow: hidden;
    /* height: 100%; */
}

.add-button:hover,
.search-button:hover {
    transform: translateY(-2px);
    box-shadow: 0 5px 15px rgba(255, 255, 255, 0.1)
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
    background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
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
    border-color: var(--primary-color);
    box-shadow: 0 0 8px rgba(74, 158, 92, 0.2);
}

:deep(.el-table) {
    background: transparent;
    border-radius: 12px;
    overflow: hidden;
}

:deep(.el-table__header) {
    background: linear-gradient(90deg, var(--primary-color), var(--secondary-color));
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
    background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
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
    background: linear-gradient(90deg, var(--primary-color), var(--secondary-color));
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
    background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
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