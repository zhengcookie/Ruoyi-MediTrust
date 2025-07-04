// 添加全局声明
declare global {
  interface Window {
    isShowingError?: boolean;
  }
}

import axios from "axios";
import type { AxiosInstance, AxiosRequestConfig } from "axios";
import { ElLoading, ElMessage, ElMessageBox, ElNotification } from "element-plus";
import { getToken } from "@/utils/auth";
import { blobValidate, tansParams } from "./ruoyi";
import cache from "@/plugins/cache";
import errorCode from "./errorCode";
import userStore from "@/stores/user";
import { saveAs } from "file-saver";

type ResponseData<T = unknown> = {
  msg?: string;
  code?: number;
  data?: T;
  rows?: T[];
  total?: number;
};

// 是否显示重新登录
export const isRelogin = { show: false };

axios.defaults.headers["Content-Type"] = "application/json;charset=utf-8";

// const err = (error: { response: { status?: number }; message?: string }) => {
//   if (error.response) {
//     switch (error?.response?.status) {
//       case 403:
//         ElMessage.error("403");
//         break;
//       case 500:
//         ElMessage.error("403");
//         break;
//       case 404:
//         ElMessage.error("404");
//         break;
//       case 504:
//         ElMessage.error("504");
//         break;
//       case 401:
//         ElMessage.error("401");
//         break;
//       default:
//         ElMessage.error("系统错误");
//         break;
//     }
//   } else if (error?.message) {
//     ElMessage.error(error.message);
//   }
//   return Promise.reject(error);
// };

export class Service {
  // axios 实例
  instance: AxiosInstance;
  // 基础配置，url和超时时间
  baseConfig: AxiosRequestConfig = { baseURL: import.meta.env.VITE_APP_BASE_API, timeout: 30000 };

  constructor(config: AxiosRequestConfig) {
    // 使用axios.create创建axios实例，配置为基础配置和我们传递进来的配置
    this.instance = axios.create(Object.assign(this.baseConfig, config));

    // request拦截器
    this.instance.interceptors.request.use(
      (config) => {
        // 是否需要设置 token
        const isToken = (config.headers || {}).isToken === false;
        // 是否需要防止数据重复提交
        const isRepeatSubmit = (config.headers || {}).repeatSubmit === false;
        if (getToken() && !isToken) {
          config.headers["Authorization"] = "Bearer " + getToken(); // 让每个请求携带自定义token 请根据实际情况自行修改
        }
        // get请求映射params参数
        if (config.method === "get" && config.params) {
          let url = config.url + "?" + tansParams(config.params);
          url = url.slice(0, -1);
          config.params = {};
          config.url = url;
        }
        if (!isRepeatSubmit && (config.method === "post" || config.method === "put")) {
          const requestObj = {
            url: config.url,
            data: config.data instanceof Object ? JSON.stringify(config.data) : config.data,
            time: new Date().getTime(),
          };
          const sessionObj = cache.session.getJSON<{
            url: string;
            data: { url: string; data: unknown; time: number };
            time: number;
          }>("sessionObj");
          if (sessionObj === null) {
            cache.session.setJSON("sessionObj", requestObj);
          } else {
            const s_url = sessionObj.url; // 请求地址
            const s_data = sessionObj.data; // 请求数据
            const s_time = sessionObj.time; // 请求时间
            const interval = 1000; // 间隔时间(ms)，小于此时间视为重复提交
            if (s_data === requestObj.data && requestObj.time - s_time < interval && s_url === requestObj.url) {
              const message = "数据正在处理，请勿重复提交";
              console.warn(`[${s_url}]: ` + message);
              return Promise.reject(new Error(message));
            } else {
              cache.session.setJSON("sessionObj", requestObj);
            }
          }
        }
        return config;
      },
      (error) => {
        console.log(error);
        Promise.reject(error);
      }
    );

    // 响应拦截器
    this.instance.interceptors.response.use(
      (res) => {
        // 未设置状态码则默认成功状态
        const code = res.data.code || 200;
        // 获取错误信息
        const msg = errorCode[code] || res.data.msg || errorCode["default"];
        // 二进制数据则直接返回
        if (res.request.responseType === "blob" || res.request.responseType === "arraybuffer") {
          return res.data;
        }
        if (code === 401) {
          // 注释掉或移除登录状态过期的弹窗
          /*
          if (!isRelogin.show) {
            isRelogin.show = true;
            ElMessageBox.confirm("登录状态已过期，您可以继续留在该页面，或者重新登录", "系统提示", {
              confirmButtonText: "重新登录",
              cancelButtonText: "取消",
              type: "warning",
            })
              .then(() => {
                isRelogin.show = false;
                const store = userStore();
                store.LogOut().then(() => {
                  location.href = "/login";
                });
              })
              .catch(() => {
                isRelogin.show = false;
              });
          }
          */
          // 静默处理，只在控制台输出信息，不显示弹窗
          console.log("登录状态已过期");
          const store = userStore();
          store.LogOut();
          return Promise.reject("无效的会话，或者会话已过期，请重新登录。");
        } else if (code === 500) {
          ElMessage({ message: msg, type: "error" });
          return Promise.reject(new Error(msg));
        } else if (code === 601) {
          ElMessage({ message: msg, type: "warning" });
          return Promise.reject("error");
        } else if (code !== 200) {
          ElNotification.error({ title: msg });
          return Promise.reject("error");
        } else {
          return res.data;
        }
      },
      (error) => {
        console.log("err" + error);
        let { message } = error;
        if (message == "Network Error") {
          message = "后端接口连接异常";
        } else if (message.includes("timeout")) {
          message = "系统接口请求超时";
        } else if (message.includes("Request failed with status code")) {
          message = "系统接口" + message.substr(message.length - 3) + "异常";
        }
        
        // 防止循环弹出错误提示
        if (!window.isShowingError) {
          window.isShowingError = true;
          ElMessage({ 
            message: message, 
            type: "error", 
            duration: 5 * 1000,
            onClose: () => {
              window.isShowingError = false;
            }
          });
        }
        
        return Promise.reject(error);
      }
    );
  }

