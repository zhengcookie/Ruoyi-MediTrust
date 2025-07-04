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
            <el-table-column prop="isCompleted" label="挂号预约状态">
                <template #default="scope">
                    <el-tag :type="scope.row.isCompleted === '已完成' ? 'success' : 'warning'">
                        {{ scope.row.isCompleted }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="remark" label="病情描述" />
            <el-table-column label="操作" fixed="right" width="300">
                <template #default="scope">
                    <el-button type="primary" size="small" @click="showModalDialog(scope.row)">挂号预约详情</el-button>
                    <el-button type="danger" size="small" @click="cancelAppointment(scope.row)"
                        :disabled="scope.row.isCompleted === '已完成'">取消挂号预约</el-button>
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

                    <el-descriptions-item label="挂号预约状态">{{ patientInfo.isCompleted }}</el-descriptions-item>
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

        <el-dialog v-model="registerDialogVisible" title="挂号预约" width="60%">
            <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px" class="form-container">
                <el-card class="form-card" shadow="hover">
                    <div class="form-row">
                        <el-form-item label="医院名称：" required prop="hospitalName">
                            <el-autocomplete
                                v-model="formData.hospitalName"
                                :fetch-suggestions="queryHospitals"
                                placeholder="请输入或选择医院"
                                :trigger-on-focus="true"
                                clearable
                                class="form-select"
                                @select="handleHospitalSelect"
                            ></el-autocomplete>
                        </el-form-item>
                        <el-form-item label="挂号科室：" required prop="department">
                            <el-autocomplete
                                v-model="formData.department"
                                :fetch-suggestions="queryDepartments"
                                placeholder="请输入或选择科室"
                                :trigger-on-focus="true"
                                clearable
                                class="form-select"
                                @select="handleDepartmentSelect"
                            ></el-autocomplete>
                        </el-form-item>

                        <el-form-item label="医生姓名：" required prop="doctorName">
                            <el-autocomplete
                                v-model="formData.doctorName"
                                :fetch-suggestions="queryDoctorNames"
                                placeholder="请输入医生姓名"
                                :trigger-on-focus="true"
                                clearable
                                class="form-select"
                                @select="handleDoctorSelect"
                                @focus="onDoctorInputFocus"
                            ></el-autocomplete>
                        </el-form-item>
                    </div>

                    <div class="form-row">
                        

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
import { onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import type { FormInstance } from 'element-plus';
import { getSickAppointment, getAppointmentsList, deleteAppointment, createAppointment, getAllDoctorsName } from "../../api/medical";

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

const userId = parseInt(localStorage.getItem("userId") || "0");
const accountAddress = localStorage.getItem("accountAddress") || "";
const name = localStorage.getItem("name") || "";

// 预约详情信息
const patientInfo = reactive({
    patientName: '',
    appointmentId: 0,
    hospitalName: '',
    department: '',
    doctorName: '',
    createTime: '',
    isCompleted: '',
    remark: ''
});

// 新建预约表单数据
const formData = reactive({
    patientName: name,
    patient: accountAddress,
    doctorName: '',
    doctor: '',
    hospitalName: '',
    department: '',
    createTime: '',
    remark: ''
});

// 表单验证规则
const rules = {
    doctorName: [
        { required: true, message: '请输入医生姓名', trigger: 'blur' }
    ],
  
    hospitalName: [
        { required: true, message: '请输入医院名称', trigger: 'blur' }
    ],
    department: [
        { required: true, message: '请选择科室', trigger: 'blur' }
    ],
    createTime: [
        { required: true, message: '请选择挂号预约时间', trigger: 'blur' }
    ],
    remark: [
        { required: true, message: '请输入病情描述', trigger: 'blur' }
    ]
};

// 存储所有医生信息
const doctorsList = ref<{doctorId: number, doctorName: string}[]>([]);

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
    console.log("查询医院，输入字符:", queryString);
    
    // 如果查询字符串为空，返回所有医院
    if (!queryString) {
        console.log("返回所有医院");
        callback(hospitalOptions);
        return;
    }
    
    // 过滤医院名称，支持单个字符匹配
    const results = hospitalOptions.filter(
        (hospital) => hospital.value.toLowerCase().includes(queryString.toLowerCase())
    );
    
    console.log("过滤后的医院数量:", results.length);
    callback(results);
};

// 选择医院处理
const handleHospitalSelect = (item: any) => {
    formData.hospitalName = item.value;
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
    console.log("查询科室，输入字符:", queryString);
    
    // 如果查询字符串为空，返回所有科室
    if (!queryString) {
        console.log("返回所有科室");
        callback(departmentOptions);
        return;
    }
    
    // 过滤科室名称，支持单个字符匹配
    const results = departmentOptions.filter(
        (dept) => dept.value.toLowerCase().includes(queryString.toLowerCase())
    );
    
    console.log("过滤后的科室数量:", results.length);
    callback(results);
};

// 选择科室处理
const handleDepartmentSelect = (item: any) => {
    formData.department = item.value;
};

// 在输入框获得焦点时也尝试加载数据
const onDoctorInputFocus = () => {
    console.log("医生输入框获得焦点");
    if (doctorsList.value.length === 0) {
        console.log("尝试加载医生列表...");
        fetchDoctorNames();
    }
};

// 获取所有医生姓名
const fetchDoctorNames = async () => {
    console.log("开始获取医生列表...");
    
    // 添加一些测试数据，以防API暂时不工作
    const testDoctors = [
        { doctorId: 1, doctorName: "张医生" },
        { doctorId: 2, doctorName: "李医生" },
        { doctorId: 3, doctorName: "王医生" }
    ];
    
    try {
        const response = await getAllDoctorsName();
        console.log("API返回原始数据:", response);
        
        if (response && Array.isArray(response) && response.length > 0) {
            doctorsList.value = response.map((doctor: any) => ({
                doctorId: doctor.doctorId,
                doctorName: doctor.doctorName
            }));
            console.log('获取医生列表成功，数量:', doctorsList.value.length);
            return Promise.resolve(response);
        } else {
            console.warn('API未返回有效医生数据，使用测试数据');
            doctorsList.value = testDoctors;
            return Promise.resolve(testDoctors);
        }
    } catch (error) {
        console.error('获取医生列表失败', error);
        console.warn('使用测试数据代替');
        doctorsList.value = testDoctors;
        return Promise.resolve(testDoctors);
    }
};

// 查询过滤医生姓名
const queryDoctorNames = (queryString: string, callback: (suggestions: any[]) => void) => {
    console.log("查询医生，查询字符串:", queryString, "当前列表长度:", doctorsList.value.length);
    
    // 如果列表为空或长度为0，使用测试数据
    if (!doctorsList.value || doctorsList.value.length === 0) {
        console.log("医生列表为空，尝试加载...");
        fetchDoctorNames().then(() => {
            console.log("加载后过滤医生");
            filterDoctors(queryString, callback);
        });
    } else {
        console.log("使用现有医生列表过滤");
        filterDoctors(queryString, callback);
    }
};

// 过滤医生数据
const filterDoctors = (queryString: string, callback: (suggestions: any[]) => void) => {
    console.log("过滤医生，输入字符:", queryString);
    
    // 确保有至少一个医生数据
    if (!doctorsList.value || doctorsList.value.length === 0) {
        // 如果没有数据，返回测试数据
        const testDoctors = [
            { doctorId: 1, doctorName: "张医生" },
            { doctorId: 2, doctorName: "李医生" },
            { doctorId: 3, doctorName: "王医生" }
        ].map(doctor => ({
            value: doctor.doctorName,
            id: doctor.doctorId,
            label: doctor.doctorName
        }));
        console.log("没有医生数据，使用测试数据");
        callback(testDoctors);
        return;
    }
    
    // 如果查询字符串为空或未定义，返回所有医生
    if (!queryString) {
        const allDoctors = doctorsList.value.map(doctor => ({
            value: doctor.doctorName,
            id: doctor.doctorId || 0,
            label: doctor.doctorName
        }));
        console.log("返回所有医生:", allDoctors.length);
        callback(allDoctors);
        return;
    }
    
    // 对医生名进行过滤，支持单个字符的匹配
    const results = doctorsList.value.filter(doctor => {
        if (!doctor || !doctor.doctorName) return false;
        // 对医生名进行字符匹配，忽略大小写
        return doctor.doctorName.toLowerCase().includes(queryString.toLowerCase());
    });
    
    console.log("过滤后的结果数量:", results.length);
    
    // 转换为autocomplete所需格式
    const suggestions = results.map(doctor => ({
        value: doctor.doctorName,
        id: doctor.doctorId || 0,
        label: doctor.doctorName
    }));
    
    console.log("返回的医生建议列表:", JSON.stringify(suggestions));
    callback(suggestions);
};

// 选择医生处理
const handleDoctorSelect = (item: any) => {
    
    formData.doctorName = item.value;
    console.log('已选择医生:', item);
};

// 格式化时间戳为本地时间字符串
const formatDate = (timestamp: number): string => {
    if (!timestamp) return '';
    const date = new Date(timestamp);
    return date.toLocaleString();
};

const handleRegister = async () => {
    await submitForm();
}
// 搜索预约
const handleSearch = async () => {
    if (!searchText.value) {
        ElMessage.warning('请输入预约ID');
        return;
    }
    
    try {
        const appointmentId = parseInt(searchText.value);
        if (isNaN(appointmentId)) {
            ElMessage.error('预约ID必须是数字');
            return;
        }
        
        const response = await getSickAppointment(appointmentId, userId);
        console.log('查询预约响应:', response.code); // 查看响应结构
        if (response) {
            patientInfo.patientName = response.patientName;
            patientInfo.appointmentId = response.appointmentId;
            patientInfo.department = response.department;
            patientInfo.hospitalName = response.hospitalName;
            patientInfo.doctorName = response.doctorName;
            patientInfo.createTime = formatDate(response.createTime);
            patientInfo.isCompleted = response.isCompleted ? '已完成' : '未完成';
            patientInfo.remark = response.remark;
            dialogVisible.value = true;
        } else {
            ElMessage.error('查询失败');
        }
    } catch (error) {
        ElMessage.error('查询失败');
    }
};

// 分页大小变化处理
const handleSizeChange = (val: number) => {
    pageSize.value = val;
    getAppointment();
};


// 页码变化处理
const handleCurrentChange = (val: number) => {
    currentPage.value = val;
    getAppointment();
};

// 显示预约详情弹窗
const showModalDialog = (row: any) => {
    dialogVisible.value = true;
    loading.value = true;
    Object.assign(patientInfo, row);
    loading.value = false;
};

// 取消预约
const cancelAppointment = (row: any) => {
    ElMessageBox.confirm('确定要取消该挂号预约吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            const response = await deleteAppointment(row.appointmentId);
            console.log('取消预约响应:', response); // 查看响应结构
            if (response && response.code === 200) {
                ElMessage.success('取消成功');
                getAppointment();
            } else {
                ElMessage.error('取消失败');
            }
        } catch (error) {
            ElMessage.error('取消失败');
        }
    });
};

