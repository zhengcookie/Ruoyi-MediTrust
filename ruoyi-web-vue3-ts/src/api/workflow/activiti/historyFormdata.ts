import server from "@/utils/request";

export type ApprovalHisObj = {
  id: string;
  taskNodeName?: string;
  createName?: string;
  createdDate?: string;
  formHistoryDataDTO?: {
    title?: string;
    value?: string;
  }[];
};
// 查询请假详细
export function historyFromData(instanceId: string) {
  return server.request<ApprovalHisObj[]>({
    url: "/historyFromData/ByInstanceId/" + instanceId,
    method: "get",
  });
}