  /**
   * 请求方法 此接口后台参数不标准，建议调整后台接口
   * @param config
   * @returns Promise
   */
  public requestT<T = unknown>(config: AxiosRequestConfig): Promise<ResponseData & T> {
    try {
      return this.instance.request(config);
    } catch (error) {
      console.error("请求出错:", error);
      return Promise.reject(error);
    }
  }

  /**
   * 请求方法
   * @param config
   * @returns Promise
   */
  public request<T = unknown>(config: AxiosRequestConfig): Promise<ResponseData<T>> {
    try {
      return this.instance.request(config);
    } catch (error) {
      console.error("请求出错:", error);
      return Promise.reject(error);
    }
  }

  /**
   * get请求
   * @param url
   * @param config
   * @returns
   */
  public get<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<ResponseData<T>> {
    return this.instance.get(url, config);
  }

  /**
   * post 请求
   * @param url
   * @param data
   * @param config
   * @returns
   */
  public post<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<ResponseData<T>> {
    return this.instance.post(url, data, config);
  }

  /**
   * put 请求
   * @param url
   * @param data
   * @param config
   * @returns
   */
  public put<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<ResponseData<T>> {
    return this.instance.put(url, data, config);
  }

  /**
   * delete 请求
   * @param url
   * @param data
   * @param config
   * @returns
   */
  public delete<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<ResponseData<T>> {
    return this.instance.delete(url, config);
  }

  /**
   * 下载
   * @param url
   * @param params
   * @param filename
   * @param config
   * @returns Promise
   */
  public download<T>(url: string, params: T, filename: string, config: AxiosRequestConfig = {}) {
    const downloadLoading = ElLoading.service({
      text: "正在下载数据，请稍候",
      spinner: "el-icon-loading",
      background: "rgba(0, 0, 0, 0.7)",
    });
    return this.instance
      .post<T, Blob>(url, params, {
        transformRequest: [
          (params) => {
            return tansParams(params);
          },
        ],
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        responseType: "blob",
        ...config,
      })
      .then(async (data) => {
        const isLogin = await blobValidate(data);
        if (isLogin) {
          const blob = new Blob([data]);
          saveAs(blob, filename);
        } else {
          const resText = await data.text();
          const rspObj = JSON.parse(resText);
          const errMsg = errorCode[rspObj.code] || rspObj.msg || errorCode["default"];
          ElMessage.error(errMsg);
        }
        downloadLoading.close();
      })
      .catch((r) => {
        console.error(r);
        ElMessage.error("下载文件出现错误，请联系管理员！");
        downloadLoading.close();
      });
  }
}

// 默认导出Request实例
export default new Service({});
