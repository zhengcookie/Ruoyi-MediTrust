<template>
    <animated-background v-slot="{ themeColors }">
        <div class="is-login">
            <h3 class="title">云链·医问</h3>
            <!-- 使用 ref 获取表单实例 -->
            <el-form ref="formRef" :model="loginItem" :rules="rules" @submit.prevent="onLogin" style="max-width: 400px"
                label-width="auto" class="login-form">
                <el-form-item label="用户名称" prop="name">
                    <el-input style="width: 300px" type="text" class="form-control" id="name" placeholder="请输入用户名称"
                        autocomplete="off" v-model.trim="loginItem.name">
                    </el-input>
                </el-form-item>

                <el-form-item label="登录密码" prop="password">
                    <el-input style="width: 300px" type="password" class="form-control" id="password"
                        placeholder="请输入登录密码" autocomplete="off" v-model.trim="loginItem.password">
                    </el-input>
                </el-form-item>

                <el-form-item label="用户类型" prop="userType">
                    <el-radio-group v-model="loginItem.userType">
                        <el-radio label="1">病人</el-radio>
                        <el-radio label="2">医生</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
            <!-- 登录按钮 -->
            <div class="form-group">
                <!-- 点击时触发表单验证 -->
                <el-button 
                    type="primary" 
                    class="login-btn" 
                    @click.stop="onLogin"
                    :loading="loading"
                    :style="{
                        background: `linear-gradient(135deg, ${themeColors.buttonGradientStart} 0%, ${themeColors.buttonGradientEnd} 100%)`
                    }"
                >登录</el-button>
                <el-button 
                    class="reset-btn" 
                    @click.stop="clearLogin"
                    :style="{
                        borderColor: themeColors.buttonGradientStart,
                        color: themeColors.buttonGradientStart
                    }"
                >重置</el-button>
            </div>
            <div class="check">
                <p><router-link :style="{ color: themeColors.buttonGradientStart }" to="/register" class="register-link">立即注册</router-link></p>
                <p><router-link  to="/forgot-password" class="forgot-password">忘记密码？</router-link></p>
            </div>
        </div>
    </animated-background>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import type { FormInstance } from 'element-plus';  // 引入FormInstance类型
import router from "@/router";
import { login } from "../api/medical"; // 导入登录API函数
import AnimatedBackground from '../components/AnimatedBackground.vue';

// 响应式对象，存储登录信息
const loginItem = reactive({
    name: "",
    password: "",
    userType: "1" // 默认为病人登录
});

// 表单验证规则
const rules = {
    name: [
        { required: true, message: '请输入用户名称', trigger: 'blur' },
    ],
    password: [
        { required: true, message: '请输入登录密码', trigger: 'blur' },
        { min: 6, max: 12, message: '密码长度应在 6 到 12 个字符之间', trigger: 'blur' }
    ],
    userType: [
        { required: true, message: '请选择用户类型', trigger: 'change' }
    ]
};

// 使用ref创建表单实例
const formRef = ref<FormInstance | null>(null);

// loading状态
const loading = ref(false);

// 登录逻辑
const onLogin = () => {
    // 获取表单实例
    if (!formRef.value) {
        console.log('formRef is null');
        return;  // 确保formRef有效
    }

    // 执行表单验证
    formRef.value.validate((valid) => {
        if (valid) {
            // 设置loading状态为true
            loading.value = true;

            // 使用API进行登录
            login(loginItem.name, parseInt(loginItem.userType), loginItem.password)
                .then((res: any) => {
                    // 移除延时处理，直接处理响应
                    console.log('登录响应:', res); // 查看响应结构
                    
                    // 直接访问res的code，而不是res.data.code
                    if (res.code == 200){
                        // 确保res中有必要的用户数据
                        if (!res.id || !res.address || !res.name) {
                            alert("登录响应数据不完整，请联系管理员");
                            loading.value = false;
                            return;
                        }
                        
                        // 存储用户数据
                        localStorage.setItem("userId", res.id);
                        localStorage.setItem("accountAddress", res.address);
                        localStorage.setItem("name", res.name);
                        
                        // 根据用户类型跳转
                        if (loginItem.userType === '2') {
                            localStorage.setItem("userType", "2");
                            router.push("/doctor");
                        } else {
                            localStorage.setItem("userType", "1");
                            router.push("/patient");
                        }
                        
                        alert("登录成功!");
                        console.log(res.msg);
                    } else {
                        // 处理错误响应
                        alert(res.msg || "登录失败，请检查用户名和密码");
                    }
                    loading.value = false;
                }).catch((error: any) => {
                    console.error('登录错误:', error);
                    loading.value = false;
                    
                    // 简化错误处理，直接提示用户名或密码错误
                    if (typeof error === 'string') {
                        alert("密码错误或用户名错误");
                    } else {
                        alert("登录失败，请检查用户名和密码");
                    }
                });
        } else {
            console.log('表单验证失败');
        }
    });
};

// 重置登录信息
const clearLogin = () => {
    loginItem.name = "";
    loginItem.password = "";
    loginItem.userType = "1";
};
</script>

<style scoped>
.is-login {
    position: relative;
    box-sizing: border-box;
    background: rgba(255, 255, 255, 0.9);
    width: 550px;
    min-height: 400px;
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
}

.login-form {
    width: 100%;
    margin: 20px 0;
    position: relative;
    z-index: 3;
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
    box-shadow: 0 0 0 1px var(--theme-button-start);
    transform: translateY(-1px);
}

:deep(.el-radio__input.is-checked .el-radio__inner) {
    background: var(--theme-button-start);
    border-color: var(--theme-button-start);
}

:deep(.el-radio__label) {
    color: #2c3e50;
}

.form-group {
    margin: 25px 0;
    display: flex;
    justify-content: center;
    gap: 15px;
    position: relative;
    z-index: 3;
}

.login-btn,
.reset-btn {
    position: relative;
    z-index: 3;
    min-width: 120px;
    height: 40px;
    font-size: 16px;
    border-radius: 8px;
    transition: all 0.3s ease;
}

.login-btn {
    border: none;
    color: white;
}

.login-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.login-btn:active {
    transform: translateY(0);
}

.reset-btn {
    background: white;
}

.reset-btn:hover {
    transform: translateY(-2px);
}

.check {
    width: 300px;
    display: flex;
    justify-content: space-between;
    font-size: 14px;
    margin-top: 20px;
    position: relative;
    z-index: 3;
}

.register-link,
.forgot-password {
    text-decoration: none;
    font-weight: 500;
    transition: all 0.3s ease;
    position: relative;
}

.register-link:hover,
.forgot-password:hover {
    opacity: 0.8;
}
</style>
