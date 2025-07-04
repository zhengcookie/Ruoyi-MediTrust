import { themes, defaultTheme } from '../constants/themes';

// 应用主题
export const applyTheme = (themeName: string) => {
  const theme = themes.find(t => t.name === themeName) || defaultTheme;
  
  document.documentElement.style.setProperty('--primary-color', theme.primary);
  document.documentElement.style.setProperty('--secondary-color', theme.secondary);
  document.documentElement.style.setProperty('--background-gradient-start', theme.gradientStart);
  document.documentElement.style.setProperty('--background-gradient-end', theme.gradientEnd);
  document.documentElement.style.setProperty('--text-color', theme.textColor);
  document.documentElement.style.setProperty('--text-color-secondary', theme.textColorSecondary);
  document.documentElement.style.setProperty('--header-text-color', theme.headerTextColor || theme.textColor);
  document.documentElement.style.setProperty('--card-background', 'rgba(255, 255, 255, 0.95)');
  document.documentElement.style.setProperty('--header-background', 'linear-gradient(90deg, #ffffff, var(--background-gradient-start))');
  document.documentElement.style.setProperty('--menu-hover', 'rgba(0, 0, 0, 0.1)');
  document.documentElement.style.setProperty('--table-text-color', '#2c3e50');
  document.documentElement.style.setProperty('--table-header-color', '#606266');
  document.documentElement.style.setProperty('--table-row-hover', '#f5f7fa');
};

// 调整颜色亮度的辅助函数
export const adjustColor = (color: string, percent: number) => {
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

// 根据背景色计算对比度最佳的文字颜色
export const getContrastYIQ = (hexcolor: string) => {
  const r = parseInt(hexcolor.substring(1, 3), 16);
  const g = parseInt(hexcolor.substring(3, 5), 16);
  const b = parseInt(hexcolor.substring(5, 7), 16);
  const yiq = ((r * 299) + (g * 587) + (b * 114)) / 1000;
  return (yiq >= 128) ? '#000000' : '#ffffff';
};

// 调整颜色透明度
export const adjustOpacity = (color: string, opacity: number) => {
  if (color.startsWith('#')) {
    const r = parseInt(color.slice(1, 3), 16);
    const g = parseInt(color.slice(3, 5), 16);
    const b = parseInt(color.slice(5, 7), 16);
    return `rgba(${r}, ${g}, ${b}, ${opacity})`;
  }
  return color;
}; 