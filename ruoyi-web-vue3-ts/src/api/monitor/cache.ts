import server from "@/utils/request";
export type CacheObj = {
  cacheName: string;
  cacheKey: string;
  cacheValue?: string;
};

export type CacheInfoObj = {
  info?: {
    redis_version?: string;
    redis_mode?: string;
    tcp_port?: string;
    connected_clients?: string;
    uptime_in_days?: string;
    used_memory_human?: string;
    used_cpu_user_children?: string;
    maxmemory_human?: string;
    aof_enabled?: string;
    rdb_last_bgsave_status?: string;
    instantaneous_input_kbps?: string;
    instantaneous_output_kbps?: string;
  };
  dbSize?: string;
  commandStats?: string;
};

// 查询缓存详细
export function getCache() {
  return server.request<CacheInfoObj>({
    url: "/monitor/cache",
    method: "get",
  });
}

// 查询缓存名称列表
export function listCacheName() {
  return server.request<string[]>({
    url: "/monitor/cache/getNames",
    method: "get",
  });
}

// 查询缓存键名列表
export function listCacheKey(cacheName: string) {
  return server.request<string[]>({
    url: "/monitor/cache/getKeys/" + cacheName,
    method: "get",
  });
}

// 查询缓存内容
export function getCacheValue(cacheName: string, cacheKey: string) {
  return server.request<CacheObj>({
    url: "/monitor/cache/getValue/" + cacheName + "/" + cacheKey,
    method: "get",
  });
}

// 清理指定名称缓存
export function clearCacheName(cacheName: string) {
  return server.request({
    url: "/monitor/cache/clearCacheName/" + cacheName,
    method: "delete",
  });
}

// 清理指定键名缓存
export function clearCacheKey(cacheKey: string) {
  return server.request({
    url: "/monitor/cache/clearCacheKey/" + cacheKey,
    method: "delete",
  });
}

// 清理全部缓存
export function clearCacheAll() {
  return server.request({
    url: "/monitor/cache/clearCacheAll",
    method: "delete",
  });
}
