declare module 'vue-router' {
  import { DefineComponent } from 'vue'
  
  export interface RouteRecordRaw {
    path: string
    name?: string
    component?: any
    redirect?: string
    children?: RouteRecordRaw[]
    meta?: Record<string, any>
  }
  
  export function createRouter(options: {
    history: any
    routes: RouteRecordRaw[]
  }): any
  
  export function createWebHistory(base?: string): any
} 