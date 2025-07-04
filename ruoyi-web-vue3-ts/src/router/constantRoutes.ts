import type { RouteRecordRaw } from 'vue-router'

// 使用异步导入替代直接导入，避免循环依赖问题
// import PatientHome from "../view/Patient/PatientHome.vue"
// import Register from '../view/Register.vue'
// import DoctorHome from '../view/Doctor/DoctorHome.vue'
// import Login from '../view/Login.vue'
// import ForgotPassword from '../view/ForgotPassword.vue'

// 路由配置
const constantRoutes: Array<RouteRecordRaw> = [
    {
        path: '/login',
        name: 'login', 
        component: () => import('../view/Login.vue')
    },
    {
        path: '/register',
        name: 'register',
        component: () => import('../view/Register.vue')
    },
    {
        path: '/forgot-password',
        name: 'forgotPassword',
        component: () => import('../view/ForgotPassword.vue')
    },
    {
        path: '/patient',
        name: 'patientHome',
        component: () => import("../view/Patient/PatientHome.vue"),
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
        component: () => import("../view/Doctor/DoctorHome.vue"),
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
];

export default constantRoutes;
