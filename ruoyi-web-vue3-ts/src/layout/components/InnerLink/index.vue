<template>
  <div v-loading="loading" :style="'height:' + height" element-loading-text="正在加载页面，请稍候！">
    <iframe :id="iframeId" style="width: 100%; height: 100%" :src="src" frameborder="no"></iframe>
  </div>
</template>

<script lang="ts" setup name="InnerLinkLayout">
import { ref, onMounted } from "vue";

const props = withDefaults(
  defineProps<{
    src?: string;
    iframeId: string;
  }>(),
  {
    src: "/",
  }
);

const loading = ref(false);
const height = ref(document.documentElement.clientHeight - 94.5 + "px;");

onMounted(() => {
  const iframeId = ("#" + props.iframeId).replace(/\//g, "\\/");
  const iframe = document.querySelector(iframeId) as HTMLIFrameElement;
  // iframe页面loading控制 iframe.attachEvent
  if (iframe.addEventListener instanceof Function) {
    loading.value = true;
    iframe.addEventListener("onload", function () {
      loading.value = false;
    });
  } else {
    loading.value = true;
    iframe.onload = function () {
      loading.value = false;
    };
  }
});
</script>
