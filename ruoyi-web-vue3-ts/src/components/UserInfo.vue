<template>
  <el-dropdown @command="handleCommand" trigger="click">
    <div class="avatar-container">
      <img :src="savedPreview || '/images/default-avatar.png'" alt="avatar" class="avatar" />
      <span class="username">{{ username }}</span>
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="profile">
          <el-icon><User /></el-icon>个人主页
        </el-dropdown-item>
        <el-dropdown-item command="settings">
          <el-icon><Setting /></el-icon>设置
        </el-dropdown-item>
        <el-dropdown-item divided command="logout">
          <el-icon><SwitchButton /></el-icon>退出登录
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
// import { ref } from 'vue';
import router from '@/router';
import { User, Setting, SwitchButton } from '@element-plus/icons-vue';

const route = router.currentRoute.value;
const username = localStorage.getItem("name") || "";
const savedPreview = localStorage.getItem("avatarPreview") || "";

const emit = defineEmits(['settings']);

const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/doctor/info');
      break;
    case 'settings':
      emit('settings');
      break;
    case 'logout':
      logout();
      break;
  }
};

const logout = () => {
  localStorage.removeItem("accountAddress");
  localStorage.removeItem("name");

  // localStorage.removeItem("avatarPreview");
  localStorage.removeItem("userId");

  router.push("/login");
};

defineExpose({
  username,
  savedPreview,
  handleCommand,
  logout
});
</script>

<style scoped>
.avatar-container {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 20px;
  transition: background-color 0.3s;
}

.avatar-container:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.username {
  font-size: 14px;
  color: var(--text-color);
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 2px solid var(--primary-color);
}

.el-dropdown-menu__item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.el-dropdown-menu__item .el-icon {
  font-size: 16px;
}
</style> 