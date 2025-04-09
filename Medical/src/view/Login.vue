<template>
    <div class="login">
        <div class="is-login">
            <h3 class="title">区块链医疗健康管理系统</h3>
            <!-- 使用 ref 获取表单实例 -->
            <el-form ref="formRef" :model="loginItem" :rules="rules" @submit.prevent="onLogin" style="max-width: 400px"
                label-width="auto" class="login-form">
                <el-form-item label="登录地址" prop="address">
                    <el-input style="width: 300px" type="text" class="form-control" id="address" placeholder="请输入登录地址"
                        autocomplete="off" v-model.trim="loginItem.address">
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
                <el-button type="primary" class="login-btn" @click.stop="onLogin"
                    :loading="loading">登录</el-button>
                <el-button class="reset-btn" @click.stop="clearLogin">重置</el-button>
            </div>
            <div class="check">
                <p><router-link to="/register" class="register-link">立即注册</router-link></p>
                <p class="forgot-password">忘记密码？</p>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { FormInstance } from 'element-plus';  // 引入FormInstance类型
import axios from "axios";
import { useRouter } from "vue-router";

// 响应式对象，存储登录信息
const loginItem = reactive({
    address: "",
    password: "",
    userType: "1" // 默认为病人登录
});

// 表单验证规则
const rules = {
    address: [
        { required: true, message: '请输入登录地址', trigger: 'blur' },
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

// 懒加载路由
const router = useRouter();

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

            // 发送登录请求
            axios.get('http://localhost:8085/login', {
                params: {
                    address: loginItem.address,
                    password: loginItem.password,
                    type: loginItem.userType
                }
            }).then((res) => {
                setTimeout(() => {
                    if (res.data.code == 200){
                        if (loginItem.userType === '2') {
                        localStorage.setItem("accountAddress", res.data.accountAddress);
                        localStorage.setItem("name", res.data.name);
                        router.push("/doctor");
                    } else {
                        localStorage.setItem("accountAddress", res.data.accountAddress);
                        localStorage.setItem("name", res.data.name);
                        router.push("/patient");
                    }
                    alert("登录成功!");
                    console.log(res.data.msg);
                    loading.value = false;
                    }else{
                       alert(res.data.msg);
                       loading.value = false;
                    }
                    // 根据用户类型存储信息和跳转
                }, 1000);
            }).catch((error) => {
                console.error(error);
                loading.value = false;
            });
        } else {
            console.log('表单验证失败');
        }
    });
};

// 重置登录信息
const clearLogin = () => {
    loginItem.address = "";
    loginItem.password = "";
    loginItem.userType = "1";
};
</script>

<style scoped>
.login {
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
.login::before {
    content: '';
    position: absolute;
    width: 1000px;
    height: 1000px;
    background: linear-gradient(135deg, rgba(74, 158, 92, 0.1) 0%, rgba(33, 150, 243, 0.1) 100%);
    border-radius: 50%;
    top: -400px;
    right: -400px;
    animation: float 15s ease-in-out infinite;
    pointer-events: none;
    z-index: 0;
}

.login::after {
    content: '';
    position: absolute;
    width: 800px;
    height: 800px;
    background: linear-gradient(135deg, rgba(33, 150, 243, 0.1) 0%, rgba(74, 158, 92, 0.1) 100%);
    border-radius: 50%;
    bottom: -300px;
    left: -300px;
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
    background: linear-gradient(90deg, #4a9e5c, #2196F3);
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
    margin: 25px 0;
    display: flex;
    justify-content: center;
    gap: 15px;
    position: relative;
    z-index: 3;
}

.login-btn, .reset-btn {
    position: relative;
    z-index: 3;
}

.login-btn {
    width: 120px;
    height: 40px;
    font-size: 16px;
    background: linear-gradient(135deg, #4a9e5c 0%, #2196F3 100%);
    border: none;
    color: white;
    border-radius: 8px;
    transition: all 0.3s ease;
}

.login-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 15px rgba(74, 158, 92, 0.3);
}

.login-btn::after {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.1), transparent);
    transform: rotate(45deg);
    animation: shine 3s infinite;
    pointer-events: none;
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
    width: 120px;
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

.check {
    width: 300px;
    display: flex;
    justify-content: space-between;
    font-size: 14px;
    margin-top: 20px;
    position: relative;
    z-index: 3;
}

.register-link {
    text-decoration: none;
    color: #4a9e5c;
    font-weight: 500;
    transition: all 0.3s ease;
    position: relative;
}

.register-link::after {
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

.register-link:hover::after {
    transform: scaleX(1);
}

.forgot-password {
    color: #909399;
    cursor: pointer;
    transition: all 0.3s ease;
    position: relative;
}

.forgot-password:hover {
    color: #606266;
}

.forgot-password::after {
    content: '';
    position: absolute;
    bottom: -2px;
    left: 0;
    width: 100%;
    height: 1px;
    background: #909399;
    transform: scaleX(0);
    transition: transform 0.3s ease;
}

.forgot-password:hover::after {
    transform: scaleX(1);
}
</style>
