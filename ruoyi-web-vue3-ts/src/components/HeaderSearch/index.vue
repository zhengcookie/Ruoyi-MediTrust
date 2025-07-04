<template>
  <div :class="{ show: show }" class="header-search">
    <el-icon class="search-icon" @click.stop="click">
      <Search />
    </el-icon>
    <el-select
      ref="headerSearchSelectRef"
      v-model="search"
      :remote-method="querySearch"
      filterable
      default-first-option
      remote
      placeholder="Search"
      class="header-search-select"
      @change="change"
    >
      <el-option v-for="option in options" :key="option.item.path" :value="option.item" :label="option.item.title.join(' > ')" />
    </el-select>
  </div>
</template>

<script lang="ts" setup name="HeaderSearchCmpt">
import Fuse, { type FuseResult } from "fuse.js";
import { isHttp } from "@/utils/validate";
import permissionStore from "@/stores/permission";
import { nextTick, onMounted, ref, watch, watchEffect } from "vue";
import router from "@/router";
import type { RouteRecordRaw } from "vue-router";
type SearchData = {
  path: string;
  title: string[];
  query?: string;
};

const search = ref("");
const options = ref<FuseResult<SearchData>[]>([]);
const searchPool = ref<SearchData[]>([]);
const show = ref(false);
const fuse = ref<Fuse<SearchData> | undefined>(undefined);
const headerSearchSelectRef = ref();
const route = router.currentRoute.value;
const pStore = permissionStore();

function click() {
  show.value = !show.value;
  if (show.value) {
    headerSearchSelectRef.value && headerSearchSelectRef.value.focus();
  }
}
function close() {
  headerSearchSelectRef.value && headerSearchSelectRef.value.blur();
  options.value = [];
  show.value = false;
}
function change(val: SearchData) {
  const path = val.path;
  const query = val.query;
  if (isHttp(path)) {
    // http(s):// 路径新窗口打开
    const pindex = path.indexOf("http");
    window.open(path.substring(pindex, path.length), "_blank");
  } else {
    if (query) {
      router.push({ path: path, query: JSON.parse(query) });
    } else {
      router.push(path);
    }
  }

  search.value = "";
  options.value = [];
  nextTick(() => {
    show.value = false;
  });
}
function initFuse(list: SearchData[]) {
  fuse.value = new Fuse(list, {
    shouldSort: true,
    threshold: 0.4,
    location: 0,
    distance: 100,
    minMatchCharLength: 1,
    keys: [
      {
        name: "title",
        weight: 0.7,
      },
      {
        name: "path",
        weight: 0.3,
      },
    ],
  });
}
// Filter out the routes that can be displayed in the sidebar
// And generate the internationalized title
function generateRoutes(routes: RouteRecordRaw[], prefixTitle: string[] = []) {
  let res: SearchData[] = [];

  for (const r of routes) {
    // skip hidden router
    if (r.meta?.hidden) {
      continue;
    }
    const data: SearchData = {
      path: r.meta?.fullPath ? r.meta?.fullPath : "/",
      title: [...prefixTitle],
    };

    if (r.meta && r.meta.title) {
      data.title = [...data.title, r.meta.title];

      if (r.redirect !== "noRedirect") {
        res.push(data);
      }
    }
    if (r.meta?.query) {
      data.query = r.meta.query;
    }

    // recursive child routes
    if (r.children) {
      const tempRoutes = generateRoutes(r.children, data.title);
      if (tempRoutes.length >= 1) {
        res = [...res, ...tempRoutes];
      }
    }
  }
  return res;
}
function querySearch(query: string) {
  if (query !== "") {
    options.value = fuse.value?.search(query) || [];
  } else {
    options.value = [];
  }
}

onMounted(() => {
  searchPool.value = generateRoutes(pStore.tagsRouters);
});

watchEffect(() => {
  searchPool.value = generateRoutes(pStore.tagsRouters);
});

watch(show, (value) => {
  if (value) {
    document.body.addEventListener("click", close);
  } else {
    document.body.removeEventListener("click", close);
  }
});

watch(searchPool, (list) => {
  initFuse(list);
});
</script>

<style lang="less" scoped>
.header-search {
  .search-icon {
    cursor: pointer;
    font-size: 24px;
    vertical-align: middle;
    margin-right: 5px;
  }

  .header-search-select {
    font-size: 18px;
    transition: width 0.2s;
    width: 0;
    overflow: hidden;
    background: transparent;
    border-radius: 0;
    display: inline-block;
    vertical-align: middle;

    :deep(.el-input__inner) {
      border-radius: 0;
      border: 0;
      padding-left: 0;
      padding-right: 0;
      box-shadow: none !important;
      border-bottom: 1px solid #d9d9d9;
      vertical-align: middle;
    }
  }

  &.show {
    .header-search-select {
      width: 210px;
      margin-right: 10px;
    }
  }
}
</style>
