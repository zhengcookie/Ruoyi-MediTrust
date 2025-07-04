import server from "@/utils/request";
import type { QueryParam } from "../form";

export type LoginLogObj = {
  browser?: string;
  createBy?: string;
  createTime?: string;
  infoId: string;
  ipaddr?: string;
  loginLocation?: string;
  loginTime?: string;
  msg?: string;
  os?: string;
  remark?: string;
  status?: string;
  updateBy?: string;
  updateTime?: string;
  userName?: string;
};
// 查询登录日志列表
export function list(query: LoginLogObj & QueryParam) {
  return server.request<LoginLogObj>({
    url: "/monitor/logininfor/list",
    method: "get",
    params: query,
  });
}

// 删除登录日志
export function delLogininfor(infoId: string) {
  return server.request({
    url: "/monitor/logininfor/" + infoId,
    method: "delete",
  });
}

// 解锁用户登录状态
export function unlockLogininfor(userName: string) {
  return server.request({
    url: "/monitor/logininfor/unlock/" + userName,
    method: "get",
  });
}

// 清空登录日志
export function cleanLogininfor() {
  return server.request({
    url: "/monitor/logininfor/clean",
    method: "delete",
  });
}
