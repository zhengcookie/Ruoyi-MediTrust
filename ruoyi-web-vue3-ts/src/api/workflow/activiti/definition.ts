import server from "@/utils/request";
import type { QueryParam } from "../../form";

export type ActDefinitionObj = {
  id: string;
  key?: string;
  name?: string;
  category?: string;
  version?: string;
  description?: string;
  deploymentId?: string;
  suspendState?: number;
};

// 查询ActDefinition列表
export function listDefinition(query: ActDefinitionObj & QueryParam) {
  return server.request<ActDefinitionObj>({
    url: "/activiti/processDefinition/list",
    method: "get",
    params: query,
  });
}

// 挂起激活转换
export function suspendOrActiveApply(data: ActDefinitionObj) {
  return server.request({
    url: "/activiti/processDefinition/suspendOrActiveApply",
    method: "post",
    data: data,
  });
}

// 删除ActDefinition
export function delDefinition(id: string) {
  return server.request({
    url: "/activiti/processDefinition/remove/" + id,
    method: "delete",
  });
}

// 流程定义转成模型
export function convert2Model(data: { processDefinitionId: string }) {
  return server.request({
    url: "/activiti/processDefinition/convert2Model",
    method: "post",
    params: data,
  });
}

// 上传并部署流程定义
export function uploadDefinition(data: { file: File }) {
  return server.request({
    url: "/activiti/processDefinition/upload",
    method: "post",
    headers: {
      "Content-Type": "multipart/form-data",
    },
    data: data,
  });
}
