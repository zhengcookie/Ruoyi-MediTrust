import cache from "@/plugins/cache";
import type { RouteLocationNormalized, RouteRecordName } from "vue-router";
import { defineStore } from "pinia";

export type Tag = {
  fullPath: string;
  name: RouteRecordName | null | undefined;
  title: string;
};

// declare interface TagStore {
//   tagsList: Tag[],
//   visitedViews: RouteLocationNormalized[],
//   iframeViews: RouteLocationNormalized[],
// }

const store = defineStore("tagsView", {
  state: () => {
    //刷新后，重新加载缓存中的页签
    const tagsList = cache.session.getJSON<Tag[]>("store_tagsList") || [];
    // const route: RouteLocationNormalized | undefined
    return {
      activePath: "",
      tagsList: tagsList,
      visitedViews: [] as RouteLocationNormalized[],
      iframeViews: [] as RouteLocationNormalized[],
    };
  },
  getters: {
    //方法名称参照官网
    //https://pinia.vuejs.org/core-concepts/getters.html#accessing-other-getters
  },
  actions: {
    setTagsItem(tagsList: Tag[]) {
      this.tagsList = tagsList;
      cache.session.setJSON("store_tagsList", this.tagsList);
    },
    addTagsItem(tag: Tag) {
      if (!this.tagsList) {
        this.tagsList = [];
      }
      this.tagsList.push(tag);
      this.setTagsItem(this.tagsList);
    },
    updateTagsItem(tag: Tag) {
      const index = this.tagsList.findIndex((item) => tag.fullPath === item.fullPath);
      if (index >= 0) {
        this.tagsList[index] = tag;
        this.setTagsItem(this.tagsList);
      } else {
        this.addTagsItem(tag);
      }
    },
    delTagsItem(index: number) {
      if (this.tagsList && this.tagsList.length > 0) {
        this.tagsList.splice(index, 1);
        this.setTagsItem(this.tagsList);
      }
    },
    clearAllTags() {
      this.setTagsItem([]);
    },
    closeTagsOther(tagsList: Tag[]) {
      this.setTagsItem(tagsList);
    },
    // 关闭指定tab页签
    closePage(path?: string) {
      if (!path) {
        path = this.activePath;
      }
      // 删除当前页面
      const index = this.tagsList.findIndex((item) => item.fullPath === path);
      index >= 0 && this.delTagsItem(index);
      // 设置下一页面
      if (path == this.activePath) {
        const nextTag = this.tagsList[index] ? this.tagsList[index] : this.tagsList[index - 1];
        this.activePath = nextTag && nextTag.fullPath ? nextTag.fullPath : "/index";
      }
    },
    // 关闭当前打开指定页面
    closeOpenPage(toPath: string, path?: string) {
      if (!path) {
        path = this.activePath;
      }
      // 删除当前页面
      const index = this.tagsList.findIndex((item) => item.fullPath === path);
      index >= 0 && this.delTagsItem(index);
      // 设置下一页面
      this.activePath = toPath;
    },
  },
});

export default store;
