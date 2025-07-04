import vue from "@vitejs/plugin-vue";

import createSvgIcon from "./svg-icon";

export default function createVitePlugins(viteEnv: string, isBuild = false) {
  const vitePlugins = [vue()];
  vitePlugins.push(createSvgIcon(isBuild));
  return vitePlugins;
}
