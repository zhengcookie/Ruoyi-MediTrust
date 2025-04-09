<template>
    <div class="register">
        <div class="is-register">
            <h3 class="title">区块链医疗健康管理系统</h3>
            <el-form ref="formRef" :model="registerItem" :rules="rules" @submit.prevent="onRegister" style="max-width: 400px" label-width="auto" class="register-form">
                <el-form-item label="注册名称" prop="name">
                    <el-input style="width: 300px" type="text" class="form-control" id="name" placeholder="请输入注册名称"
                        autocomplete="off" v-model.trim="registerItem.name">
                    </el-input>
                </el-form-item>
                <el-form-item label="区块链地址" prop="address">
                    <el-input style="width: 300px" type="text" class="form-control" id="address"
                        placeholder="请输入区块链地址" autocomplete="off" v-model.trim="registerItem.address">
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
                >注册</el-button>
                <el-button 
                    class="reset-btn" 
                    @click="clearRegister"
                >重置</el-button>
            </div>
            <div class="login-link">
                <p>已有账号？<router-link to="/login">点击登录</router-link></p>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue"
import type { FormInstance } from "element-plus";
import axios from "axios";
import router from "../router";

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
    address: [
        { required: true, message: '请输入区块链地址', trigger: 'blur' },
        { pattern: /^0x[a-fA-F0-9]{40}$/, message: '请输入有效的区块链地址', trigger: 'blur' }
    ],
    age: [
        { required: true, message: '请输入年龄', trigger: 'blur' }
    ],
    userType: [
        { required: true, message: '请选择用户类型', trigger: 'change' }
    ]
}

const registerItem = reactive({
    password: "",
    gender: "男",
    address: "",
    age: 0,
    name: "",
    userType: 1 // 默认选择患者
})

const onRegister = () => {
    if (sex.value == 1) registerItem.gender = "男"
    else registerItem.gender = "女"
    console.log(registerItem)
    if (!formRef.value) {
        console.log('formRef is null');
        return;
    }
    formRef.value.validate((valid) => {
        if (valid) {
            loading.value = true;
            const apiPath = 'http://localhost:8085/register' ;
            axios.post(apiPath, registerItem).then((res) => {
                setTimeout(() => {
                    if (res.data.code == 200){
                        alert("注册成功!");
                        router.push("/login")
                    }else{
                        alert(res.data.msg);
                    }
                    loading.value = false;
                }, 1000);
            }).catch((error) => {
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
    registerItem.address = '',
    registerItem.password = '',
    registerItem.userType = 1
}
</script>

<style scoped>
.register {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, #f5f7fa 0%, #e4ecf7 100%);
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;
}

/* 装饰性背景元素 */
.register::before {
    content: '';
    position: absolute;
    width: 1000px;
    height: 1000px;
    background: linear-gradient(135deg, rgba(74, 158, 92, 0.1) 0%, rgba(33, 150, 243, 0.1) 100%);
    border-radius: 50%;
    top: -400px;
    left: -400px;
    animation: float 15s ease-in-out infinite;
    pointer-events: none;
    z-index: 0;
}

.register::after {
    content: '';
    position: absolute;
    width: 800px;
    height: 800px;
    background: linear-gradient(135deg, rgba(33, 150, 243, 0.1) 0%, rgba(74, 158, 92, 0.1) 100%);
    border-radius: 50%;
    bottom: -300px;
    right: -300px;
    animation: float 20s ease-in-out infinite reverse;
    pointer-events: none;
    z-index: 0;
}

@keyframes float {
    0%, 100% {
        transform: translate(0, 0);
    }
    50% {
        transform: translate(30px, -30px);
    }
}

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
    background: linear-gradient(90deg, #4a9e5c, #2196F3);
    border-radius: 3px;
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

.register-btn, .reset-btn {
    position: relative;
    z-index: 3;
}

.register-btn {
    min-width: 120px;
    height: 40px;
    font-size: 16px;
    background: linear-gradient(135deg, #4a9e5c 0%, #2196F3 100%);
    border: none;
    color: white;
    border-radius: 8px;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
}

.register-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 15px rgba(74, 158, 92, 0.3);
}

.register-btn::after {
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

.reset-btn {
    min-width: 120px;
    height: 40px;
    font-size: 16px;
    background: white;
    border: 1px solid #dcdfe6;
    color: #606266;
    border-radius: 8px;
    transition: all 0.3s ease;
}

.reset-btn:hover {
    border-color: #4a9e5c;
    color: #4a9e5c;
    transform: translateY(-2px);
}

.login-link {
    margin-top: 20px;
    font-size: 14px;
    text-align: center;
}

.login-link a {
    color: #4a9e5c;
    text-decoration: none;
    font-weight: 500;
    transition: all 0.3s ease;
    position: relative;
}

.login-link a::after {
    content: '';
    position: absolute;
    bottom: -2px;
    left: 0;
    width: 100%;
    height: 1px;
    background: #4a9e5c;
    transform: scaleX(0);
    transition: transform 0.3s ease;
}

.login-link a:hover::after {
    transform: scaleX(1);
}
</style>