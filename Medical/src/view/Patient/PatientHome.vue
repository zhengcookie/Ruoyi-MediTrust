<template>
  <div id="base" :class="currentTheme">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="logo">
        <img src="../../assets/images/首页/u12.png" alt="logo" />
        <h1>区块链医疗健康管理系统</h1>
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
        <img src="../../assets/images/主页面/u125.png" alt="notification" />
        <img src="../../assets/images/主页面/u123.png" alt="message" />
        <img src="../../assets/images/主页面/u119.jpg" alt="avatar" class="avatar" />
        <div class="user-dropdown" @click="toggleDropdown">
          <span>{{ username }}</span>
          <el-button type="info" @click="logout" style="margin-left: 10px">退出</el-button>
        </div>
      </div>
    </div>

    <!-- 侧边导航栏 -->
    <div class="sidebar">
      <div class="menu-item" @click="router.push('/patient/personalInformation')">
        <img src="../../assets/images/主页面/u152.svg" alt="个人信息" />
        <router-link to="/patient/personalInformation">个人信息</router-link>
      </div>
<!-- 
      <div class="menu-item" @click="router.push('/patient/registeredmanagement')">
        <img src="../../assets/images/主页面/u159.svg" alt="挂号管理" />
        <router-link to="/patient/registeredmanagement">挂号管理</router-link>
      </div> -->

      <div class="menu-item" @click="router.push('/patient/reserveinfo')">
        <img src="../../assets/images/主页面/u159.svg" alt="挂号预约管理" />
        <router-link to="/patient/reserveinfo">挂号预约</router-link>
      </div>

      <div class="menu-item" @click="router.push('/patient/myMedical')">
        <img src="../../assets/images/主页面/u164.svg" alt="病历管理" />
        <router-link to="/patient/myMedical">病历管理</router-link>
      </div>
<!-- 
      <div class="menu-item" @click="router.push('/patient/permission')">
        <img src="../../assets/images/主页面/u171.svg" alt="权限管理" />
        <router-link to="/patient/permission">权限管理</router-link>
      </div> -->
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { ref, onMounted } from 'vue';
import { Brush } from '@element-plus/icons-vue';

const router = useRouter();
const isDropdownOpen = ref(false);
const username = localStorage.getItem("name") || "";

// 主题相关
const currentTheme = ref('default');
const customColor = ref('#4a9e5c');

const themes = [
  {
    name: 'default',
    label: '清新绿',
    primary: '#4a9e5c',
    secondary: '#3d8a4e',
    gradientStart: '#f5f7fa',
    gradientEnd: '#e4ecf7',
    textColor: '#2c3e50',
    textColorSecondary: '#606266',
    headerTextColor: '#2c3e50'
  },
  {
    name: 'warm',
    label: '暖阳橙',
    primary: '#f4511e',
    secondary: '#ff7043',
    gradientStart: '#fff3e0',
    gradientEnd: '#ffe0b2',
    textColor: '#333333',
    textColorSecondary: '#666666'
  },
  {
    name: 'elegant',
    label: '优雅紫',
    primary: '#6a1b9a',
    secondary: '#8e24aa',
    gradientStart: '#f3e5f5',
    gradientEnd: '#e1bee7',
    textColor: '#2c2c2c',
    textColorSecondary: '#4a4a4a'
  },
  {
    name: 'ocean',
    label: '海洋蓝',
    primary: '#0277bd',
    secondary: '#039be5',
    gradientStart: '#e1f5fe',
    gradientEnd: '#b3e5fc',
    textColor: '#263238',
    textColorSecondary: '#455a64'
  },
  {
    name: 'forest',
    label: '森林绿',
    primary: '#2e7d32',
    secondary: '#388e3c',
    gradientStart: '#e8f5e9',
    gradientEnd: '#c8e6c9',
    textColor: '#1b5e20',
    textColorSecondary: '#33691e'
  },
  {
    name: 'sunset',
    label: '日落红',
    primary: '#c62828',
    secondary: '#d32f2f',
    gradientStart: '#ffebee',
    gradientEnd: '#ffcdd2',
    textColor: '#b71c1c',
    textColorSecondary: '#c62828'
  },
  {
    name: 'tech',
    label: '科技蓝',
    primary: '#0288d1',
    secondary: '#03a9f4',
    gradientStart: '#e1f5fe',
    gradientEnd: '#b3e5fc',
    textColor: '#01579b',
    textColorSecondary: '#0277bd'
  },
  {
    name: 'nature',
    label: '自然棕',
    primary: '#795548',
    secondary: '#8d6e63',
    gradientStart: '#efebe9',
    gradientEnd: '#d7ccc8',
    textColor: '#3e2723',
    textColorSecondary: '#4e342e'
  }
];

