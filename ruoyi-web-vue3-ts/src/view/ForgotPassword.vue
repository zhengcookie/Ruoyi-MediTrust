<template>
    <animated-background v-slot="{ themeColors }">
        <div class="forgot-password">
            <div class="is-forgot-password">
                <h3 class="title">云链·医问</h3>
                <h4 class="subtitle">忘记密码</h4>
                <el-form ref="formRef" :model="forgotPasswordItem" :rules="rules" @submit.prevent="onSubmit" style="max-width: 400px" label-width="auto" class="forgot-password-form">
                    <el-form-item label="用户名" prop="userName">
                        <el-input style="width: 300px" type="text" class="form-control" id="userName"
                            placeholder="请输入用户名" autocomplete="off" v-model.trim="forgotPasswordItem.userName">
                        </el-input>
                    </el-form-item>
                    <el-form-item label="用户类型" prop="userType">
                        <el-radio-group v-model="forgotPasswordItem.userType">
                            <el-radio :value="1" size="small">患者</el-radio>
                            <el-radio :value="2" size="small">医生</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="邮箱" prop="email">
                        <el-input style="width: 300px" type="email" class="form-control" id="email"
                            placeholder="请输入邮箱" autocomplete="off" v-model.trim="forgotPasswordItem.email" @blur="handleEmailBlur">
                        </el-input>
                    </el-form-item>
                    <el-form-item label="验证码" prop="inputCode">
                        <div class="verification-code-container">
                            <el-input style="width: 200px" type="text" class="form-control" id="verification-code"
                                placeholder="请输入验证码" autocomplete="off" v-model.trim="forgotPasswordItem.inputCode">
                            </el-input>
                        </div>
                        <!-- 显示验证码图片 -->
                        <div v-if="verificationImageUrl" class="verification-image-container">
                            <img :src="verificationImageUrl" alt="验证码" class="verification-image" 
                                 @click="refreshVerificationCode" 
                                 title="点击刷新验证码" />
                        </div>
                    </el-form-item>
                    <el-form-item label="新密码" prop="password">
                        <el-input style="width: 300px" type="password" class="form-control" id="password"
                            placeholder="请输入新密码" autocomplete="off" v-model.trim="forgotPasswordItem.password">
                        </el-input>
                    </el-form-item>
                    <el-form-item label="确认密码" prop="confirmPassword">
                        <el-input style="width: 300px" type="password" class="form-control" id="confirmPassword"
                            placeholder="请确认新密码" autocomplete="off" v-model.trim="forgotPasswordItem.confirmPassword">
                        </el-input>
                    </el-form-item>
                </el-form>
                <div class="form-group">
                    <el-button 
                        type="primary" 
                        class="submit-btn" 
                        @click="onSubmit"
                        :loading="loading"
                        :style="{
                            background: `linear-gradient(135deg, ${themeColors.buttonGradientStart} 0%, ${themeColors.buttonGradientEnd} 100%)`
                        }"
                    >重置密码</el-button>
                    <el-button 
                        class="reset-btn" 
                        @click="clearForm"
                        :style="{
                            borderColor: themeColors.buttonGradientStart,
                            color: themeColors.buttonGradientStart
                        }"
                    >重置</el-button>
                </div>
                <div class="login-link">
                    <p>想起密码？<router-link :style="{ color: themeColors.buttonGradientStart }" to="/login">点击登录</router-link></p>
                </div>
            </div>
        </div>
    </animated-background>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue"
import type { FormInstance, FormRules } from "element-plus";
import router from "../router";
import { sendCode, forgetPassword } from "../api/medical"; // 导入具体的API函数
import { ElMessage } from "element-plus";
import AnimatedBackground from '../components/AnimatedBackground.vue';

const formRef = ref<FormInstance | null>(null);
const loading = ref(false);
const codeSending = ref(false);
const codeButtonText = ref("发送验证码");
// 添加验证码图片URL
const verificationImageUrl = ref('');

// 表单验证规则
const validatePass2 = (rule: any, value: any, callback: any) => {
    if (value === '') {
        callback(new Error('请再次输入密码'))
    } else if (value !== forgotPasswordItem.password) {
        callback(new Error('两次输入密码不一致!'))
    } else {
        callback()
    }
}

