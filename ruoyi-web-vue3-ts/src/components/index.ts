// 字典标签组件
import DictTag from "./DictTag/index.vue";
import Pagination from "./Pagination/index.vue";
import RightToolbar from "./RightToolbar/index.vue";

import type { App } from "vue";

const myDirective = {
  version: "1.0.0",
  install: (app: App) => {
    // 全局组件挂载
    app.component("DictTag", DictTag);
    app.component("Pagination", Pagination);
    app.component("RightToolbar", RightToolbar);
  },
};

export default myDirective;
