<template>
  <el-dialog v-model="visible" :title="title" width="600px" append-to-body>
    <!--审批历史表格-->
    <div v-loading="loading">
      <leave-info v-if="definitionKey.indexOf('leave') === 0" :business-key="busiId"></leave-info>
      <el-divider />
      <el-form ref="formRef" :model="form" :rule="rule" label-width="100px">
        <el-form-item label="审批" prop="pass">
          <el-radio-group v-model="form.pass" class="ml-4">
            <el-radio label="1">同意</el-radio>
            <el-radio label="2">不同意</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="意见" prop="comment">
          <el-input v-model="form.comment" type="textarea" :max-length="50" />
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button :loading="loading" type="primary" @click="handleSubmit">确认</el-button>
        <el-button @click="visible = false">关闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup name="CompTaskDetail">
import { nextTick, watch } from "vue";
import { computed, ref } from "vue";
import LeaveInfo from "../../leave/leaveInfo.vue";
import { complete } from "@/api/workflow/activiti/task";
import { ElMessage } from "element-plus";
import type { ElForm } from "@/api/form";

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
const loading = ref(false);
const title = ref("");
const form = ref<{
  pass: string;
  comment?: string;
}>({
  pass: "1",
  comment: "",
});
const rule = ref({
  pass: [{ required: true, message: "请选择审批", trigger: "change" }],
});
const busiId = ref("");

//element ################################################
const formRef = ref<ElForm>();

//emit ################################################
const emit = defineEmits<{ (event: "update:modelValue", value: boolean): void; (event: "ok"): void }>();
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

//方法 ################################################
/**
 * 审批提交
 */
const handleSubmit = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      loading.value = true;
      complete({ ...form.value, taskId: props.taskId })
        .then(() => {
          ElMessage.success("处理成功！");
          visible.value = false;
          emit("ok");
        })
        .finally(() => {
          loading.value = false;
        });
    }
  });
};
</script>

<style scoped></style>
