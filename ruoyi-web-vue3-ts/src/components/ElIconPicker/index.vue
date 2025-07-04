<template>
  <div class="el-icon-picker_con">
    <el-popover v-model:visible="showPopv" :width="450" placement="bottom-start">
      <template #reference>
        <el-button :icon="modelValue" class="icon_picker_btn" @click="showPopover">{{ modelValue || "请选择图标" }}</el-button>
      </template>
      <div class="el-icon-picker">
        <el-input v-model="filterVal" class="filter_input" placeholder="输入图标查询" :suffix-icon="Search" @input="inputFilter" />

        <el-tabs v-model="activeName" type="card">
          <el-tab-pane label="el-icon" name="el">
            <div class="icon_con">
              <el-icon v-for="icon in elIconList" :key="icon" class="icon" :title="icon" :class="{ icon, 'icon-active': icon == modelValue }" @click="chooseIcon(icon)">
                <component :is="icon"></component>
              </el-icon>
            </div>
          </el-tab-pane>
          <el-tab-pane label="my-icon" name="my">
            <div class="icon_con">
              <el-icon v-for="icon in myIconList" :key="icon" class="icon" :title="icon" :class="{ icon, 'icon-active': icon == modelValue }" @click="chooseIcon(icon)">
                <component :is="icon"></component>
              </el-icon>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-popover>
    <el-icon v-if="modelValue" class="delete_icon" size="16" title="清除" @click="clearPicker">
      <Close />
    </el-icon>
  </div>
</template>

<script lang="ts" setup name="ComElIconPicker">
import { ref, watch } from "vue";
import { Search } from "@element-plus/icons-vue";
import * as ELIconComps from "@element-plus/icons-vue";
import { myIconNames } from "../SvgIcon/myIcon";

// 获取所有elment Icon name
const elIcons: string[] = [];
for (const [key] of Object.entries(ELIconComps)) {
  elIcons.push(key);
}
// 获取所有自定义 Icon name
const myIcons = myIconNames;

const activeName = ref("el");

const elIconList = ref<string[]>([]);
const myIconList = ref<string[]>([]);
const filterVal = ref("");
const showPopv = ref(false);

const emit = defineEmits<{ (event: "update:modelValue", value: string): void }>();

withDefaults(
  defineProps<{
    modelValue?: string;
  }>(),
  {
    modelValue: "",
  }
);

watch(
  () => showPopv.value,
  (newVal) => {
    if (newVal) {
      filterVal.value = "";
      inputFilter();
    }
  }
);

const showPopover = () => {
  filterVal.value = "";
  showPopv.value = true;
};

const chooseIcon = (icon: string) => {
  emit("update:modelValue", icon);
  showPopv.value = false;
};

// 图标过滤查询
const inputFilter = (val?: string) => {
  if (val) {
    elIconList.value = elIcons.filter((item) => item.toLowerCase().indexOf(val.toLowerCase()) !== -1);
    myIconList.value = myIcons.filter((item) => item.toLowerCase().indexOf(val.toLowerCase()) !== -1);
  } else {
    elIconList.value = [];
    elIconList.value = elIconList.value.concat(elIcons);
    myIconList.value = [];
    myIconList.value = myIconList.value.concat(myIcons);
  }
};

// 清除选中
const clearPicker = () => {
  emit("update:modelValue", "");
};
</script>

<style scoped lang="less">
.el-icon-picker {
  height: 320px;
  position: relative;

  .icon_picker_btn {
    float: left;
  }

  .filter_input {
    position: absolute;
    right: 0;
    top: 0;
    width: 170px;
    padding: 0 12px;
    box-sizing: border-box;
    z-index: 100;
  }

  .icon_con {
    height: 260px;
    overflow-y: auto;

    .icon {
      cursor: pointer;
      text-align: center;
      font-size: 20px;
      line-height: 45px;
      margin: 10px;
    }

    .icon:hover {
      color: var(--el-color-primary);
    }

    .icon-active {
      color: var(--el-color-primary);
    }
  }
}

.el-icon-picker_con {
  .delete_icon {
    float: right;
    margin-left: 8px;
    height: 32px;
    cursor: pointer;
    visibility: hidden;
  }

  .delete_icon:hover {
    color: red;
  }
}

.el-icon-picker_con:hover {
  .delete_icon {
    visibility: visible;
  }
}
</style>
