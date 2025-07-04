import { login, logout, getInfo } from "@/api/login";
import type { LoginReq } from "@/api/login";
import { getToken, setToken, removeToken } from "@/utils/auth";
import { defineStore } from "pinia";

const store = defineStore("user", {
  state: () => {
    return {
      token: getToken(),
      name: "",
      nickName: "",
      phonenumber: "",
      email: "",
      avatar: "",
      roles: [] as string[],
      permissions: [] as string[],
    };
  },
  getters: {
    //方法名称参照官网
    //https://pinia.vuejs.org/core-concepts/getters.html#accessing-other-getters
  },
  actions: {
    // 登录
    Login(userInfo: LoginReq) {
      const username = userInfo.username.trim();
      const password = userInfo.password;
      const code = userInfo.code;
      const uuid = userInfo.uuid;
      return login({ username, password, code, uuid }).then((res) => {
        setToken(res.token);
        this.token = res.token;
        return res.token;
      });
    },

    // 获取用户信息
    GetInfo() {
      return getInfo().then((res) => {
        const user = res.user;
        const avatar = user.avatar == "" || user.avatar == null ? "/images/profile.jpg" : import.meta.env.VITE_APP_BASE_API + user.avatar;
        if (res.roles && res.roles.length > 0) {
          // 验证返回的roles是否是一个非空数组
          this.roles = res.roles;
          this.permissions = res.permissions;
        } else {
          this.roles = ["ROLE_DEFAULT"];
        }
        this.name = user.userName || "";
        this.nickName = user.nickName || "";
        this.avatar = avatar;
        return res;
      });
    },

    // 退出系统
    LogOut() {
      return logout().then(() => {
        this.token = "";
        this.roles = [];
        this.permissions = [];
        removeToken();
        return this.token;
      });
    },

    // 前端 登出
    FedLogOut() {
      return new Promise((resolve) => {
        this.token = "";
        removeToken();
        resolve(this.token);
      });
    },
  },
});

export default store;
