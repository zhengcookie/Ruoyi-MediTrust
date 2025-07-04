import auth from "@/plugins/auth";
import router from "@/router";
// import constantRoutes from "@/router/constantRoutes";
import dynamicRoutes from "@/router/dynamicRoutes";
import { getRouters } from "@/api/menu";
// import Layout from '@/layout/index.vue'
// import ParentView from '@/components/ParentView/index.vue'
// import InnerLink from '@/layout/components/InnerLink/index.vue'

import { defineStore } from "pinia";
import type { RouteRecordRaw } from "vue-router";
import type { MenuRouter } from "@/api/menu";
import { ref, type Ref } from "vue";

//引入所有views下.vue文件
const modules = import.meta.glob("@/views/**/**.vue");

/**
 * 处理菜单路由访问路径
 * @param menuRouters
 * @param perantPath
 * @returns
 */
function dealMenuPath(menuRouters: MenuRouter[], perantPath = "") {
  const routers: MenuRouter[] = [];
  menuRouters.forEach((aRouter) => {
    aRouter.meta.fullPath = perantPath ? perantPath + "/" + aRouter.path : aRouter.path;
    if (aRouter.children) {
      aRouter.children = dealMenuPath(aRouter.children, aRouter.path);
    }
    routers.push(aRouter);
  });
  return routers;
}

/**
 * 首页菜单
 * @returns
 */
function getHomeMenu() {
  const homeMenu: MenuRouter = {
    path: "/index",
    name: "index",
    meta: { title: "首页", icon: "HomeFilled", fullPath: "/index" },
  };
  return [homeMenu];
}

// 遍历后台传来的路由字符串，转换为组件对象
function filterSidebarRouter(asyncRouterMap: MenuRouter[], perantPath = "") {
  const routers: RouteRecordRaw[] = [];
  asyncRouterMap.forEach((aRouter) => {
    const fullPath = perantPath ? perantPath + "/" + aRouter.path : aRouter.path;
    const router: RouteRecordRaw = {
      name: aRouter.name,
      path: aRouter.path,
      // component: () => Layout,
      children: [],
      redirect: aRouter.redirect,
      meta: {
        title: aRouter.meta.title,
        icon: aRouter.meta.icon,
        noCache: aRouter.meta.noCache,
        link: aRouter.meta.link,
        hidden: aRouter.hidden || false,
        alwaysShow: aRouter.alwaysShow,
        fullPath: fullPath,
      },
    };
    // if (aRouter.children) {
    //   aRouter.children = filterChildren(aRouter.children)
    // }

    // Layout ParentView 组件特殊处理
    if (aRouter.component) {
      if (aRouter.component === "Layout") {
        router.component = () => import("@/layout/index.vue");
      } else if (aRouter.component === "ParentView") {
        router.component = () => import("@/components/ParentView/index.vue");
        // } else if (aRouter.component === "InnerLink") {
        //   router.component = () => import("@/layout/components/InnerLink/index.vue");
      } else if (aRouter.component === "IframeView") {
        router.component = () => import("@/layout/components/IframeView/index.vue");
      } else {
        router.component = modules[`/src/views/${aRouter.component}.vue`];
      }
    }
    if (aRouter.children) {
      router.children = filterSidebarRouter(aRouter.children, fullPath);
    }
    routers.push(router);
  });
  return routers;
}

// function filterChildren(childrenMap: MenuRouter[]) {
//   let children: MenuRouter[] = [];
//   childrenMap.forEach((cRouter) => {
//     if (cRouter.children && cRouter.children.length) {
//       if (cRouter.component === "ParentView") {
//         cRouter.children.forEach((c) => {
//           if (c.children && c.children.length) {
//             children = children.concat(filterChildren(c.children));
//             return;
//           }
//           children.push(c);
//         });
//         return;
//       }
//     }
//     children = children.concat(cRouter);
//   });
//   return children;
// }

/**
 *  动态路由遍历，验证是否具备权限
 * @param routes
 * @returns
 */
