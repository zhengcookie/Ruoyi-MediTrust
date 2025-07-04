<template>
  <div :class="{ hidden: hidden }" class="pagination-container">
    <el-config-provider :locale="zhCn">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :background="background"
        :layout="layout"
        :page-sizes="pageSizes"
        :pager-count="pagerCount"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-config-provider>
  </div>
</template>

<script setup lang="ts" name="PaginationCmpt">
import { scrollTo } from "@/utils/scroll-to";
import { computed } from "vue";
import { zhCn } from "element-plus/es/locales";

const props = withDefaults(
  defineProps<{
    total: number;
    page?: number;
    limit?: number;
    pageSizes?: number[];
    pagerCount?: number;
    layout?: string;
    background?: boolean;
    autoScroll?: boolean;
    hidden?: boolean;
  }>(),
  {
    page: 1,
    limit: 10,
    pageSizes: () => [10, 20, 30, 50],
    pagerCount: document.body.clientWidth < 992 ? 5 : 7, // 移动端页码按钮的数量端默认值5
    layout: "total, sizes, prev, pager, next, jumper",
    background: true,
    autoScroll: true,
    hidden: false,
  }
);

const emit = defineEmits<{
  (event: "update:page", val: number): void;
  (event: "update:limit", val: number): void;
  (event: "pagination", params: { page: number; limit: number }): void;
}>();

const currentPage = computed({
  get() {
    return props.page;
  },
  set(val) {
    emit("update:page", val);
  },
});
const pageSize = computed({
  get() {
    return props.limit;
  },
  set(val) {
    emit("update:limit", val);
  },
});
function handleSizeChange(val: number) {
  if (currentPage.value * val > props.total) {
    currentPage.value = 1;
  }
  emit("pagination", { page: currentPage.value, limit: val });
  if (props.autoScroll) {
    scrollTo(0, 800);
  }
}
function handleCurrentChange(val: number) {
  emit("pagination", { page: val, limit: pageSize.value });
  if (props.autoScroll) {
    scrollTo(0, 800);
  }
}
</script>

<style scoped>
.pagination-container {
  background: #fff;
  padding: 32px 16px;
}

.pagination-container.hidden {
  display: none;
}
</style>
