import { createRouter, createWebHistory } from 'vue-router'
import constantRoutes from './constantRoutes'

// 创建 Vue Router 实例
const router = createRouter({
  history: createWebHistory("/"), // 使用固定的base路径
  routes: constantRoutes,
})

export default router

