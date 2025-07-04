import server from "@/utils/request";
export type JobObj = {
  jobId: string;
  jobName?: string;
  jobGroup?: string;
  invokeTarget?: string;
  cronExpression?: string;
  misfirePolicy?: number;
  concurrent?: number;
  status?: string;
  createTime?: string;
  nextValidTime?: string;
};
// 查询定时任务调度列表
export function listJob(query: JobObj & { pageNum: number; pageSize: number }) {
  return server.request<JobObj>({
    url: "/monitor/job/list",
    method: "get",
    params: query,
  });
}

// 查询定时任务调度详细
export function getJob(jobId: string) {
  return server.request<JobObj>({
    url: "/monitor/job/" + jobId,
    method: "get",
  });
}

// 新增定时任务调度
export function addJob(data: JobObj) {
  return server.request({
    url: "/monitor/job",
    method: "post",
    data: data,
  });
}

// 修改定时任务调度
export function updateJob(data: JobObj) {
  return server.request({
    url: "/monitor/job",
    method: "put",
    data: data,
  });
}

// 删除定时任务调度
export function delJob(jobId: string) {
  return server.request({
    url: "/monitor/job/" + jobId,
    method: "delete",
  });
}

// 任务状态修改
export function changeJobStatus(jobId: string, status: string) {
  const data = {
    jobId,
    status,
  };
  return server.request({
    url: "/monitor/job/changeStatus",
    method: "put",
    data: data,
  });
}

// 定时任务立即执行一次
export function runJob(jobId: string, jobGroup: string) {
  const data = {
    jobId,
    jobGroup,
  };
  return server.request({
    url: "/monitor/job/run",
    method: "put",
    data: data,
  });
}
