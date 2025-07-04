<template>
  <div>
    <i-frame v-model:src="url"></i-frame>
  </div>
</template>

<script lang="ts" setup name="DruidPage">
import iFrame from "@/components/iFrame/index.vue";

import { ref } from "vue";

const url = ref(import.meta.env.VITE_APP_BASE_API + "/druid/login.html");
</script>