// 打印预约信息
// 打印预约信息
const printAppointment = () => {
  // 隐藏对话框内容以外的元素
  const printWindow = window.open('', '_blank');
  if (!printWindow) {
    ElMessage.error('请允许弹出窗口以打印预约单');
    return;
  }
  
  printWindow.document.write(`
    <html>
    <head>
      <title>挂号预约单</title>
      <style>
        @page { size: A4; margin: 2cm; }
        body { font-family: SimSun, sans-serif; }
        h1 { text-align: center; }
        table { width: 100%; border-collapse: collapse; }
        table, th, td { border: 1px solid black; }
        th, td { padding: 8px; text-align: left; }
        .remark { margin-top: 15px; border: 1px solid black; padding: 10px; }
      </style>
    </head>
    <body>
      <h1>${patientInfo.hospitalName}</h1>
      <h2 style="text-align:center">挂号预约信息单</h2>
      <table>
        <tr><th>编号</th><td>${patientInfo.appointmentId}</td></tr>
        <tr><th>患者姓名</th><td>${patientInfo.patientName}</td></tr>
        <tr><th>挂号预约医生</th><td>${patientInfo.doctorName}</td></tr>
        <tr><th>医院名称</th><td>${patientInfo.hospitalName}</td></tr>
        <tr><th>挂号预约科室</th><td>${patientInfo.department}</td></tr>
        <tr><th>挂号预约时间</th><td>${patientInfo.createTime}</td></tr>
        <tr><th>挂号预约状态</th><td>${patientInfo.isCompleted}</td></tr>
      </table>
      <div class="remark">
        <p><b>病情描述:</b></p>
        <p>${patientInfo.remark || '无'}</p>
      </div>
      <p style="text-align:right">打印时间: ${new Date().toLocaleString()}</p>
    </body>
    </html>
  `);
  
  printWindow.document.close();
  printWindow.onload = () => {
    printWindow.print();
    setTimeout(() => printWindow.close(), 500);
  };
};

