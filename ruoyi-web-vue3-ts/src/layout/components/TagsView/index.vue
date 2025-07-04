<template>
  <div v-if="showTags" class="tags">
    <div ref="tagsRef" class="tags-box" @wheel.stop="tabsWheel">
      <ul ref="tabsUlRef">
        <li v-for="item in tagsList" ref="tabsLiRef" :key="item.fullPath" class="tags-li" :class="{ active: isActive(item.fullPath) }">
          <router-link :to="item.fullPath" class="tags-li-title">{{ item.title }}</router-link>
          <span class="tags-li-icon" @click="closeTags(item)">
            <el-icon>
              <Close />
            </el-icon>
          </span>
        </li>
      </ul>
    </div>

    <div class="tags-close-box">
      <el-dropdown @command="handleTags">
        <el-button size="small" type="primary">
          标签选项
          <el-icon>
            <ArrowDown />
          </el-icon>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu size="small">
            <el-dropdown-item command="other">关闭其他</el-dropdown-item>
            <el-dropdown-item command="all">关闭所有</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script lang="ts" setup name="TagsViewLayout">
import { computed, ref, watch, VueElement, nextTick } from "vue";
import tagsViewStore, { type Tag } from "@/stores/tagsView";
import permissionStore from "@/stores/permission";
import router from "@/router";

const route = router.currentRoute.value;
const isActive = (path: string) => {
  return path === route.fullPath;
};

const tStore = tagsViewStore();
const pStore = permissionStore();
const tagsList = computed(() => tStore.tagsList);
const showTags = computed(() => tStore.tagsList.length > 0);

watch(
  () => tStore.activePath,
  (value) => {
    value != route.fullPath && router.push(value);
  }
);

watch(
  () => route.fullPath,
  (value) => {
    tStore.activePath = route.fullPath;
    value && setTags();
    nextTick(() => {
      scrollToActiveTag();
    });
  }
);

// 关闭单个标签
const closeTags = (tag: Tag) => {
  tStore.closePage(tag.fullPath);
};

// 设置标签
const setTags = () => {
  // 更新tags数据
  if (pStore.isTagsRouter(route.meta.fullPath || "")) {
    tStore.updateTagsItem({
      name: route.name,
      title: route.meta.title || "",
      fullPath: route.fullPath,
    });
  }
};

// 当前路由地址
tStore.activePath = route.fullPath;
// setTags(route);
// onBeforeRouteLeave((to) => {
//   setTags(to);
// });

// 关闭全部标签
const closeAll = () => {
  tStore.clearAllTags();
  router.push("/index");
};
// 关闭其他标签
const closeOther = () => {
  const curItem = tagsList.value.filter((item) => {
    return item.fullPath === route.fullPath;
  });
  tStore.closeTagsOther(curItem);
};
const handleTags = (command: string) => {
  command === "other" ? closeOther() : closeAll();
};

// 关闭当前页面的标签页
// store.commit("closeCurrentTag", {
//     $router: router,
//     $route: route
// });

//滚轮滑动操作
let translateX = 0;
const tagsRef = ref();
const tabsUlRef = ref();
const tabsLiRef = ref();
const tabsWheel = (e: WheelEvent) => {
  translateX = translateX - e.deltaY;
  const max = tagsRef.value.offsetWidth - tabsUlRef.value.offsetWidth;
  if (max >= 0) {
    return;
  }

  if (translateX > 0) {
    translateX = 0;
  } else if (translateX < max) {
    translateX = max;
  }
  tabsUlRef.value.style.transform = `translate(${translateX}px, 0px)`;
  e.preventDefault();
};
// 定位到当前tag
const scrollToActiveTag = () => {
  tabsLiRef.value.forEach((item: VueElement) => {
    if (item.className && item.className.indexOf("active") >= 0) {
      if (item.offsetLeft + translateX < 0) {
        translateX = 0 - item.offsetLeft;
        tabsUlRef.value.style.transform = `translate(${translateX}px, 0px)`;
      } else if (item.offsetLeft + item.offsetWidth + translateX - tagsRef.value.offsetWidth > 0) {
        translateX = tagsRef.value.offsetWidth - item.offsetLeft - item.offsetWidth;
        tabsUlRef.value.style.transform = `translate(${translateX}px, 0px)`;
      }
    }
  });
};
</script>

<style lang="less" scoped>
.tags {
  position: relative;
  height: 30px;
  overflow: hidden;
  background: #fff;
  padding-right: 110px;
  box-shadow: 0 5px 10px #ddd;

  .tags-box {
    width: calc(100% - 10px);
    margin: 0 5px;
    height: 100%;
  }

  .tags-close-box {
    position: absolute;
    right: 0;
    top: 0;
    box-sizing: border-box;
    padding-top: 1px;
    text-align: center;
    width: 110px;
    height: 30px;
    background: #fff;
    box-shadow: -3px 0 15px 3px rgba(0, 0, 0, 0.1);
    z-index: 10;

    .el-icon {
      margin-left: 5px;
    }

    button {
      width: 100px;
      height: 27px;
    }
  }
}

.tags-box {
  ul {
    box-sizing: border-box;
    display: inline-flex;

    .tags-li {
      float: left;
      border-radius: 3px;
      margin: 4px 5px 0 0;
      font-size: 12px;
      cursor: pointer;
      height: 23px;
      line-height: 23px;
      border: 1px solid #e9eaec;
      background: #fff;
      padding: 0 5px 0 12px;
      color: #666;
      display: inline-flex;
      justify-content: center;

      .tags-li-title {
        float: left;
        max-width: 80px;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        margin-right: 5px;
        color: #666;
      }

      .tags-li-icon {
        display: flex;
        align-items: center;
      }
    }

    .tags-li:not(.active):hover {
      background: #f8f8f8;
    }

    .tags-li.active {
      color: #fff;
      border: 1px solid #409eff;
      background-color: #409eff;

      .tags-li-title {
        color: #fff;
      }
    }

    .tags-li:last-child {
      margin-right: 0;
    }
  }
}
</style>