function filterDynamicRoutes(routes: RouteRecordRaw[], perantPath = "") {
  const res: RouteRecordRaw[] = [];
  routes.forEach((route) => {
    if ((route.meta?.permissions && auth.hasPermiOr(route.meta.permissions)) || (route.meta?.roles && auth.hasRoleOr(route.meta.roles))) {
      const fullPath = perantPath ? perantPath + "/" + route.path : route.path;
      route.meta.fullPath = fullPath;
      route.children && (route.children = filterDynamicRoutes(route.children, fullPath));
      res.push(route);
    }
  });
  return res;
}

/**
 * 获取tagsView中路由的路径
 * @param routers
 * @param perantPath
 * @returns
 */
// function getTagsRouterPath(routers: RouteRecordRaw[], perantPath = "") {
//   const paths: string[] = [];
//   routers.forEach((router) => {
//     paths.push(perantPath ? perantPath + "/" + router.path : router.path);
//     router.children &&
//       getTagsRouterPath(router.children, router.path).forEach((path) => {
//         paths.push(path);
//       });
//   });
//   return paths;
// }

/**
 * tagsView路由匹配
 * @param tagsRouters
 * @param fullPath
 * @returns boolean
 */
function checkTagsRouter(tagsRouters: RouteRecordRaw[], fullPath: string) {
  for (let i = 0; i < tagsRouters.length; i++) {
    const router = tagsRouters[i];
    if (fullPath == router.meta?.fullPath) {
      return true;
    }

    if (router.meta?.fullPath && fullPath.indexOf(router.meta.fullPath) === 0 && router.children) {
      if (checkTagsRouter(router.children, fullPath)) {
        return true;
      }
    }
  }
  return false;
}

/**
 * 深度合并两个路由，后者覆盖前者
 * @param routers1
 * @param routers2
 */
function concatRouter(routers1: RouteRecordRaw[], routers2: RouteRecordRaw[]) {
  const map: Map<string, RouteRecordRaw> = new Map();
  for (const router of routers2) {
    map.set(router.path, router);
  }

  for (const router of routers1) {
    const temp = map.get(router.path);
    if (!temp) {
      map.set(router.path, router);
      continue;
    }

    if (!temp.children && router.children) {
      temp.children = router.children;
      continue;
    }

    if (temp.children && router.children) {
      temp.children = concatRouter(temp.children, router.children);
      continue;
    }
  }

  const list: RouteRecordRaw[] = [];
  map.forEach((router) => {
    list.push(router);
  });
  return list;
}

const store = defineStore("permission", {
  state: () => ({
    sidebarMenu: ref([]) as Ref<MenuRouter[]>,
    // routes: ref([]) as Ref<RouteRecordRaw[]>,
    tagsRouters: ref([]) as Ref<RouteRecordRaw[]>,
    sidebarRouters: ref([]) as Ref<RouteRecordRaw[]>,
    sidebarIsCollapsed: ref(false),
  }),
  getters: {},
  actions: {
    // 生成路由
    GenerateRoutes() {
      // 向后端请求路由数据
      return getRouters().then((res) => {
        const routers = res.data || [];
        //菜单展示
        this.sidebarMenu = getHomeMenu().concat(dealMenuPath(routers));
        //动态添加路由
        this.sidebarRouters = filterSidebarRouter(routers); // 菜单路由
        const asyncRoutes = filterDynamicRoutes(dynamicRoutes); // 功能路由
        this.tagsRouters = concatRouter(this.sidebarRouters, asyncRoutes); // 合并路由
        this.tagsRouters.forEach((route) => router.addRoute(route));
        // this.tagsRouterPaths = getTagsRouterPath(this.tagsRouters);
        //全部路由
        // this.routes = concatRouter(this.tagsRouters, constantRoutes);
        return this.tagsRouters;
      });
    },
    // Sidebar 是否折叠
    setSidebarIsCollapsed(value: boolean) {
      this.sidebarIsCollapsed = value;
    },
    // 判断是否是tag需要显示的路由
    isTagsRouter(fullPath: string) {
      if (fullPath === "/index") {
        return true;
      }
      return checkTagsRouter(this.tagsRouters, fullPath);
    },
  },
});

export default store;
