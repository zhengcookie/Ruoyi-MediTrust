<template>
    <div class="animated-background" :class="currentTheme">
        <div class="background-decoration before"></div>
        <div class="background-decoration after"></div>
        <div class="theme-switcher">
            <el-popover
                placement="bottom"
                :width="300"
                trigger="click"
            >
                <template #reference>
                    <el-button>
                        <el-icon><Brush /></el-icon>
                        主题颜色
                    </el-button>
                </template>
                <div class="color-picker">
                    <div
                        v-for="theme in themes"
                        :key="theme.name"
                        class="color-option"
                        :class="{ active: currentTheme === theme.name }"
                        @click="switchTheme(theme.name)"
                    >
                        <div class="color-preview" :style="{
                            background: `linear-gradient(135deg, ${theme.gradientStart} 0%, ${theme.gradientEnd} 100%)`
                        }"></div>
                        <span>{{ theme.label }}</span>
                    </div>
                </div>
            </el-popover>
        </div>
        <slot :theme="currentTheme" :themeColors="currentThemeColors"></slot>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { Brush } from '@element-plus/icons-vue';

interface Theme {
    name: string;
    label: string;
    gradientStart: string;
    gradientEnd: string;
    accent1: string;
    accent2: string;
    buttonGradientStart: string;
    buttonGradientEnd: string;
}

const themes: Theme[] = [
    {
        name: 'blue',
        label: '蓝色主题',
        gradientStart: '#E3F2FD',
        gradientEnd: '#BBDEFB',
        accent1: 'rgba(30, 136, 229, 0.1)',
        accent2: 'rgba(66, 165, 245, 0.1)',
        buttonGradientStart: '#1E88E5',
        buttonGradientEnd: '#42A5F5'
    },
    {
        name: 'green',
        label: '绿色主题',
        gradientStart: '#E8F5E9',
        gradientEnd: '#C8E6C9',
        accent1: 'rgba(46, 125, 50, 0.1)',
        accent2: 'rgba(56, 142, 60, 0.1)',
        buttonGradientStart: '#2e7d32',
        buttonGradientEnd: '#388e3c'
    },
    {
        name: 'orange',
        label: '橙色主题',
        gradientStart: '#FFF3E0',
        gradientEnd: '#FFE0B2',
        accent1: 'rgba(244, 81, 30, 0.1)',
        accent2: 'rgba(255, 112, 67, 0.1)',
        buttonGradientStart: '#f4511e',
        buttonGradientEnd: '#ff7043'
    },
    {
        name: 'purple',
        label: '紫色主题',
        gradientStart: '#F3E5F5',
        gradientEnd: '#E1BEE7',
        accent1: 'rgba(106, 27, 154, 0.1)',
        accent2: 'rgba(142, 36, 170, 0.1)',
        buttonGradientStart: '#6a1b9a',
        buttonGradientEnd: '#8e24aa'
    },
    {
        name: 'red',
        label: '红色主题',
        gradientStart: '#FFEBEE',
        gradientEnd: '#FFCDD2',
        accent1: 'rgba(198, 40, 40, 0.1)',
        accent2: 'rgba(211, 47, 47, 0.1)',
        buttonGradientStart: '#c62828',
        buttonGradientEnd: '#d32f2f'
    }
];

const currentTheme = ref('blue');

const currentThemeColors = computed(() => {
    const theme = themes.find(t => t.name === currentTheme.value);
    return theme || themes[0];
});

const switchTheme = (themeName: string) => {
    currentTheme.value = themeName;
    const theme = themes.find(t => t.name === themeName);
    if (theme) {
        document.documentElement.style.setProperty('--theme-gradient-start', theme.gradientStart);
        document.documentElement.style.setProperty('--theme-gradient-end', theme.gradientEnd);
        document.documentElement.style.setProperty('--theme-accent-1', theme.accent1);
        document.documentElement.style.setProperty('--theme-accent-2', theme.accent2);
        document.documentElement.style.setProperty('--theme-button-start', theme.buttonGradientStart);
        document.documentElement.style.setProperty('--theme-button-end', theme.buttonGradientEnd);
    }
};

onMounted(() => {
    switchTheme(currentTheme.value);
});
</script>

<style scoped>
.animated-background {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, var(--theme-gradient-start) 0%, var(--theme-gradient-end) 100%);
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;
}

.background-decoration {
    position: absolute;
    border-radius: 50%;
    pointer-events: none;
    z-index: 0;
}

.background-decoration.before {
    width: 1000px;
    height: 1000px;
    background: linear-gradient(135deg, var(--theme-accent-1) 0%, var(--theme-accent-2) 100%);
    top: -400px;
    left: -400px;
    animation: float 40s ease-in-out infinite;
}

.background-decoration.after {
    width: 800px;
    height: 800px;
    background: linear-gradient(135deg, var(--theme-accent-2) 0%, var(--theme-accent-1) 100%);
    bottom: -300px;
    right: -300px;
    animation: float 40s ease-in-out infinite reverse;
}

.theme-switcher {
    position: fixed;
    top: 20px;
    right: 20px;
    z-index: 1000;
}

.color-picker {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
    padding: 8px;
}

.color-option {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.3s ease;
}

.color-option:hover {
    background-color: var(--el-fill-color-light);
}

.color-option.active {
    background-color: var(--el-fill-color);
}

.color-preview {
    width: 24px;
    height: 24px;
    border-radius: 4px;
    border: 1px solid var(--el-border-color);
}

@keyframes float {
    0%, 100% {
        transform: translate(0, 0);
    }
    50% {
        transform: translate(30px, -30px);
    }
}

/* 主题相关样式 */
.blue {
    --theme-gradient-start: #E3F2FD;
    --theme-gradient-end: #BBDEFB;
    --theme-accent-1: rgba(30, 136, 229, 0.1);
    --theme-accent-2: rgba(66, 165, 245, 0.1);
    --theme-button-start: #1E88E5;
    --theme-button-end: #42A5F5;
}

.green {
    --theme-gradient-start: #E8F5E9;
    --theme-gradient-end: #C8E6C9;
    --theme-accent-1: rgba(46, 125, 50, 0.1);
    --theme-accent-2: rgba(56, 142, 60, 0.1);
    --theme-button-start: #2e7d32;
    --theme-button-end: #388e3c;
}

.orange {
    --theme-gradient-start: #FFF3E0;
    --theme-gradient-end: #FFE0B2;
    --theme-accent-1: rgba(244, 81, 30, 0.1);
    --theme-accent-2: rgba(255, 112, 67, 0.1);
    --theme-button-start: #f4511e;
    --theme-button-end: #ff7043;
}

.purple {
    --theme-gradient-start: #F3E5F5;
    --theme-gradient-end: #E1BEE7;
    --theme-accent-1: rgba(106, 27, 154, 0.1);
    --theme-accent-2: rgba(142, 36, 170, 0.1);
    --theme-button-start: #6a1b9a;
    --theme-button-end: #8e24aa;
}

.red {
    --theme-gradient-start: #FFEBEE;
    --theme-gradient-end: #FFCDD2;
    --theme-accent-1: rgba(198, 40, 40, 0.1);
    --theme-accent-2: rgba(211, 47, 47, 0.1);
    --theme-button-start: #c62828;
    --theme-button-end: #d32f2f;
}
</style> 