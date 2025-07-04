<template>
  <div v-loading="loading" style="width: 100%; height: 100%" element-loading-text="正在加载页面，请稍候！">
    <iframe ref="iframeRef" style="width: 100%; height: 100%" frameborder="no"></iframe>
  </div>
</template>

<script lang="ts" setup name="InnerLinkLayout">
import { ref, onMounted, nextTick } from "vue";
import router from "@/router";

const route = router.currentRoute.value;

// const props = withDefaults(
//   defineProps<{
//     src?: string;
//     iframeId: string;
//   }>(),
//   {
//     src: "/",
//   }
// );

const iframeRef = ref<HTMLIFrameElement>();
const loading = ref(false);

onMounted(() => {
  nextTick(() => {
    if (iframeRef.value && route.meta.link) {
      if (iframeRef.value.addEventListener instanceof Function) {
        loading.value = true;
        iframeRef.value.addEventListener("load", function () {
          loading.value = false;
        });
      } else {
        loading.value = true;
        iframeRef.value.onload = function () {
          loading.value = false;
        };
      }
      iframeRef.value.src = route.meta.link;
    }
  });
});
</script>
