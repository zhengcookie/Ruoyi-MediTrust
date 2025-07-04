import type { QueryParam } from "@/api/form";
import server from "@/utils/request";

export type DictTypeObj = {
  dictId?: string;
  dictName?: string;
  dictType?: string;
  status?: string;
  remark?: string;
};

// 查询字典类型列表
export function listType(query: DictTypeObj & QueryParam) {
  return server.request<DictTypeObj>({
    url: "/system/dict/type/list",
    method: "get",
    params: query,
  });
}

// 查询字典类型详细
export function getType(dictId: string) {
  return server.request<DictTypeObj>({
    url: "/system/dict/type/" + dictId,
    method: "get",
  });
}

// 新增字典类型
export function addType(data: DictTypeObj) {
  return server.request({
    url: "/system/dict/type",
    method: "post",
    data: data,
  });
}

// 修改字典类型
export function updateType(data: DictTypeObj) {
  return server.request({
    url: "/system/dict/type",
    method: "put",
    data: data,
  });
}

// 删除字典类型
export function delType(dictId: string) {
  return server.request({
    url: "/system/dict/type/" + dictId,
    method: "delete",
  });
}

// 刷新字典缓存
export function refreshCache() {
  return server.request({
    url: "/system/dict/type/refreshCache",
    method: "delete",
  });
}

// 获取字典选择框列表
export function optionselect() {
  return server.request<DictTypeObj[]>({
    url: "/system/dict/type/optionselect",
    method: "get",
  });
}