// 修改主题切换处理函数
const handleThemeChange = (themeName: string) => {
  if (themeName === 'custom') {
    applyCustomTheme(customColor.value);
  } else {
    const theme = themes.find(t => t.name === themeName);
    if (theme) {
      currentTheme.value = themeName;
      document.documentElement.style.setProperty('--primary-color', theme.primary);
      document.documentElement.style.setProperty('--secondary-color', theme.secondary);
      document.documentElement.style.setProperty('--background-gradient-start', theme.gradientStart);
      document.documentElement.style.setProperty('--background-gradient-end', theme.gradientEnd);
      document.documentElement.style.setProperty('--text-color', theme.textColor);
      document.documentElement.style.setProperty('--text-color-secondary', theme.textColorSecondary);
      document.documentElement.style.setProperty('--header-text-color', theme.headerTextColor || '#2c3e50');
      document.documentElement.style.setProperty('--card-background', 'rgba(255, 255, 255, 0.95)');
      document.documentElement.style.setProperty('--header-background', 'linear-gradient(90deg, #ffffff, var(--background-gradient-start))');
      document.documentElement.style.setProperty('--menu-hover', 'rgba(0, 0, 0, 0.1)');
      document.documentElement.style.setProperty('--table-text-color', '#2c3e50');
      document.documentElement.style.setProperty('--table-header-color', '#606266');
      document.documentElement.style.setProperty('--table-row-hover', '#f5f7fa');

    }
  }
  localStorage.setItem('theme', themeName);
  localStorage.setItem('themeColor', customColor.value);
};

// 处理自定义颜色变化
// const handleColorChange = (color: string) => {
//   customColor.value = color;
//   applyCustomTheme(color);
// };

// 应用自定义主题
const applyCustomTheme = (color: string) => {
  document.documentElement.style.setProperty('--primary-color', color);
  document.documentElement.style.setProperty('--secondary-color', adjustColor(color, 20));
  document.documentElement.style.setProperty('--background-gradient-start', adjustColor(color, 90));
  document.documentElement.style.setProperty('--background-gradient-end', adjustColor(color, 95));
  // 为自定义主题生成对比度良好的文字颜色
  const textColor = getContrastYIQ(color);
  document.documentElement.style.setProperty('--text-color', textColor);
  document.documentElement.style.setProperty('--text-color-secondary', adjustOpacity(textColor, 0.85));
};

// 根据背景色计算对比度最佳的文字颜色
const getContrastYIQ = (hexcolor: string) => {
  const r = parseInt(hexcolor.substring(1, 3), 16);
  const g = parseInt(hexcolor.substring(3, 5), 16);
  const b = parseInt(hexcolor.substring(5, 7), 16);
  const yiq = ((r * 299) + (g * 587) + (b * 114)) / 1000;
  return (yiq >= 128) ? '#000000' : '#ffffff';
};

// 调整颜色透明度
const adjustOpacity = (color: string, opacity: number) => {
  if (color.startsWith('#')) {
    const r = parseInt(color.slice(1, 3), 16);
    const g = parseInt(color.slice(3, 5), 16);
    const b = parseInt(color.slice(5, 7), 16);
    return `rgba(${r}, ${g}, ${b}, ${opacity})`;
  }
  return color;
};