const rules = reactive<FormRules>({
    userName: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 1, max: 10, message: '用户名长度应在1到10个字符之间', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, max: 12, message: '密码长度应在6到12个字符之间', trigger: 'blur' }
    ],
    confirmPassword: [
        { required: true, message: '请确认新密码', trigger: 'blur' },
        { validator: validatePass2, trigger: 'blur' }
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
})

const forgotPasswordItem = reactive({
    userName: "",
    password: "",
    confirmPassword: "",
    userType: 1, // 默认选择患者
    email: "",
    code: "",
    inputCode: ""
})

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

// 当邮箱输入完毕时自动获取验证码
const handleEmailBlur = () => {
    // 当邮箱输入完毕并且格式正确时，自动获取验证码
    if (forgotPasswordItem.email && /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(forgotPasswordItem.email)) {
        getVerificationCode();
    }
};

// 获取验证码函数
const getVerificationCode = () => {
    if (!forgotPasswordItem.email) {
        ElMessage.warning("请先输入邮箱");
        return;
    }

    codeSending.value = true;
    
    // 调用后端API发送验证码
    sendCode(forgotPasswordItem.email).then((res: any) => {
        if (res.code == 200){
            forgotPasswordItem.code = res.verificationCode;
            
            // 生成验证码图片并显示
            verificationImageUrl.value = generateVerificationCodeImage(forgotPasswordItem.code);
        }
        codeSending.value = false;
    }).catch((error: any) => {
        console.log(error);
        codeSending.value = false;
    });
};

// 刷新验证码
const refreshVerificationCode = () => {
    getVerificationCode();
};

const onSubmit = () => {
    if (!formRef.value) {
        console.log('formRef is null');
        return;
    }
    
    formRef.value.validate((valid) => {
        if (valid) {
            // 验证验证码是否正确
            if (forgotPasswordItem.inputCode !== forgotPasswordItem.code) {
                alert("验证码错误，请重新输入");
                return;
            }
            
            loading.value = true;
            forgetPassword(
                forgotPasswordItem.userName,
                forgotPasswordItem.userType,
                forgotPasswordItem.password
            ).then((res: any) => {
                setTimeout(() => {
                    if (res.code == 200){
                        alert("密码重置成功!");
                        router.push("/login")
                    } else {
                        alert(res.data.msg);
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

const clearForm = () => {
    forgotPasswordItem.userName = "";
    forgotPasswordItem.password = "";
    forgotPasswordItem.confirmPassword = "";
    forgotPasswordItem.userType = 1;
    forgotPasswordItem.email = "";
    forgotPasswordItem.inputCode = "";
    forgotPasswordItem.code = "";
}
</script>

<style scoped>
.forgot-password {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;
    z-index: 1;
}

.is-forgot-password {
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
    margin-bottom: 10px;
    font-weight: 600;
    text-align: center;
    position: relative;
}

.subtitle {
    color: #5a6d82;
    font-size: 18px;
    margin-bottom: 30px;
    font-weight: 500;
    text-align: center;
    position: relative;
    padding-bottom: 15px;
}

.subtitle::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 60px;
    height: 3px;
    background: linear-gradient(90deg, var(--theme-button-start), var(--theme-button-end));
    border-radius: 3px;
}

.forgot-password-form {
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

.submit-btn, .reset-btn {
    position: relative;
    z-index: 3;
}

.submit-btn {
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

.submit-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.submit-btn:active {
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

.verification-code-container {
    display: flex;
    align-items: center;
    gap: 10px;
}

.verification-image-container {
    margin-top: 10px;
    display: flex;
    justify-content: center;
}

.verification-image {
    border-radius: 4px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
    cursor: pointer; /* 添加鼠标指针样式 */
}

.verification-image:hover {
    transform: scale(1.05);
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
    filter: brightness(1.05); /* 鼠标悬浮时微微变亮 */
}

/* 添加点击效果 */
.verification-image:active {
    transform: scale(0.98);
    filter: brightness(0.95);
}
@keyframes changeForgotPasswordLink {
    0% {
        --forgot-password-link-color: #1E88E5;
    }

    20% {
        --forgot-password-link-color: #2e7d32;
    }

    40% {
        --forgot-password-link-color: #f4511e;
    }

    60% {
        --forgot-password-link-color: #6a1b9a;
    }

    80% {
        --forgot-password-link-color: #0277bd;
    }

    100% {
        --forgot-password-link-color: #c62828;
    }
}

/* 移除旧的背景装饰，因为现在使用 AnimatedBackground 组件 */
.forgot-password::before,
.forgot-password::after {
    display: none;
}
</style> 