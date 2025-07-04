<template>
  <div class="dashboard-container">
    <!-- 健康数据总览 -->
    <div class="header-stats">
      <div class="stat-card big-number">
        <div class="stat-title">今日就诊人数</div>
        <div class="stat-value">23,456</div>
      </div>
    </div>

    <div class="dashboard-content">
      <!-- 左侧面板 -->
      <div class="left-panel">
        <!-- 诊疗分类比例图 -->
        <div class="dashboard-card">
          <div class="card-header">
            <span>诊疗服务分类</span>
          </div>
          <div class="chart-container">
            <div class="donut-chart">
              <!-- 此处放置环形图 -->
              <div class="chart-placeholder"></div>
              <div class="chart-legend">
                <div class="legend-item">
                  <div class="color-dot" style="background-color: var(--primary-color);"></div>
                  <div class="legend-text">内科 35.49%</div>
                </div>
                <div class="legend-item">
                  <div class="color-dot" style="background-color: var(--secondary-color);"></div>
                  <div class="legend-text">外科 34.08%</div>
                </div>
                <div class="legend-item">
                  <div class="color-dot" style="background-color: rgba(92, 158, 255, 0.8);"></div>
                  <div class="legend-text">急诊 30.87%</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 全国医疗分布图 -->
        <div class="dashboard-card">
          <div class="card-header">
            <span>全国就诊地区分布</span>
            <div class="filter-icon">
              <i class="el-icon-filter"></i> 筛选
            </div>
          </div>
          <div class="map-container">
            <!-- 此处放置中国地图 -->
            <div class="map-placeholder"></div>
          </div>
        </div>
      </div>

      <!-- 中间面板 -->
      <div class="center-panel">
        <!-- 时间和天气 -->
        <div class="time-weather">
          <div class="time">{{ currentTime }}</div>
          <div class="date">{{ currentDate }}</div>
          <div class="weather">
            <i class="el-icon-sunny"></i>
            <span>晴天 28.6°C</span>
          </div>
          <div class="filters">
            <div class="filter-item">
              <span>医院</span>
              <select>
                <option>全部医院</option>
              </select>
            </div>
            <div class="filter-item">
              <span>科室</span>
              <select>
                <option>全部科室</option>
              </select>
            </div>
          </div>
        </div>

        <!-- 全国医院联网图 -->
        <div class="network-map">
          <!-- 此处放置医院联网图 -->
          <div class="map-placeholder"></div>
        </div>

        <!-- 医院排行榜 -->
        <div class="hospital-rankings">
          <div class="ranking-header">
            <span>城市</span>
            <span>患者数</span>
          </div>
          <div class="ranking-list">
            <div class="ranking-item">
              <span class="rank">1</span>
              <span class="city">上海市</span>
              <span class="value">175846</span>
            </div>
            <div class="ranking-item">
              <span class="rank">2</span>
              <span class="city">湖南省</span>
              <span class="value">129687</span>
            </div>
            <div class="ranking-item">
              <span class="rank">3</span>
              <span class="city">福建省</span>
              <span class="value">78452</span>
            </div>
            <div class="ranking-item">
              <span class="rank">4</span>
              <span class="city">北京市</span>
              <span class="value">57841</span>
            </div>
            <div class="ranking-item">
              <span class="rank">5</span>
              <span class="city">江苏省</span>
              <span class="value">45279</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧面板 -->
      <div class="right-panel">
        <!-- 数据指标 -->
        <div class="dashboard-card">
          <div class="card-header">
            <span>健康数据指标</span>
          </div>
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-name">入院人数</div>
              <div class="stat-chart"></div>
            </div>
            <div class="stat-item">
              <div class="stat-name">在院人数</div>
              <div class="stat-chart"></div>
            </div>
            <div class="stat-item">
              <div class="stat-name">出院人数</div>
              <div class="stat-chart"></div>
            </div>
            <div class="stat-item">
              <div class="stat-name">手术人数</div>
              <div class="stat-chart"></div>
            </div>
          </div>
        </div>

        <!-- 收支统计 -->
        <div class="dashboard-card">
          <div class="card-header">
            <span>收支统计</span>
          </div>
          <div class="revenue-display">
            <div class="revenue-amount">123,456.5元</div>
          </div>
        </div>

        <!-- 医院通知 -->
        <div class="dashboard-card">
          <div class="card-header">
            <span>医院通知</span>
          </div>
          <div class="notice-board">
            <div class="notice-item">
              <i class="el-icon-bell"></i>
              <span>今日值班：李医生</span>
            </div>
            <div class="notice-item">
              <i class="el-icon-user"></i>
              <span>值班人：王护士长</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onBeforeUnmount } from 'vue';

