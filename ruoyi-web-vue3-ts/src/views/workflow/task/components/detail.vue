<template>
  <el-dialog v-model="visible" :title="title" width="600px" append-to-body>
    <!--审批历史表格-->
    <div>
      <leave-info v-if="definitionKey.indexOf('leave') === 0" :business-key="busiId"></leave-info>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">关闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup name="CompTaskDetail">
import { nextTick, watch } from "vue";
import { computed, ref } from "vue";
import LeaveInfo from "../../leave/leaveInfo.vue";

//外部参数 ################################################
const props = withDefaults(
  defineProps<{
    modelValue: boolean; // 页面展示
    taskId: string;
    taskContent: string;
    definitionKey: string;
    businessKey: string;
  }>(),
  {}
);

//ref对象 ################################################
const title = ref("");
const busiId = ref("");

//emit ################################################
const emit = defineEmits<{ (event: "update:modelValue", value: boolean): void }>();
//computed ################################################
const visible = computed({
  get() {
    return props.modelValue;
  },
  set(value) {
    emit("update:modelValue", value);
  },
});

//watch ################################################
watch(
  () => props.taskContent,
  (value) => {
    title.value = value;
  }
);
watch(
  () => props.businessKey,
  () => {
    nextTick(() => {
      const ids = props.businessKey.split(":");
      busiId.value = ids[1] ? ids[1] : props.businessKey;
    });
  }
);
</script>

<style scoped></style>
