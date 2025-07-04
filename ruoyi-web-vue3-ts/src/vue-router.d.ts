// 扩展vue-router类型声明
import 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    // 添加你的路由元字段类型
    title?: string
    icon?: string
    hidden?: boolean
  }
  
  // 添加缺失的类型声明
  interface RouteLocationNormalized {
    meta: RouteMeta
  }
  
  // 兼容旧版本类型
  type RouteRecordName = string | symbol;
  type RouteLocationRaw = string | object;
} 