export default {
  name: 'HealthDashboard',
  data() {
    return {
      // 此处添加数据
    }
  },
  setup() {
    // 时间相关
    const currentTime = ref('');
    const currentDate = ref('');
    let clockTimer = null;

    // 更新时间
    const updateClock = () => {
      const now = new Date();
      
      // 格式化时间 HH:MM:SS
      const hours = String(now.getHours()).padStart(2, '0');
      const minutes = String(now.getMinutes()).padStart(2, '0');
      const seconds = String(now.getSeconds()).padStart(2, '0');
      currentTime.value = `${hours}:${minutes}:${seconds}`;
      
      // 格式化日期 YYYY/MM/DD
      const year = now.getFullYear();
      const month = String(now.getMonth() + 1).padStart(2, '0');
      const day = String(now.getDate()).padStart(2, '0');
      currentDate.value = `${year}/${month}/${day}`;
    };

    // 用于存储RGB值的变量
    const primaryColorRGB = ref('74, 158, 92'); // 默认绿色
    const secondaryColorRGB = ref('61, 138, 78'); // 默认绿色
    let themeCheckInterval = null; // 用于存储间隔ID
    
    // 将十六进制颜色转换为RGB
    const hexToRgb = (hex) => {
      // 移除#号
      hex = hex.replace('#', '');
      
      // 解析RGB值
      const r = parseInt(hex.substring(0, 2), 16);
      const g = parseInt(hex.substring(2, 4), 16);
      const b = parseInt(hex.substring(4, 6), 16);
      
      return isNaN(r) || isNaN(g) || isNaN(b) ? '0, 0, 0' : `${r}, ${g}, ${b}`;
    };
    
    // 更新主题
    const updateThemeColors = () => {
      // 从CSS变量中获取当前主题颜色
      const primaryColor = getComputedStyle(document.documentElement).getPropertyValue('--primary-color').trim();
      const secondaryColor = getComputedStyle(document.documentElement).getPropertyValue('--secondary-color').trim();
      
      // 转换为RGB并存储
      if (primaryColor) {
        primaryColorRGB.value = hexToRgb(primaryColor);
        document.documentElement.style.setProperty('--primary-color-rgb', primaryColorRGB.value);
      }
      
      if (secondaryColor) {
        secondaryColorRGB.value = hexToRgb(secondaryColor);
        document.documentElement.style.setProperty('--secondary-color-rgb', secondaryColorRGB.value);
      }
    };
    
    // 监听主题变化
    const handleThemeChange = () => {
      updateThemeColors();
    };
    
    onMounted(() => {
      // 初始化时间
      updateClock();
      // 每秒更新一次时间
      clockTimer = setInterval(updateClock, 1000);
      
      // 初始化主题颜色
      updateThemeColors();
      
      // 监听本地存储变化（用于检测主题变化）
      window.addEventListener('storage', handleThemeChange);
      
      // 直接监听 --primary-color 变量的变化可能不可行，因此我们使用轮询
      themeCheckInterval = setInterval(updateThemeColors, 2000);
    });
    
    onBeforeUnmount(() => {
      // 组件卸载前清除事件监听和定时器
      window.removeEventListener('storage', handleThemeChange);
      
      if (themeCheckInterval) {
        clearInterval(themeCheckInterval);
      }
      
      if (clockTimer) {
        clearInterval(clockTimer);
      }
    });
    
    return {
      primaryColorRGB,
      secondaryColorRGB,
      currentTime,
      currentDate
    };
  }
}
</script>

<style scoped>
.dashboard-container {
  width: 100%;
  height: 100vh;
  background-color: var(--background-gradient-start);
  background-image: 
    radial-gradient(at 20% 30%, rgba(var(--primary-color-rgb, 74, 158, 92), 0.2) 0%, transparent 70%),
    radial-gradient(at 80% 70%, rgba(var(--secondary-color-rgb, 61, 138, 78), 0.2) 0%, transparent 70%);
  color: var(--text-color);
  display: flex;
  flex-direction: column;
  padding: 15px;
  box-sizing: border-box;
  overflow: hidden;
}

.header-stats {
  display: flex;
  margin-bottom: 15px;
  height: 100px;
}

.dashboard-content {
  display: flex;
  flex: 1;
  gap: 15px;
  height: calc(100vh - 130px);
}