// 调整颜色亮度的辅助函数
const adjustColor = (color: string, percent: number) => {
  const num = parseInt(color.replace('#', ''), 16);
  const amt = Math.round(2.55 * percent);
  const R = (num >> 16) + amt;
  const G = (num >> 8 & 0x00FF) + amt;
  const B = (num & 0x0000FF) + amt;
  return '#' + (
    0x1000000 +
    (R < 255 ? (R < 1 ? 0 : R) : 255) * 0x10000 +
    (G < 255 ? (G < 1 ? 0 : G) : 255) * 0x100 +
    (B < 255 ? (B < 1 ? 0 : B) : 255)
  ).toString(16).slice(1);
};

// 在组件挂载时恢复保存的主题设置
onMounted(() => {
  const savedTheme = localStorage.getItem('theme');
  const savedColor = localStorage.getItem('themeColor');

  if (savedTheme) {
    currentTheme.value = savedTheme;
    if (savedTheme === 'custom' && savedColor) {
      customColor.value = savedColor;
      applyCustomTheme(savedColor);
    }
  }

  const accountAddress = localStorage.getItem("accountAddress");
  if (!accountAddress || username === "未登录") {
    router.push("/login");
  }
});

const toggleDropdown = () => {
  isDropdownOpen.value = !isDropdownOpen.value;
};

// 退出登录
const logout = () => {
  localStorage.removeItem("accountAddress");
  localStorage.removeItem("name");
  router.push("/login");
};


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

/* 默认主题 */
.default {
  --primary-color: #4a9e5c;
  --secondary-color: #3d8a4e;
  --background-gradient-start: #f5f7fa;
  --background-gradient-end: #e4ecf7;
}

/* 深色主题 */
.dark {
  --primary-color: #010a13;
  --secondary-color: #34495e;
  --background-gradient-start: #2c3e50;
  --background-gradient-end: #3498db;
}

/* 文字样式优化 */
.logo h1 {
  font-size: 30px;
  font-weight: 700;
  color: var(--text-color);
  margin: 0;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
}

.menu-item a,
.menu-item span {
  font-size: 13px;
  color: #ffffff;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.user-dropdown span {
  color: var(--text-color);
  font-weight: 500;
}

.theme-label {
  font-size: 14px;
  color: var(--text-color);
  font-weight: 500;
}

.feature-title {
  font-size: 18px;
  color: var(--text-color);
  margin-bottom: 10px;
  font-weight: 600;
}

.feature-description {
  color: var(--text-color-secondary);
  font-size: 14px;
  line-height: 1.6;
}

.action-title {
  font-size: 20px;
  color: var(--text-color);
  margin-bottom: 20px;
  font-weight: 600;
}

/* 深色主题文字优化 */
.dark {
  --text-color: #ffffff;
  --text-color-secondary: rgba(255, 255, 255, 0.85);
}

.dark .logo h1 {
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
}

.dark .menu-item a {
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.4);
}

/* 主题切换按钮文字优化 */
.theme-button {
  color: #ffffff;
  font-weight: 500;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

/* 卡片内容文字优化 */
.feature-card {
  background: rgba(255, 255, 255, 0.95);
}

.dark .feature-card {
  background: rgba(0, 0, 0, 0.2);
}

/* 确保下拉菜单文字清晰可见 */
.el-dropdown-menu {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(10px);
}

.dark .el-dropdown-menu {
  background: rgba(48, 48, 48, 0.98);
}

/* 添加文字过渡效果 */
* {
  transition: background-color 0.3s, color 0.3s, border-color 0.3s, text-shadow 0.3s;
}
</style>

<style scoped>
#base {
  min-height: 100vh;
  background: linear-gradient(135deg, var(--background-gradient-start) 0%, var(--background-gradient-end) 100%);
  display: flex;
}

/* 顶部导航栏样式 */
.rotate {
  transform: rotate(180deg);
  transition: transform 0.3s;
}

