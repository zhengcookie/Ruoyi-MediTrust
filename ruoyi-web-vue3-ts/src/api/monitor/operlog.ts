import server from "@/utils/request";

export type OperLogObj = {
  businessType: string;
  businessTypes?: string;
  createBy?: string;
  createTime?: string;
  deptName?: string;
  errorMsg?: string;
  jsonResult?: string;
  method?: string;
  operId: string;
  operIp?: string;
  operLocation?: string;
  operName?: string;
  operParam?: string;
  costTime?: string;
  operTime?: string;
  operUrl?: string;
  operatorType?: string;
  remark?: string;
  requestMethod?: string;
  status?: number;
  title?: string;
  updateBy?: string;
  updateTime?: string;
};
// 查询操作日志列表
export function list(query: OperLogObj & { pageNum: number; pageSize: number }) {
  return server.request<OperLogObj>({
    url: "/monitor/operlog/list",
    method: "get",
    params: query,
  });
}

// 删除操作日志
export function delOperlog(operId: string) {
  return server.request({
    url: "/monitor/operlog/" + operId,
    method: "delete",
  });
}

// 清空操作日志
export function cleanOperlog() {
  return server.request({
    url: "/monitor/operlog/clean",
    method: "delete",
  });
}
