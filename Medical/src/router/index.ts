import { createRouter, createWebHistory,RouteRecordRaw } from 'vue-router'

import PatientHome from "../view/Patient/PatientHome.vue"
import Register from '../view/Register.vue'
import DoctorHome from '../view/Doctor/DoctorHome.vue'
import Login from '../view/Login.vue'

// 路由配置
const routes: Array<RouteRecordRaw> = [
    {
        path: '/login',
        name: 'login', 
        component: Login
    },
    {
        path: '/register',
        name: 'register',
        component: Register
    },
    {
        path: '/patient',
        name: 'patientHome',
        component: PatientHome,
        redirect: '/patient/personalInformation',
        children: [
            {
                path: 'personalInformation',
                name: 'patientPersonalInfo',
                component: () => import("../view/Patient/PersonalInformation.vue")
            },
            // {
            //     path: 'registeredmanagement', 
            //     name: 'patientRegistration',
            //     component: () => import("../view/Patient/Registeredmanagement.vue")
            // },
            {
                path: 'reserveinfo',
                name: 'patientReservation', 
                component: () => import("../view/Patient/Reserveinformation.vue")
            },
            {
                path: 'myMedical',
                name: 'myMedical',
                component: () => import("../view/Patient/MyMedical.vue")
            },
            // {
            //     path: 'permission',
            //     name: 'patientPermission',
            //     component: () => import("../view/Patient/Permission.vue")
            // }
        ]
    },
    {
        path: '/doctor',
        name: 'doctorHome',
        component: DoctorHome,
        redirect: '/doctor/index',
        children: [
            {
                path: 'index',
                name: 'doctorIndex',
                component: () => import("../view/Doctor/index.vue")
            },
            {
                path: 'info',
                name: 'doctorInfo',
                component: () => import("../view/Doctor/DoctorInfo.vue")
            },
            // {
            //     path: 'patientManagement',
            //     name: 'patientManagement', 
            //     component: () => import("../view/Doctor/PatientManagement.vue")
            // },
            {
                path: 'appointmentManagement',
                name: 'appointmentManagement',
                component: () => import("../view/Doctor/appointmentManagement.vue")
            },
            {
                path: 'records',
                name: 'medicalRecords',
                component: () => import("../view/Doctor/MedicalRecords.vue") 
            }
        ]
    },
    {
        path: '/',
        redirect: '/login'
    }
]
  
  // 创建 Vue Router 实例
  const router = createRouter({
    
    history: createWebHistory(import.meta.env.BASE_URL), // 使用 HTML5 History 模式
    routes,
  })
  export default router