.header {
  position: fixed;
  top: 0;
  left: 80px;
  right: 0;
  height: 70px;
  background: linear-gradient(90deg, #ffffff, var(--background-gradient-start));
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

.user-dropdown {
  position: relative;
  cursor: pointer;
  padding: 5px 10px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 5px 0;
  min-width: 100px;
}

.dropdown-item {
  padding: 8px 16px;
  color: #333;
  transition: background 0.3s;
}

.dropdown-item:hover {
  background: #f5f7fa;
}



/* 侧边导航栏样式 */
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
  background-color: rgba(255, 255, 255, 0.2);
}

.menu-item img {
  width: 36px;
  height: 36px;
  transition: transform 0.3s ease;
}

.menu-item:hover img {
  transform: scale(1.1);
}

.menu-item a,
.menu-item span {
  font-size: 13px;
  color: #ffffff;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.menu-item:hover a {
  color: #e6f3e8;
}

/* 主要内容区域样式 */
.main-content {
  margin-left: 80px;
  margin-top: 70px;
  padding: 30px;
  flex: 1;
  background-color: #f5f7fa;
  min-height: calc(100vh - 70px);
}

.patient-home {
    min-height: 100vh;
    background: linear-gradient(135deg, #f5f7fa 0%, #e4ecf7 100%);
    padding: 20px;
    box-sizing: border-box;
}

.welcome-section {
    background: rgba(255, 255, 255, 0.9);
    border-radius: 15px;
    padding: 25px;
    margin-bottom: 30px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.8);
    position: relative;
    overflow: hidden;
}

.welcome-section::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, #4a9e5c, #2196F3);
    border-radius: 3px;
}

.welcome-title {
    font-size: 24px;
    color: #2c3e50;
    margin-bottom: 15px;
    font-weight: 600;
    display: flex;
    align-items: center;
    gap: 10px;
}

.welcome-title::after {
    content: '';
    display: block;
    width: 30px;
    height: 2px;
    background: linear-gradient(90deg, #4a9e5c, #2196F3);
    margin-left: 10px;
}

.feature-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 20px;
    margin-top: 20px;
}

.feature-card {
    background: rgba(255, 255, 255, 0.95);
    border-radius: 12px;
    padding: 25px;
    transition: all 0.3s ease;
    cursor: pointer;
    position: relative;
    border: 1px solid rgba(255, 255, 255, 0.8);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
    overflow: hidden;
}

.feature-card::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 4px;
    height: 100%;
  background: linear-gradient(180deg, var(--primary-color), var(--secondary-color));
    border-radius: 4px;
    opacity: 0;
    transition: opacity 0.3s ease;
}

.feature-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
}

.feature-card:hover::before {
    opacity: 1;
}

.feature-icon {
    font-size: 24px;
    color: #4a9e5c;
    margin-bottom: 15px;
    background: linear-gradient(135deg, #4a9e5c, #2196F3);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
}

.feature-title {
    font-size: 18px;
  color: var(--text-color);
    margin-bottom: 10px;
    font-weight: 600;
}

.feature-description {
  color: var(--text-color-secondary);
    font-size: 14px;
    line-height: 1.6;
}

.quick-actions {
    margin-top: 30px;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 15px;
    padding: 25px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.8);
}

.action-title {
    font-size: 20px;
  color: var(--text-color);
    margin-bottom: 20px;
    font-weight: 600;
    position: relative;
    padding-left: 15px;
}

.action-title::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 4px;
    height: 20px;
    background: linear-gradient(180deg, #4a9e5c, #2196F3);
    border-radius: 2px;
}

.action-buttons {
    display: flex;
    gap: 15px;
    flex-wrap: wrap;
}

.action-btn {
    padding: 12px 25px;
    border-radius: 8px;
    font-size: 15px;
    font-weight: 500;
    transition: all 0.3s ease;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
    border: none;
    color: white;
    position: relative;
    overflow: hidden;
}

.action-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 5px 15px rgba(74, 158, 92, 0.3);
}

