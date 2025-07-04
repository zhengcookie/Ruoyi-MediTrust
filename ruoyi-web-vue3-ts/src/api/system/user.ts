import server from "@/utils/request";
import { parseStrEmpty } from "@/utils/ruoyi";
import type { RoleObj } from "./role";
import type { DeptObj } from "./dept";
import type { PostObj } from "./post";
import type { QueryParam } from "../form";

export type UserInfoObj = {
  userId: string;
  avatar?: string;
  deptId?: string;
  nickName?: string;
  userName?: string;
  phonenumber?: string;
  password?: string;
  email?: string;
  sex?: string;
  createTime?: string;
  status?: string;
  remark?: string;
  dept?: DeptObj;
  postIds?: string[];
  roleIds?: string[];
};

// 查询用户列表
export function listUser(query: UserInfoObj & QueryParam) {
  return server.request<UserInfoObj>({
    url: "/system/user/list",
    method: "get",
    params: query,
  });
}

// 查询用户详细
export function getUser(userId: string) {
  return server.requestT<{
    data: UserInfoObj;
    posts: PostObj[];
    roles: RoleObj[];
    postIds: string[];
    roleIds: string[];
  }>({
    url: "/system/user/" + parseStrEmpty(userId),
    method: "get",
  });
}

// 新增用户
export function addUser(data: UserInfoObj) {
  return server.request({
    url: "/system/user",
    method: "post",
    data: data,
  });
}

// 修改用户
export function updateUser(data: UserInfoObj) {
  return server.request({
    url: "/system/user",
    method: "put",
    data: data,
  });
}

// 删除用户
export function delUser(userId: string) {
  return server.request({
    url: "/system/user/" + userId,
    method: "delete",
  });
}

// 用户密码重置
export function resetUserPwd(userId: string, password: string) {
  const data = {
    userId,
    password,
  };
  return server.request({
    url: "/system/user/resetPwd",
    method: "put",
    data: data,
  });
}

// 用户状态修改
export function changeUserStatus(userId: string, status: string) {
  const data = {
    userId,
    status,
  };
  return server.request({
    url: "/system/user/changeStatus",
    method: "put",
    data: data,
  });
}

// 查询用户个人信息
export function getUserProfile() {
  return server.requestT<{
    data: UserInfoObj;
    roleGroup: string;
    postGroup: string;
  }>({
    url: "/system/user/profile",
    method: "get",
  });
}

// 修改用户个人信息
export function updateUserProfile(data: UserInfoObj) {
  return server.request({
    url: "/system/user/profile",
    method: "put",
    data: data,
  });
}

// 用户密码重置
export function updateUserPwd(oldPassword: string, newPassword: string) {
  const data = {
    oldPassword,
    newPassword,
  };
  return server.request({
    url: "/system/user/profile/updatePwd",
    method: "put",
    params: data,
  });
}

// 用户头像上传
export function uploadAvatar(data: FormData) {
  return server.requestT<{ imgUrl: string }>({
    url: "/system/user/profile/avatar",
    method: "post",
    data: data,
  });
}

// 查询授权角色
export function getAuthRole(userId: string) {
  return server.requestT<{ user: UserInfoObj; roles: RoleObj[] }>({
    url: "/system/user/authRole/" + userId,
    method: "get",
  });
}

// 保存授权角色
export function updateAuthRole(data: { userId: string; roleIds: string }) {
  return server.request({
    url: "/system/user/authRole",
    method: "put",
    params: data,
  });
}

// 查询部门下拉树结构
export function deptTreeSelect() {
  return server.request<DeptObj[]>({
    url: "/system/user/deptTree",
    method: "get",
  });
}
