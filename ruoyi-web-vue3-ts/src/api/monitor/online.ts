import server from "@/utils/request";
export type OnlineInfoObj = {
  ipaddr?: string;
  userName?: string;
  tokenId: string;
};
// 查询在线用户列表
export function list(query: OnlineInfoObj & { pageNum: number; pageSize: number }) {
  return server.request<OnlineInfoObj>({
    url: "/monitor/online/list",
    method: "get",
    params: query,
  });
}

// 强退用户
export function forceLogout(tokenId: string) {
  return server.request({
    url: "/monitor/online/" + tokenId,
    method: "delete",
  });
}
