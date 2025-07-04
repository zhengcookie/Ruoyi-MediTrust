import server from "@/utils/request";
import type { TreeDeptObj } from "./dept";
import type { UserInfoObj } from "./user";
import type { QueryParam } from "../form";
import type { TreeKey } from "element-plus/es/components/tree/src/tree.type";

export type RoleObj = {
  roleId: string;
  roleName?: string;
  roleKey?: string;
  createTime?: string;
  flag?: boolean;
  dataScope?: number;
  status?: string;
  roleSort?: number;
  menuIds?: TreeKey[];
  deptIds?: TreeKey[];
  menuCheckStrictly?: boolean;
  deptCheckStrictly?: boolean;
  remark?: string;
};

// 查询角色列表
export function listRole(query: RoleObj & QueryParam) {
  return server.request<RoleObj>({
    url: "/system/role/list",
    method: "get",
    params: query,
  });
}

// 查询角色详细
export function getRole(roleId: string) {
  return server.request<RoleObj>({
    url: "/system/role/" + roleId,
    method: "get",
  });
}

// 新增角色
export function addRole(data: RoleObj) {
  return server.request({
    url: "/system/role",
    method: "post",
    data: data,
  });
}

// 修改角色
export function updateRole(data: RoleObj) {
  return server.request({
    url: "/system/role",
    method: "put",
    data: data,
  });
}

// 角色数据权限
export function dataScope(data: RoleObj) {
  return server.request({
    url: "/system/role/dataScope",
    method: "put",
    data: data,
  });
}

// 角色状态修改
export function changeRoleStatus(roleId: string, status: string) {
  const data = {
    roleId,
    status,
  };
  return server.request({
    url: "/system/role/changeStatus",
    method: "put",
    data: data,
  });
}

// 删除角色
export function delRole(roleId: string) {
  return server.request({
    url: "/system/role/" + roleId,
    method: "delete",
  });
}

// 查询角色已授权用户列表
export function allocatedUserList(query: { pageNum: number; pageSize: number; roleId: string; userName: string; phonenumber: string }) {
  return server.request<UserInfoObj>({
    url: "/system/role/authUser/allocatedList",
    method: "get",
    params: query,
  });
}

// 查询角色未授权用户列表
export function unallocatedUserList(query: RoleObj) {
  return server.request<UserInfoObj>({
    url: "/system/role/authUser/unallocatedList",
    method: "get",
    params: query,
  });
}

// 取消用户授权角色
export function authUserCancel(data: { userId: string; roleId: string }) {
  return server.request({
    url: "/system/role/authUser/cancel",
    method: "put",
    data: data,
  });
}

// 批量取消用户授权角色
export function authUserCancelAll(data: { roleId: string; userIds: string }) {
  return server.request({
    url: "/system/role/authUser/cancelAll",
    method: "put",
    params: data,
  });
}

// 授权用户选择
export function authUserSelectAll(data: { roleId: string; userIds: string }) {
  return server.request({
    url: "/system/role/authUser/selectAll",
    method: "put",
    params: data,
  });
}

// 根据角色ID查询部门树结构
export function deptTreeSelect(roleId: string) {
  return server.requestT<{ depts: TreeDeptObj[]; checkedKeys: number[] }>({
    url: "/system/role/deptTree/" + roleId,
    method: "get",
  });
}
