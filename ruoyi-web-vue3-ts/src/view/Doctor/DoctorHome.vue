<template>
  <BaseLayout>
    <template #sidebar>
      <div class="menu-item" @click="router.push('/doctor/index')">
        <img src="@/assets/images/首页/u16.svg" alt="首页" />
        <router-link to="/doctor/index">首页</router-link>
      </div>
      <div class="menu-item" @click="router.push('/doctor/info')">
        <img src="@/assets/images/主页面/u152.svg" alt="个人信息" />
        <router-link to="/doctor/Info">个人信息</router-link>
      </div>
      <div class="menu-item" @click="router.push('/doctor/appointmentManagement')">
        <img src="@/assets/images/主页面/u159.svg" alt="挂号预约管理" />
        <router-link to="/doctor/appointmentManagement">挂号预约</router-link>
      </div>
      <div class="menu-item" @click="router.push('/doctor/records')">
        <img src="@/assets/images/主页面/u164.svg" alt="病历管理" />
        <router-link to="/doctor/records">病历管理</router-link>
      </div>
      <div class="menu-item" @click="router.push('/doctor/health-dashboard')">
        <el-icon size="36"><Monitor /></el-icon>
        <router-link to="/doctor/health-dashboard">健康大屏</router-link>
      </div>
    </template>
    <router-view></router-view>
  </BaseLayout>
</template>

<script setup lang="ts">
import router from '@/router';
import BaseLayout from '@/components/BaseLayout.vue';
import { DataAnalysis, Monitor } from '@element-plus/icons-vue';

const route = router.currentRoute.value;
</script>

<style scoped>
.menu-item {
  padding: 20px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.menu-item:hover {
  background-color: var(--menu-hover);
}

.menu-item img {
  width: 36px;
  height: 36px;
  transition: transform 0.3s ease;
}

.menu-item:hover img {
  transform: scale(1.1);
}

.menu-item a {
  font-size: 13px;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
  color: #ffffff;
}
</style>