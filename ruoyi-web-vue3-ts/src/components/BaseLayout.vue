<template>
  <div id="base" :class="currentTheme">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="logo">
        <img src="@/assets/images/首页/u12.png" alt="logo" />
        <h1>云链·医问</h1>
      </div>
      <div class="user-info">
        <el-dropdown @command="handleThemeChange" trigger="click">
          <el-button type="primary" class="theme-button">
            <el-icon>
              <Brush />
            </el-icon>
            主题设置
          </el-button>
          <template #dropdown>
            <el-dropdown-menu class="theme-dropdown">
              <div class="theme-grid">
                <el-dropdown-item v-for="theme in themes" :key="theme.name" :command="theme.name" class="theme-item">
                  <div class="theme-preview">
                    <div class="color-preview">
                      <div class="color-block primary" :style="{ background: theme.primary }"></div>
                      <div class="color-block secondary" :style="{ background: theme.secondary }"></div>
                    </div>
                    <span class="theme-label">{{ theme.label }}</span>
                  </div>
                </el-dropdown-item>
              </div>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <img src="@/assets/images/主页面/u125.png" alt="notification" />
        <img src="@/assets/images/主页面/u123.png" alt="message" />
        <UserInfo @settings="showSettings" />
      </div>
    </div>

    <!-- 侧边导航栏 -->
    <div class="sidebar">
      <slot name="sidebar"></slot>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <slot></slot>
    </div>

    <!-- 设置对话框 -->
    <SettingsDialog v-model:visible="settingsVisible" @save="handleSettingsSave" />
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from 'vue';
// import { Brush } from '@element-plus/icons-vue';
import { themes, defaultTheme } from '../constants/themes';
import { applyTheme } from '../utils/theme';
import UserInfo from './UserInfo.vue';
import SettingsDialog from './SettingsDialog.vue';

export default defineComponent({
  name: 'BaseLayout',
  components: {
    UserInfo,
    SettingsDialog
  },
  setup() {
    // const savedPreview = localStorage.getItem("avatarPreview");
    const currentTheme = ref(defaultTheme.name);
    const settingsVisible = ref(false);

    const handleThemeChange = (themeName: string) => {
      const theme = themes.find(t => t.name === themeName);
      if (theme) {
        currentTheme.value = themeName;
        applyTheme(theme.name);
        localStorage.setItem('theme', themeName);
      }
    };

    const showSettings = () => {
      settingsVisible.value = true;
      console.log('showSettings');
    };

    const handleSettingsSave = (settings: any) => {
      // 处理设置保存
      console.log('Settings saved:', settings);
    };

    onMounted(() => {
      const savedTheme = localStorage.getItem('theme');
      if (savedTheme) {
        const theme = themes.find(t => t.name === savedTheme);
        if (theme) {
          currentTheme.value = savedTheme;
          applyTheme(theme.name);
        }
      }
    });

    return {
      currentTheme,
      settingsVisible,
      handleThemeChange,
      showSettings,
      handleSettingsSave,
      themes
    };
  }
});
</script>

<style>
/* 定义CSS变量 */
:root {
  --primary-color: #4a9e5c;
  --secondary-color: #3d8a4e;
  --background-gradient-start: #f5f7fa;
  --background-gradient-end: #e4ecf7;
  --text-color: #2c3e50;
  --text-color-secondary: #606266;
  --header-text-color: #2c3e50;
  --card-background: rgba(255, 255, 255, 0.95);
  --header-background: linear-gradient(90deg, #ffffff, var(--background-gradient-start));
  --menu-hover: rgba(0, 0, 0, 0.1);
  --table-text-color: #2c3e50;
  --table-header-color: #606266;
  --table-row-hover: #f5f7fa;
}

/* 基础布局样式 */
#base {
  min-height: 100vh;
  background: linear-gradient(135deg, var(--background-gradient-start) 0%, var(--background-gradient-end) 100%);
  display: flex;
}

.header {
  position: fixed;
  top: 0;
  left: 80px;
  right: 0;
  height: 70px;
  background: var(--header-background);
  padding: 0 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  z-index: 1000;
  transition: all 0.3s ease;
}

.logo {
  display: flex;
  align-items: center;
  gap: 20px;
}

.logo img {
  width: 60px;
  height: 60px;
  transition: transform 0.3s ease;
}

.logo img:hover {
  transform: scale(1.05);
}

.logo h1 {
  font-size: 30px;
  font-weight: 700;
  color: var(--header-text-color);
  margin: 0;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 28px;
}

.user-info img {
  width: 26px;
  height: 26px;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.user-info img:hover {
  transform: scale(1.1);
}

.user-info .avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 2px solid var(--primary-color);
}

.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: 80px;
  background: linear-gradient(180deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  padding-top: 70px;
  box-shadow: 2px 0 8px rgba(0, 21, 41, 0.15);
  z-index: 999;
  transition: all 0.3s ease;
}

.main-content {
  margin-left: 80px;
  margin-top: 70px;
  padding: 30px;
  flex: 1;
  background-color: var(--background-gradient-start);
  min-height: calc(100vh - 70px);
}

/* 主题切换相关样式 */
.theme-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 20px;
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  border: none;
  color: white;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.theme-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.theme-dropdown {
  padding: 16px;
  border-radius: 12px;
  min-width: 280px;
}

.theme-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}

.theme-item {
  margin: 0;
  padding: 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.theme-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.color-preview {
  display: flex;
  gap: 4px;
}

.color-block {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  transition: transform 0.3s ease;
}

.color-block.primary {
  transform: rotate(-15deg);
}

.color-block.secondary {
  transform: rotate(15deg);
}

.theme-item:hover .color-block.primary {
  transform: rotate(-5deg) scale(1.1);
}

.theme-item:hover .color-block.secondary {
  transform: rotate(5deg) scale(1.1);
}

.theme-label {
  font-size: 14px;
  color: var(--text-color);
  font-weight: 500;
}

/* 深色主题特殊处理 */
.dark {
  --text-color: #ffffff;
  --text-color-secondary: rgba(255, 255, 255, 0.9);
  --header-text-color: #ffffff;
}

/* 响应式优化 */
@media (max-width: 768px) {
  .theme-grid {
    grid-template-columns: 1fr;
  }

  .header {
    padding: 0 16px;
  }

  .logo h1 {
    font-size: 20px;
  }
}
</style> 