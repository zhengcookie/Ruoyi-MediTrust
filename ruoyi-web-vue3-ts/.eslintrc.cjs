/* eslint-env node */
require("@rushstack/eslint-patch/modern-module-resolution");

//https://eslint.vuejs.org/rules/

module.exports = {
  root: true,
  extends: [
    "plugin:vue/vue3-essential",
    "plugin:vue/vue3-recommended",
    // "plugin:vue/vue3-strongly-recommended",
    "eslint:recommended",
    "@vue/eslint-config-typescript/recommended",
    "@vue/eslint-config-prettier",
    // "@vue/eslint-config-prettier/skip-formatting", // 或者和esline分开执行prettier --write . --ignore-path .gitignore
  ],
  rules: {
    "vue/multi-word-component-names": "off", // 关闭名称校验
    "vue/html-closing-bracket-newline": "off", // html 不换行
  },
};
