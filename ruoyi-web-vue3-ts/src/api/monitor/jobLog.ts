import server from "@/utils/request";
import type { QueryParam } from "../form";
export type JobLogObj = {
  jobLogId: string;
  jobName?: string;
  jobGroup?: string;
  createTime?: string;
  invokeTarget?: string;
  jobMessage?: string;
  status?: string;
  exceptionInfo?: string;
};
// 查询调度日志列表
export function listJobLog(query: JobLogObj & QueryParam) {
  return server.request<JobLogObj>({
    url: "/monitor/jobLog/list",
    method: "get",
    params: query,
  });
}

// 删除调度日志
export function delJobLog(jobLogId: string) {
  return server.request({
    url: "/monitor/jobLog/" + jobLogId,
    method: "delete",
  });
}

// 清空调度日志
export function cleanJobLog() {
  return server.request({
    url: "/monitor/jobLog/clean",
    method: "delete",
  });
}
