<template>
    <animated-background v-slot="{ themeColors }">
        <div class="is-register">
            <h3 class="title">云链·医问</h3>
            <el-form ref="formRef" :model="registerItem" :rules="rules" @submit.prevent="onRegister" style="max-width: 400px" label-width="auto" class="register-form">
                <el-form-item label="邮箱" prop="email">
                    <el-input style="width: 300px" type="email" class="form-control" id="email"
                        placeholder="请输入邮箱" autocomplete="off" v-model.trim="registerItem.email" @blur="handleEmailBlur">
                    </el-input>
                </el-form-item>
                <el-form-item label="验证码" prop="inputCode">
                    <div class="verification-code-container">
                        <el-input style="width: 200px" type="text" class="form-control" id="verification-code"
                            placeholder="请输入验证码" autocomplete="off" v-model.trim="registerItem.inputCode">
                        </el-input>
                        
                    </div>
                    <!-- 显示验证码图片 -->
                    <div v-if="verificationImageUrl" class="verification-image-container">
                        <img :src="verificationImageUrl" alt="验证码" class="verification-image" 
                             @click="refreshVerificationCode" 
                             title="点击刷新验证码" />
                    </div>
                </el-form-item>
                <el-form-item label="注册名称" prop="name">
                    <el-input style="width: 300px" type="text" class="form-control" id="name" placeholder="请输入注册名称"
                        autocomplete="off" v-model.trim="registerItem.name">
                    </el-input>
                </el-form-item>
                <el-form-item label="注册密码" prop="password">
                    <el-input style="width: 300px" type="password" class="form-control" id="password"
                        placeholder="请输入注册密码" autocomplete="off" v-model.trim="registerItem.password">
                    </el-input>
                </el-form-item>
                <el-form-item label="年龄" prop="age">
                    <el-input-number v-model="registerItem.age" :min="1" :max="100" />
                </el-form-item>
                <el-form-item label="性别" prop="gender">
                    <el-radio-group v-model="sex">
                        <el-radio :value="1" size="small">男</el-radio>
                        <el-radio :value="2" size="small">女</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="用户类型" prop="userType">
                    <el-radio-group v-model="registerItem.userType">
                        <el-radio :value="1" size="small">患者</el-radio>
                        <el-radio :value="2" size="small">医生</el-radio>
                    </el-radio-group>
                </el-form-item>
                
            </el-form>
            <div class="form-group">
                <el-button 
                    type="primary" 
                    class="register-btn" 
                    @click="onRegister"
                    :loading="loading"
                    :style="{
                        background: `linear-gradient(135deg, ${themeColors.buttonGradientStart} 0%, ${themeColors.buttonGradientEnd} 100%)`
                    }"
                >注册</el-button>
                <el-button 
                    class="reset-btn" 
                    @click="clearRegister"
                    :style="{
                        borderColor: themeColors.buttonGradientStart,
                        color: themeColors.buttonGradientStart
                    }"
                >重置</el-button>
            </div>
            <div class="login-link">
                <p>已有账号？<router-link :style="{ color: themeColors.buttonGradientStart }" to="/login">点击登录</router-link></p>
            </div>
        </div>
    </animated-background>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue"
import type { FormInstance } from "element-plus";
import router from "../router";
import { register, sendCode } from "../api/medical"; // 导入具体的API函数
import { ElMessage } from "element-plus";
import AnimatedBackground from '../components/AnimatedBackground.vue';

const formRef = ref<FormInstance | null>(null);
const sex = ref(1);
const loading = ref(false);

const rules = {
    name: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 1, max: 10, message: '用户名长度应在3到10个字符之间', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 12, message: '密码长度应在6到12个字符之间', trigger: 'blur' }
    ],
    age: [
        { required: true, message: '请输入年龄', trigger: 'blur' }
    ],
    userType: [
        { required: true, message: '请选择用户类型', trigger: 'change' }
    ],
    email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
    ],
    inputCode: [
        { required: true, message: '请输入验证码', trigger: 'blur' }
    ]
}

