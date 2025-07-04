<template>
  <div class="about">
    <v-header />
    <v-sidebar />
    <div class="content-box" :class="{ 'content-collapse': pStore.sidebarIsCollapsed }">
      <v-tags></v-tags>
      <div class="content">
        <breadcrumb></breadcrumb>
        <router-view v-slot="{ Component }">
          <transition v-if="$route.meta.noCache" name="move" mode="out-in">
            <component :is="Component" />
          </transition>
          <transition v-if="!$route.meta.noCache" name="move" mode="out-in">
            <keep-alive>
              <component :is="Component" />
            </keep-alive>
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>
<script lang="ts" name="IndexLayout" setup>
import permissionStore from "@/stores/permission";
import breadcrumb from "@/components/Breadcrumb/index.vue";
import vHeader from "./components/Header/index.vue";
import vSidebar from "./components/Sidebar/index.vue";
import vTags from "./components/TagsView/index.vue";

const pStore = permissionStore();
</script>
<style lang="less" scoped>
.content-box {
  height: calc(100vh - 70px);
}
</style>
