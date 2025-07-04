<template>
  <div class="top-right-btn" :style="style">
    <el-row>
      <el-tooltip v-if="search" class="item" effect="dark" :content="showSearch ? '隐藏搜索' : '显示搜索'" placement="top">
        <el-button circle icon="Search" @click="toggleSearch()" />
      </el-tooltip>
      <el-tooltip class="item" effect="dark" content="刷新" placement="top">
        <el-button circle icon="Refresh" @click="refresh()" />
      </el-tooltip>
      <el-tooltip v-if="columns" class="item" effect="dark" content="显隐列" placement="top">
        <el-button v-if="showColumnsType == 'transfer'" circle icon="Menu" @click="showColumn()" />
        <el-dropdown v-if="showColumnsType == 'checkbox'" trigger="click" :hide-on-click="false" style="padding-left: 12px">
          <el-button circle icon="Menu" />
          <template #dropdown>
            <el-dropdown-menu>
              <template v-for="item in columns" :key="item.key">
                <el-dropdown-item>
                  <el-checkbox :checked="item.visible" :label="item.label" />
                </el-dropdown-item>
              </template>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-tooltip>
    </el-row>
    <el-dialog v-model="open" :title="title" append-to-body>
      <el-transfer v-model="value" :titles="['显示', '隐藏']" :data="columns" @change="dataChange"></el-transfer>
    </el-dialog>
  </div>
</template>
<script lang="ts" setup name="RightToolbarCmpt">
import type { TransferKey } from "element-plus";
import { computed, ref } from "vue";
type transferObj = {
  key: number;
  label: string;
  visible: boolean;
};

// 显隐数据
const value = ref<number[]>([]);
// 弹出层标题
const title = ref("显示/隐藏");
// 是否显示弹出层
const open = ref(false);
const props = withDefaults(
  defineProps<{
    /* 是否显示检索条件 */
    showSearch?: boolean;
    /* 显隐列信息 */
    columns?: transferObj[];
    /* 是否显示检索图标 */
    search?: boolean;
    /* 显隐列类型（transfer穿梭框、checkbox复选框） */
    showColumnsType?: string;
    /* 右外边距 */
    gutter?: number;
  }>(),
  {
    columns: () => {
      return [];
    },
    showSearch: true,
    search: true,
    showColumnsType: "checkbox",
    gutter: 10,
  }
);

const style = computed(() => {
  if (props.gutter) {
    return {
      marginRight: `${props.gutter / 2}px`,
    };
  }
  return {};
});

const emit = defineEmits<{ (event: "update:showSearch", val: boolean): void; (event: "queryTable"): void }>();

// 搜索
function toggleSearch() {
  emit("update:showSearch", !props.showSearch);
}

// 刷新
function refresh() {
  emit("queryTable");
}

// 右侧列表元素变化
function dataChange(data: TransferKey[]) {
  props.columns?.forEach((item) => {
    const key = item.key;
    item.visible = !data.includes(key);
  });
}

// 打开显隐列dialog
function showColumn() {
  open.value = true;
}

if (props.showColumnsType == "transfer") {
  // 显隐列初始默认隐藏列
  props.columns?.forEach((item, i) => {
    if (item.visible === false) {
      value.value.push(i);
    }
  });
}

// 勾选
// function checkboxChange(event, label) {
//   props.columns?.filter(item => item.label == label)[0].visible = event;
// }
</script>
<style lang="less" scoped>
.item:first-child {
  :deep(.el-button:first-child) {
    margin-bottom: 10px;
  }
}
</style>