const registerItem = reactive({
    password: "",
    gender: "男",
    age: 0,
    name: "",
    userType: 1, // 默认选择患者
    email: "",
    code: "",
    inputCode: ""
})

const codeSending = ref(false);

// 添加验证码图片URL
const verificationImageUrl = ref('');

// 生成验证码图片的函数
const generateVerificationCodeImage = (code: string) => {
    // 创建Canvas元素
    const canvas = document.createElement('canvas');
    const ctx = canvas.getContext('2d');
    
    // 设置Canvas大小
    canvas.width = 120;
    canvas.height = 40;
    
    if (ctx) {
        // 设置背景色
        ctx.fillStyle = '#f0f2f5';
        ctx.fillRect(0, 0, canvas.width, canvas.height);
        
        // 绘制干扰线
        for (let i = 0; i < 3; i++) {
            ctx.strokeStyle = `rgb(${Math.floor(Math.random() * 200)}, ${Math.floor(Math.random() * 200)}, ${Math.floor(Math.random() * 200)})`;
            ctx.beginPath();
            ctx.moveTo(Math.random() * canvas.width, Math.random() * canvas.height);
            ctx.lineTo(Math.random() * canvas.width, Math.random() * canvas.height);
            ctx.stroke();
        }
        
        // 绘制干扰点
        for (let i = 0; i < 30; i++) {
            ctx.fillStyle = `rgb(${Math.floor(Math.random() * 200)}, ${Math.floor(Math.random() * 200)}, ${Math.floor(Math.random() * 200)})`;
            ctx.beginPath();
            ctx.arc(Math.random() * canvas.width, Math.random() * canvas.height, 1, 0, 2 * Math.PI);
            ctx.fill();
        }
        
        // 绘制验证码文字
        ctx.fillStyle = '#4a9e5c';
        ctx.font = 'bold 24px Arial';
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        ctx.fillText(code, canvas.width / 2, canvas.height / 2);
        
        // 返回Canvas数据URL
        return canvas.toDataURL('image/png');
    }
    
    return '';
};

const handleEmailBlur = () => {
    // 当邮箱输入完毕并且格式正确时，自动获取验证码
    if (registerItem.email && /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(registerItem.email)) {
        getVerificationCode();
    }
};

// 重命名函数，更符合当前功能
const getVerificationCode = () => {
    if (!registerItem.email) {
        ElMessage.warning("请先输入邮箱");
        return;
    }

    codeSending.value = true;
    
    // 调用后端API发送验证码
    sendCode(registerItem.email).then((res: any) => {
        if (res.code == 200){
            console.log(res.msg);
            registerItem.code = res.verificationCode;
            // 生成验证码图片并显示
            verificationImageUrl.value = generateVerificationCodeImage(registerItem.code);
        }
        codeSending.value = false;
    }).catch((error: any) => {
        console.log(error);
        codeSending.value = false;
    });
};

// 添加刷新验证码功能
const refreshVerificationCode = () => {
    getVerificationCode();
};

const onRegister = () => {
    if (sex.value == 1) registerItem.gender = "男"
    else registerItem.gender = "女"
    
    if (!formRef.value) {
        console.log('formRef is null');
        return;
    }
    formRef.value.validate((valid) => {
        if (valid) {
            // 验证验证码是否正确
            if (registerItem.inputCode !== registerItem.code) {
                alert("验证码错误，请重新输入");
                return;
            }
            loading.value = true;
            register(registerItem.name, registerItem.gender, 
                registerItem.age, 
                registerItem.password, 
                registerItem.userType,
                registerItem.email
            ).then((res: any) => {
                setTimeout(() => {
                    if (res.code == 200){
                        alert("注册成功!");
                        router.push("/login")
                    } else {
                        alert(res.msg);
                    }
                    loading.value = false;
                }, 1000);
            }).catch((error: any) => {
                console.log(error)
                loading.value = false;
            });
        }
    })
}

