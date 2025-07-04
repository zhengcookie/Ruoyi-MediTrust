import Cookies from "js-cookie";
import { defineStore } from "pinia";

const store = defineStore({
  id: "app",
  state: () => {
    //刷新后，重新加载缓存中的页签
    const sidebarStatus = Cookies.get("sidebarStatus");

    return {
      sidebar: {
        opened: sidebarStatus ? !!+sidebarStatus : true,
        withoutAnimation: false,
        hide: false,
      },
      device: "desktop",
      size: Cookies.get("size") || "medium",
    };
  },
  getters: {
    //方法名称参照官网
    //https://pinia.vuejs.org/core-concepts/getters.html#accessing-other-getters
  },
  actions: {
    toggleSideBar() {
      if (this.sidebar.hide) {
        return false;
      }
      this.sidebar.opened = !this.sidebar.opened;
      this.sidebar.withoutAnimation = false;
      if (this.sidebar.opened) {
        Cookies.set("sidebarStatus", "1");
      } else {
        Cookies.set("sidebarStatus", "0");
      }
    },
    closeSideBar(withoutAnimation: boolean) {
      Cookies.set("sidebarStatus", "0");
      this.sidebar.opened = false;
      this.sidebar.withoutAnimation = withoutAnimation;
    },
    toggleDevice(device: string) {
      this.device = device;
    },
    setSize(size: string) {
      this.size = size;
      Cookies.set("size", size);
    },
    toggleSideBarHide(status: boolean) {
      this.sidebar.hide = status;
    },
  },
});

export default store;
