<template>
    <div class="personal-info">
        <el-card>
            <template #header>
                <div class="info-header">
                    <span class="header-text">基本信息</span>
                </div>
            </template>
            <el-descriptions :column="1" border>
                <!-- <el-descriptions-item label="账户ID">
                    {{ PersonalInformation.id }}
                </el-descriptions-item> -->
                <el-descriptions-item label="性别">
                    {{ PersonalInformation.gender }}
                </el-descriptions-item>
                <el-descriptions-item label="姓名">
                    {{ PersonalInformation.username }}
                </el-descriptions-item>
                <el-descriptions-item label="年龄">
                    {{ PersonalInformation.age }}
                </el-descriptions-item>
                <el-descriptions-item label="邮箱">
                    {{ PersonalInformation.email }}
                </el-descriptions-item>
            </el-descriptions>
        </el-card>


    </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { getUserDetails } from "../../api/medical";

const doctorId = ref(0);
const PersonalInformation = reactive({
    id: '',
    username: '',
    gender: '',
    age: '',
    email: ''
});

onMounted(() => {
    // 获取医生ID - 从localStorage中获取用户ID
    doctorId.value = parseInt(localStorage.getItem("userId") || "0");
    
    // 如果有userId则直接获取信息
    if (doctorId.value) {
        getUserInfo();
    } else {
        console.log("未找到用户ID信息");
    }
});

const getUserInfo = async () => {
    try {
        const response = await getUserDetails(doctorId.value);
        if (response.code == 200) {
            PersonalInformation.email = response.email || '';
            PersonalInformation.username = response.name || '';
            PersonalInformation.gender = response.gender || '';
            PersonalInformation.age = response.age?.toString() || '';
        }
    } catch (error) {
        console.error('获取用户信息失败', error);
    }
};
</script>

<style scoped>
/* 卡片样式 */
.el-card {
    background: linear-gradient(135deg, #ffffff 0%, #f0f2f5 100%);
    border: none;
    border-radius: 15px;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
    position: relative;
    overflow: hidden;
}

/* 卡片顶部渐变线条 */
.el-card::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, #4a9e5c, #00d1ff);
}

/* 统计区域样式 */
.chart-stats {
    display: flex;
    justify-content: space-around;
    margin-bottom: 20px;
    background: linear-gradient(135deg, #ffffff 0%, #f8faff 100%);
    border-radius: 12px;
    padding: 20px;
    position: relative;
    overflow: hidden;
    border: 1px solid rgba(74, 158, 92, 0.1);
}

/* 科技感扫光动画 */
.chart-stats::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: linear-gradient(45deg,
            transparent 45%,
            rgba(74, 158, 92, 0.1) 50%,
            transparent 55%);
    animation: light-sweep 3s infinite linear;
}

/* 统计项样式 */
.stat-item {
    position: relative;
    text-align: center;
    padding: 15px 25px;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 8px;
    transition: all 0.3s ease;
    border: 1px solid rgba(74, 158, 92, 0.2);
    z-index: 1;
}

.stat-item:hover {
    transform: translateY(-5px);
    box-shadow: 0 5px 15px rgba(74, 158, 92, 0.2);
}

/* 数值样式 */
.stat-value {
    font-size: 28px;
    font-weight: bold;
    background: linear-gradient(45deg, #4a9e5c, #2196F3);
    -webkit-background-clip: text;
    color: transparent;
    position: relative;
}

/* 标签样式 */
.stat-label {
    font-size: 16px;
    color: #5c6c8c;
    margin-top: 5px;
    font-weight: 500;
}

/* 标题样式 */
.info-header {
    padding: 15px 0;
    position: relative;
}

.header-text {
    font-size: 18px;
    font-weight: 600;
    color: #2c3e50;
    position: relative;
    padding-left: 15px;
}

.header-text::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 4px;
    height: 18px;
    background: linear-gradient(to bottom, #4a9e5c, #2196F3);
    border-radius: 2px;
}

/* 扫光动画 */
@keyframes light-sweep {
    0% {
        transform: translate(-50%, -50%) rotate(0deg);
    }

    100% {
        transform: translate(-50%, -50%) rotate(360deg);
    }
}

/* 图表容器样式 */
.el-card :deep(.echarts) {
    border-radius: 8px;
    padding: 10px;
    background: rgba(255, 255, 255, 0.8);
}


</style>