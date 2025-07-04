<template>
  <div>
    <template v-for="(item, index) in options">
      <template v-if="values.includes(item.value || '')">
        <span
          v-if="(item.elTagType == 'default' || item.elTagType == '') && (item.elTagClass == '' || item.elTagClass == null)"
          :key="item.value"
          :index="index"
          :class="item.elTagClass"
        >
          {{ item.label + " " }}
        </span>
        <el-tag v-else :key="item.value + ''" :disable-transitions="true" :index="index" :type="item.elTagType === 'primary' ? '' : item.elTagType" :class="item.elTagClass">
          {{ item.label + " " }}
        </el-tag>
      </template>
    </template>
    <template v-if="unmatch && showValue">
      {{ handleArray(unmatchArray) }}
    </template>
  </div>
</template>

<script setup lang="ts" name="DictTagCmpt">
import { computed } from "vue";
import type { DictObj } from "@/api/system/dict/data";

const props = withDefaults(
  defineProps<{
    options?: DictObj[]; // 数据
    value: number | string | string[];
    showValue?: boolean;
    separator?: string;
  }>(),
  {
    options: () => {
      return [];
    },
    showValue: true,
    separator: ",",
  }
);

const values = computed(() => {
  if (props.value === null || typeof props.value === "undefined" || props.value === "") return [];
  return Array.isArray(props.value) ? props.value.map((item) => "" + item) : String(props.value).split(props.separator);
});

const unmatch = computed(() => {
  // 没有value不显示
  if (props.value === null || typeof props.value === "undefined" || props.value === "" || props.options?.length === 0) {
    return false;
  }
  // 传入值为数组
  let unmatch = false; // 添加一个标志来判断是否有未匹配项
  values.value.forEach((item) => {
    if (!props.options?.some((v) => v.value === item)) {
      unmatch = true; // 如果有未匹配项，将标志设置为true
    }
  });
  return unmatch; // 返回标志的值
});
// 记录未匹配的项
const unmatchArray = computed(() => {
  const unList: string[] = [];
  // 没有value不显示
  if (props.value === null || typeof props.value === "undefined" || props.value === "" || props.options?.length === 0) {
    return unList;
  }
  // 传入值为数组
  values.value.forEach((item) => {
    if (!props.options?.some((v) => v.value === item)) {
      unList.push(item);
    }
  });
  return unList; // 返回标志的值
});

function handleArray(array: string[]) {
  if (array.length === 0) return "";
  return array.reduce((pre, cur) => {
    return pre + " " + cur;
  });
}
</script>

<style scoped>
.el-tag + .el-tag {
  margin-left: 10px;
}
</style>
