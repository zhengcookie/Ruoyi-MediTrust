import server from "@/utils/request";

export type MenuRouter = {
  name: string;
  path: string;
  hidden?: boolean;
  redirect?: string;
  component?: string;
  alwaysShow?: boolean;
  meta: {
    title: string;
    icon?: string;
    noCache?: boolean;
    link?: string;
    fullPath: string;
  };
  children?: MenuRouter[];
};

// 获取路由
export const getRouters = () => {
  return server.request<MenuRouter[]>({
    url: "/getRouters",
    method: "get",
  });
};
