<template>
  <div>loading...</div>
</template>
<script lang="ts" setup name="RedirectPage">
import { useRoute, useRouter } from "vue-router";
// import { onBeforeMount ,render} from "vue"

const router = useRouter();
const route = useRoute();
const { params, query } = route;
const { path } = params;
router.replace({ path: "/" + path, query });
</script>