// 关闭预约详情弹窗
const handleClose = () => {
    dialogVisible.value = false;
    Object.assign(patientInfo, {
        patientName: '',
        appointmentId: 0,
        hospitalName: '',
        department: '',
        doctorName: '',
        createTime: '',
        isCompleted: '',
        remark: ''
    });
};

// 获取所有预约列表
const getAppointment = async () => {
    try {
        const response = await getAppointmentsList(userId);
        if (response) {
            // 数据处理
            const parsedData = response.map((item: any) => ({
                appointmentId: item.appointmentId,
                patientName: item.patientName,
                hospitalName: item.hospitalName,
                department: item.department,
                doctorName: item.doctorName,
                createTime: formatDate(item.createTime),
                isCompleted: item.isCompleted ? '已完成' : '未完成',
                remark: item.remark
            }));

            tableData.value = parsedData;
            total.value = parsedData.length;
        }
    } catch (error) {
        console.error('获取预约列表失败', error);
    }
};

// 显示新建预约弹窗
const showRegisterDialog = () => {
    registerDialogVisible.value = true;
};

// 关闭确认预约弹窗
const handleConfirmClose = () => {
    confirmDialogVisible.value = false;
};

// 提交预约表单
const submitForm = async () => {
    if (!formRef.value) return;
    
    await formRef.value.validate((valid) => {
        if (valid) {
            confirmDialogVisible.value = true;
            registerDialogVisible.value = false;
        }
    });
};

// 确认创建预约
const confirmAppointment = async () => {
    if (!formRef.value) return;
    
    try {
        // 确保日期格式正确
        const timestamp = new Date(formData.createTime).getTime();
        
        const response = await createAppointment(
            formData.patientName,
            formData.doctorName,
            formData.hospitalName,
            formData.department,
            formData.remark,
            timestamp
        );
        
        if (response && response.code === 200) {
            ElMessage.success("挂号预约成功");
            confirmDialogVisible.value = false;
            getAppointment(); // 刷新预约列表
        } else {
            ElMessage.error("挂号预约失败！");
        }
    } catch (error) {
        ElMessage.error("提交请求失败");
        console.error(error);
    }
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