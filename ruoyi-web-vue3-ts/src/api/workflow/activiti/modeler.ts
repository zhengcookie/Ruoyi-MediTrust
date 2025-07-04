import server from "@/utils/request";
import type { QueryParam } from "../../form";

export type XmlMetaInfo = {
  key?: string;
  name?: string;
  description?: string;
  version?: number;
};

export type ActModelerObj = {
  id: string;
  key?: string;
  name?: string;
  version?: string;
  createTime?: string;
  lastUpdateTime?: string;
  description?: string;
  bpmnXml?: string;
  svgXml?: string;
  metaInfo?: string;
};

// 查询模型列表
export function listModeler(query: ActModelerObj & QueryParam) {
  return server.request<ActModelerObj>({
    url: "/activiti/modeler/list",
    method: "get",
    params: query,
  });
}

// 新增模型
export function addModeler(data: ActModelerObj) {
  return server.request<string>({
    url: "/activiti/modeler/create",
    method: "post",
    params: data,
  });
}

// 删除模型
export function delModeler(modelId: string) {
  return server.request({
    url: "/activiti/modeler/remove/" + modelId,
    method: "delete",
  });
}

// 导出模型
export function exportModeler(modelId: string) {
  return server.request({
    url: "/activiti/modeler/export/" + modelId,
    method: "get",
  });
}

// 部署模型
export function deployModeler(modelId: string) {
  return server.request({
    url: "/activiti/modeler/deploy/" + modelId,
    method: "get",
  });
}

// 获取Modeler详情
export function getModelerDetail(modelId: string) {
  return server.request<ActModelerObj>({
    url: "/activiti/modeler/detail/" + modelId,
    method: "get",
  });
}

// 部署模型
export function updateModeler(data: ActModelerObj) {
  return server.request({
    url: "/activiti/modeler/save",
    method: "put",
    data: data,
  });
}