.action-btn::after {
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

/* 添加主题切换按钮样式 */
.theme-switcher {
  margin-right: 20px;
}

/* 添加过渡效果 */
* {
  transition: background-color 0.3s, color 0.3s;
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

.theme-item:hover {
  background: rgba(var(--primary-color), 0.1);
  transform: translateY(-2px);
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

.custom-theme {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

/* 优化深色主题 */
.dark {
  --text-color: #ffffff;
  --text-color-secondary: rgba(255, 255, 255, 0.9);
  --header-text-color: #ffffff;
}

.dark .header {
  background: linear-gradient(90deg, var(--primary-color), var(--secondary-color));
  backdrop-filter: blur(10px);
}

.dark .logo h1 {
  color: var(--header-text-color);
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
}

.dark .main-content {
  background-color: var(--background-gradient-start);
}

/* 毛玻璃效果 */
.header,
.sidebar {
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

/* 优化动画效果 */
.menu-item,
.feature-card,
.action-btn {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 优化阴影效果 */
.header {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.sidebar {
  box-shadow: 2px 0 20px rgba(0, 0, 0, 0.1);
}

.feature-card {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.06);
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

/* 添加过渡效果 */
* {
  transition: background-color 0.3s, color 0.3s, border-color 0.3s;
}


/* 表格样式优化 */
.el-table {
  color: var(--table-text-color) !important;
  background-color: var(--card-background) !important;
}

.el-table th {
  color: var(--table-header-color) !important;
  background-color: var(--card-background) !important;
}

.el-table tr:hover>td {
  background-color: var(--table-row-hover) !important;
}

.el-table--enable-row-hover .el-table__body tr:hover>td {
  background-color: var(--table-row-hover) !important;
}

/* 按钮文字颜色 */
.el-button {
  color: var(--text-color) !important;
}

.el-button--primary {
  color: #ffffff !important;
}

/* 输入框文字颜色 */
.el-input__inner {
  color: var(--text-color) !important;
}

/* 标题和文字样式 */
.logo h1 {
  color: var(--header-text-color) !important;
}

.menu-item a,
.menu-item span {
  color: #ffffff !important;
}

.user-dropdown span {
  color: var(--header-text-color) !important;
}

/* 深色主题特殊处理 */
.dark {
  --text-color: #ffffff;
  --text-color-secondary: rgba(255, 255, 255, 0.9);
  --header-text-color: #ffffff;
}

.dark .el-table {
  background-color: rgba(26, 35, 126, 0.3) !important;
}

.dark .el-table th {
  background-color: rgba(26, 35, 126, 0.5) !important;
}

.dark .el-table td,
.dark .el-table th.is-leaf {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1) !important;
}

.dark .el-table--border::after,
.dark .el-table--group::after {
  background-color: rgba(255, 255, 255, 0.1) !important;
}

/* 搜索框样式 */
.dark .el-input__inner {
  background-color: rgba(255, 255, 255, 0.1) !important;
  border-color: rgba(255, 255, 255, 0.2) !important;
  color: #ffffff !important;
}

.dark .el-input__inner::placeholder {
  color: rgba(255, 255, 255, 0.5) !important;
}

/* 分页器样式 */
.dark .el-pagination {
  color: #ffffff !important;
}

.dark .el-pagination button {
  background-color: transparent !important;
  color: #ffffff !important;
}

.dark .el-pagination .el-select .el-input .el-input__inner {
  color: #ffffff !important;
}

/* 操作按钮样式 */
.dark .el-button--default {
  background-color: rgba(255, 255, 255, 0.1) !important;
  border-color: rgba(255, 255, 255, 0.2) !important;
  color: #ffffff !important;
}

.dark .el-button--primary {
  background-color: var(--primary-color) !important;
  border-color: var(--primary-color) !important;
  color: #ffffff !important;
}

/* 确保下拉菜单文字可见 */
.dark .el-dropdown-menu {
  background-color: rgba(26, 35, 126, 0.95) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
}

.dark .el-dropdown-menu__item {
  color: #ffffff !important;
}

.dark .el-dropdown-menu__item:hover {
  background-color: rgba(255, 255, 255, 0.1) !important;
}

/* 确保所有文字过渡效果平滑 */
* {
  transition: color 0.3s ease, background-color 0.3s ease, border-color 0.3s ease !important;
}
</style>