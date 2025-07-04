import server from "@/utils/request";
import type { QueryParam } from "../form";
export type SysConfigObj = {
  configId?: string;
  configName?: string;
  configKey?: string;
  configValue?: string;
  configType?: string;
  remark?: string;
};
// 查询参数列表
export function listConfig(query: SysConfigObj & QueryParam) {
  return server.request<SysConfigObj>({
    url: "/system/config/list",
    method: "get",
    params: query,
  });
}

// 查询参数详细
export function getConfig(configId: string) {
  return server.request<SysConfigObj>({
    url: "/system/config/" + configId,
    method: "get",
  });
}

// 根据参数键名查询参数值
export function getConfigKey(configKey: string) {
  return server.request({
    url: "/system/config/configKey/" + configKey,
    method: "get",
  });
}

// 新增参数配置
export function addConfig(data: SysConfigObj) {
  return server.request({
    url: "/system/config",
    method: "post",
    data: data,
  });
}

// 修改参数配置
export function updateConfig(data: SysConfigObj) {
  return server.request({
    url: "/system/config",
    method: "put",
    data: data,
  });
}

// 删除参数配置
export function delConfig(configId: string) {
  return server.request({
    url: "/system/config/" + configId,
    method: "delete",
  });
}

// 刷新参数缓存
export function refreshCache() {
  return server.request({
    url: "/system/config/refreshCache",
    method: "delete",
  });
}
