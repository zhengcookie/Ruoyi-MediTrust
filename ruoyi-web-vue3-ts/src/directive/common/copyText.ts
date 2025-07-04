/**
 * v-copyText 复制文本内容
 */
import type { Directive } from "vue";

const copyText: Directive<HTMLElement & { $copyValue: string; $copyCallback: (status: boolean, value: string) => void; $destroyCopy: () => void }, string | VoidFunction> = {
  beforeMount(el, { value, arg }) {
    console.log(el, value, arg);
    if (arg === "callback" && typeof value === "function") {
      el.$copyCallback = value;
    } else if (typeof value === "string") {
      el.$copyValue = value;
      // 点击 复制
      const handler = () => {
        if (!navigator.clipboard) {
          console.error("当前环境非https，不支持复制");
          return;
        }
        // 复制内容
        navigator.clipboard.writeText(el.$copyValue).then(
          () => {
            el.$copyCallback && el.$copyCallback(true, el.$copyValue);
          },
          () => {
            el.$copyCallback && el.$copyCallback(false, el.$copyValue);
          }
        );
      };
      el.addEventListener("click", handler);
      el.$destroyCopy = () => el.removeEventListener("click", handler);
    }
  },
};

export default copyText;