.left-panel, .right-panel {
  width: 25%;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.center-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.dashboard-card {
  background: rgba(var(--primary-color-rgb, 74, 158, 92), 0.05);
  border: 1px solid rgba(var(--primary-color-rgb, 74, 158, 92), 0.2);
  border-radius: 6px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 12px;
  display: flex;
  flex-direction: column;
  position: relative;
  backdrop-filter: blur(5px);
  flex: 1;
  min-height: 0;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 12px;
  border-bottom: 1px solid rgba(var(--primary-color-rgb, 74, 158, 92), 0.2);
  padding-bottom: 8px;
  display: flex;
  justify-content: space-between;
}

.stat-card {
  background: rgba(var(--primary-color-rgb, 74, 158, 92), 0.05);
  border: 1px solid rgba(var(--primary-color-rgb, 74, 158, 92), 0.2);
  border-radius: 6px;
  padding: 15px;
  text-align: center;
  flex: 1;
}

.big-number {
  position: relative;
  overflow: hidden;
  min-height: 120px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.big-number::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--primary-color), var(--secondary-color));
}

.stat-title {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 40px;
  font-weight: bold;
  background: linear-gradient(90deg, var(--primary-color), var(--secondary-color));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.chart-container, .map-container {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  min-height: 0;
}

.donut-chart {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.chart-placeholder {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  border: 15px solid var(--primary-color);
  border-right-color: var(--secondary-color);
  border-bottom-color: rgba(92, 158, 255, 0.8);
  margin-bottom: 15px;
}

.chart-legend {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 10px;
  margin-top: 10px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 5px 10px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
}

.color-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.map-placeholder {
  width: 100%;
  height: 100%;
  background-color: rgba(var(--primary-color-rgb, 74, 158, 92), 0.1);
  border-radius: 6px;
  background-position: center;
  background-repeat: no-repeat;
  background-size: 80% 80%;
}

.time-weather {
  background: rgba(var(--primary-color-rgb, 74, 158, 92), 0.05);
  border: 1px solid rgba(var(--primary-color-rgb, 74, 158, 92), 0.2);
  border-radius: 6px;
  padding: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.time {
  font-size: 32px;
  font-weight: bold;
}

.date {
  font-size: 16px;
  opacity: 0.9;
}

.weather {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 5px 10px;
  background: rgba(var(--primary-color-rgb, 74, 158, 92), 0.2);
  border-radius: 4px;
}

.filters {
  display: flex;
  gap: 10px;
  margin-left: auto;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.filter-item select {
  background: rgba(var(--primary-color-rgb, 74, 158, 92), 0.05);
  border: 1px solid rgba(var(--primary-color-rgb, 74, 158, 92), 0.3);
  border-radius: 4px;
  color: var(--text-color);
  padding: 5px 10px;
}

.network-map {
  flex: 2;
  background: rgba(var(--primary-color-rgb, 74, 158, 92), 0.05);
  border: 1px solid rgba(var(--primary-color-rgb, 74, 158, 92), 0.2);
  border-radius: 6px;
  padding: 15px;
}

.hospital-rankings {
  flex: 1;
  background: rgba(var(--primary-color-rgb, 74, 158, 92), 0.05);
  border: 1px solid rgba(var(--primary-color-rgb, 74, 158, 92), 0.2);
  border-radius: 6px;
  padding: 15px;
  display: flex;
  flex-direction: column;
}

.ranking-header {
  display: flex;
  justify-content: space-between;
  padding: 8px 15px;
  border-bottom: 1px solid rgba(var(--primary-color-rgb, 74, 158, 92), 0.2);
  font-weight: bold;
}

.ranking-list {
  flex: 1;
  overflow-y: auto;
}

.ranking-item {
  display: flex;
  align-items: center;
  padding: 8px 15px;
}

.rank {
  width: 20px;
  height: 20px;
  background: rgba(var(--primary-color-rgb, 74, 158, 92), 0.2);
  border-radius: 4px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 10px;
}

.city {
  flex: 1;
}

.value {
  font-weight: bold;
  color: var(--primary-color);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  height: 100%;
}

.stat-item {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  padding: 10px;
  display: flex;
  flex-direction: column;
}

.stat-name {
  margin-bottom: 8px;
  font-size: 14px;
}

.stat-chart {
  flex: 1;
  min-height: 0;
}

.revenue-display {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.revenue-amount {
  font-size: 32px;
  font-weight: bold;
  background: linear-gradient(90deg, var(--primary-color), var(--secondary-color));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.notice-board {
  display: flex;
  flex-direction: column;
  gap: 10px;
  height: 100%;
}

.notice-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
}

.filter-icon {
  display: flex;
  align-items: center;
  font-size: 14px;
  gap: 5px;
  cursor: pointer;
  opacity: 0.7;
}
</style> 