const clearRegister = () => {
    registerItem.name = "",
    registerItem.gender = "男",
    registerItem.age = 0,
    registerItem.password = '',
    registerItem.userType = 1,
    registerItem.email = '',
    registerItem.inputCode = '',
    registerItem.code = ''
}
</script>

<style scoped>
.is-register {
    position: relative;
    box-sizing: border-box;
    background: rgba(255, 255, 255, 0.9);
    width: 550px;
    min-height: 450px;
    display: flex;
    flex-flow: column;
    justify-content: space-around;
    align-items: center;
    padding: 40px;
    border-radius: 20px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.5);
    animation: slideUp 0.6s ease-out;
    z-index: 2;
}

@keyframes slideUp {
    from {
        opacity: 0;
        transform: translateY(20px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.title {
    color: #2c3e50;
    font-size: 28px;
    margin-bottom: 30px;
    font-weight: 600;
    text-align: center;
    position: relative;
    padding-bottom: 15px;
}

.title::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 60px;
    height: 3px;
    background: linear-gradient(90deg, var(--theme-button-start), var(--theme-button-end));
    border-radius: 3px;
    filter: saturate(10);
    animation: changeRegisterAccentLine 60s infinite alternate;
}

@keyframes changeRegisterAccentLine {
    0% {
        --register-accent-line-1: #1E88E5;
        --register-accent-line-2: #42A5F5;
    }

    20% {
        --register-accent-line-1: #2e7d32;
        --register-accent-line-2: #388e3c;
    }

    40% {
        --register-accent-line-1: #f4511e;
        --register-accent-line-2: #ff7043;
    }

    60% {
        --register-accent-line-1: #6a1b9a;
        --register-accent-line-2: #8e24aa;
    }

    80% {
        --register-accent-line-1: #0277bd;
        --register-accent-line-2: #039be5;
    }

    100% {
        --register-accent-line-1: #c62828;
        --register-accent-line-2: #d32f2f;
    }
}

.register-form {
    width: 100%;
    margin-bottom: 20px;
    z-index: 3;
}

:deep(.el-form-item) {
    margin-bottom: 22px;
}

:deep(.el-form-item__label) {
    font-weight: 500;
    color: #2c3e50;
}

:deep(.el-input__wrapper) {
    background: rgba(255, 255, 255, 0.8);
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
    transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

:deep(.el-input__wrapper.is-focus) {
    box-shadow: 0 0 0 1px #4a9e5c;
    transform: translateY(-1px);
}

:deep(.el-input-number) {
    width: 180px;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

:deep(.el-input-number:hover) {
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

:deep(.el-input-number.is-focus) {
    box-shadow: 0 0 0 1px #4a9e5c;
}

:deep(.el-radio__input.is-checked .el-radio__inner) {
    background: #4a9e5c;
    border-color: #4a9e5c;
}

:deep(.el-radio__label) {
    color: #2c3e50;
}

.form-group {
    display: flex;
    justify-content: center;
    gap: 15px;
    margin-top: 20px;
    position: relative;
    z-index: 3;
}

.register-btn,
.reset-btn {
    position: relative;
    z-index: 3;
}

.register-btn {
    min-width: 120px;
    height: 40px;
    font-size: 16px;
    border: none;
    color: white;
    border-radius: 8px;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
}

.register-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.register-btn:active {
    transform: translateY(0);
}

.reset-btn {
    min-width: 120px;
    height: 40px;
    font-size: 16px;
    background: white;
    border-radius: 8px;
    transition: all 0.3s ease;
}

.reset-btn:hover {
    transform: translateY(-2px);
}

.login-link {
    margin-top: 20px;
    font-size: 14px;
    text-align: center;
}

.login-link a {
    text-decoration: none;
    font-weight: 500;
    transition: all 0.3s ease;
    position: relative;
}

.login-link a:hover {
    opacity: 0.8;
}
</style>