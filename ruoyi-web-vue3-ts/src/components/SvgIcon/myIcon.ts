import { defineComponent, h } from "vue";
import svgIcon from "./index.vue";
const modules = import.meta.glob("./../../assets/icons/svg/*.svg");

// 获取icon名称
const filenNmes = [];
for (const path in modules) {
  const name = path.split("assets/icons/svg/")[1].split(".svg");
  filenNmes.push(name[0]);
}

/**
 *  icons组件名称数组 (my + Filename)
 */
export const myIconNames = filenNmes.map((name) => "my" + name.replace(/^./, (L) => L.toUpperCase()));

/**
 *  icons组件数组
 */
const myIcons = filenNmes.map((name) => {
  return {
    name: "my" + name.replace(/^./, (L) => L.toUpperCase()),
    component: defineComponent({
      render() {
        return h(svgIcon, { iconClass: name });
      },
    }),
  };
});

export default myIcons;
