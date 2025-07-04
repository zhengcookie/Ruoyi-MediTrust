import { defineStore } from "pinia";
import type { DictObj } from "@/api/system/dict/data";

type DictList = {
  key: string;
  value: DictObj[];
};

const store = defineStore("dict", {
  state: () => ({
    dict: [] as DictList[],
  }),
  getters: {
    //方法名称参照官网
    //https://pinia.vuejs.org/core-concepts/getters.html#accessing-other-getters
  },
  actions: {
    // 设置字典
    setDict(key: string, value: DictObj[]) {
      if (key !== null && key !== "") {
        this.dict.push({
          key: key,
          value: value,
        });
      }
    },
    // 获取字典
    getDict(key: string) {
      const index = this.dict.findIndex((dict) => dict.key == key);
      return index >= 0 ? this.dict[index].value : null;
    },
    // 删除字典
    removeDict(key: string) {
      try {
        for (let i = 0; i < this.dict.length; i++) {
          if (this.dict[i].key == key) {
            this.dict.splice(i, i);
            return true;
          }
        }
      } catch (e) {
        console.log(e);
      }
    },
    // 清空字典
    cleanDict() {
      this.dict = [] as DictList[];
    },
  },
});

export default store;
