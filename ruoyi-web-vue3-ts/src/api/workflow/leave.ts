import server from "@/utils/request";
import type { QueryParam } from "../form";

export type LeaveObj = {
  id: string;
  createName?: string;
  processType?: string;
  department?: string;
  type?: string;
  title?: string;
  reason?: string;
  leaveStartTime?: string;
  leaveEndTime?: string;
  instanceId?: string;
  state?: string;
  createBy?: string;
  createTime?: string;
  updateTime?: string;
};
// 查询请假列表
export function listLeave(query: LeaveObj & QueryParam) {
  return server.request<LeaveObj>({
    url: "/workflow/leave/list",
    method: "get",
    params: query,
  });
}

// 查询请假详细
export function getLeave(id: string) {
  return server.request<LeaveObj>({
    url: "/workflow/leave/" + id,
    method: "get",
  });
}

// 新增请假
export function addLeave(data: LeaveObj) {
  return server.request({
    url: "/workflow/leave",
    method: "post",
    data: data,
  });
}

// 修改请假
export function updateLeave(data: LeaveObj) {
  return server.request({
    url: "/workflow/leave",
    method: "put",
    data: data,
  });
}

// 删除请假
export function delLeave(id: LeaveObj) {
  return server.request({
    url: "/workflow/leave/" + id,
    method: "delete",
  });
}

// 导出请假
export function exportLeave(query: LeaveObj) {
  return server.request({
    url: "/workflow/leave/export",
    method: "get",
    params: query,
  });
}
