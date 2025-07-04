<template>
  <el-breadcrumb class="app-breadcrumb" separator="/">
    <transition-group name="breadcrumb">
      <el-breadcrumb-item v-for="item in levelList" :key="item.path">
        <span v-if="item.redirect === 'noRedirect'" class="no-redirect">{{ item.meta.title }}</span>
        <a v-else @click.prevent="handleLink(item)">{{ item.meta.title }}</a>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script lang="ts" setup name="Breadcrumb">
import { ref, watchEffect } from "vue";
import type { RouteLocationRaw, RouteMeta } from "vue-router";
// @ts-ignore 
import { useRoute, useRouter } from "vue-router";

type CrumbObj = {
  path: string;
  meta: any; // 临时改为any类型
  redirect?: any;
};

const route = useRoute();
const router = useRouter();
const levelList = ref<CrumbObj[]>([]);

function getBreadcrumb() {
  levelList.value = [];
  route.matched.forEach((item: any) => {
    if (item.meta && item.meta.title && item.name != "index") {
      levelList.value.push({
        path: item.path,
        meta: item.meta,
        redirect: item.redirect as RouteLocationRaw,
      });
    }
  });
  const index: CrumbObj = { path: "/index", meta: { title: "首页" } };
  levelList.value = [index].concat(levelList.value);
}

function handleLink(item: CrumbObj) {
  const { redirect, path } = item;
  if (redirect) {
    router.push(redirect);
    return;
  }
  router.push(path);
}

watchEffect(() => {
  // if you go to the redirect page, do not update the breadcrumbs
  if (route.path.startsWith("/redirect/")) {
    return;
  }
  getBreadcrumb();
});
getBreadcrumb();
</script>

<style lang="less" scoped>
.app-breadcrumb.el-breadcrumb {
  font-size: 14px;
  padding-bottom: 5px;

  .no-redirect {
    color: #97a8be;
    cursor: text;
  }
}
</style>
