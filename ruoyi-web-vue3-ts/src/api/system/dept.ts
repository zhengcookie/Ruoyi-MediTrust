import server from "@/utils/request";

export type DeptObj = {
  deptId: string;
  parentId?: string;
  deptName?: string;
  orderNum?: number;
  leader?: string;
  phone?: string;
  email?: string;
  status?: string;
  children?: DeptObj[];
};

export type TreeDeptObj = {
  id: number;
  label: string;
  children?: TreeDeptObj[];
};

// 查询部门列表
export function listDept(query?: DeptObj) {
  return server.request<DeptObj[]>({
    url: "/system/dept/list",
    method: "get",
    params: query,
  });
}

// 查询部门列表（排除节点）
export function listDeptExcludeChild(deptId: string) {
  return server.request<DeptObj[]>({
    url: "/system/dept/list/exclude/" + deptId,
    method: "get",
  });
}

// 查询部门详细
export function getDept(deptId: string) {
  return server.request<DeptObj>({
    url: "/system/dept/" + deptId,
    method: "get",
  });
}

// 新增部门
export function addDept(data: DeptObj) {
  return server.request({
    url: "/system/dept",
    method: "post",
    data: data,
  });
}

// 修改部门
export function updateDept(data: DeptObj) {
  return server.request({
    url: "/system/dept",
    method: "put",
    data: data,
  });
}

// 删除部门
export function delDept(deptId: string) {
  return server.request({
    url: "/system/dept/" + deptId,
    method: "delete",
  });
}
