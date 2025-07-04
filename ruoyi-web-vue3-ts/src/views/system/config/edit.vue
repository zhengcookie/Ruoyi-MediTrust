<template>
  <!-- 添加或修改参数配置对话框 -->
  <el-dialog v-model="open" :title="title" width="500px" append-to-body>
    <el-form ref="configRef" v-loading="loading" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="参数名称" prop="configName">
        <el-input v-model="form.configName" placeholder="请输入参数名称" />
      </el-form-item>
      <el-form-item label="参数键名" prop="configKey">
        <el-input v-model="form.configKey" placeholder="请输入参数键名" />
      </el-form-item>
      <el-form-item label="参数键值" prop="configValue">
        <el-input v-model="form.configValue" placeholder="请输入参数键值" />
      </el-form-item>
      <el-form-item label="系统内置" prop="configType">
        <el-radio-group v-model="form.configType">
          <el-radio v-for="dict in dicts.sys_yes_no" :key="dict.value" :label="dict.value">{{ dict.label }}</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup name="SysConfig">
import type { ElForm } from "@/api/form";
import { getConfig, addConfig, updateConfig, type SysConfigObj } from "@/api/system/config";
import { loadDicts } from "@/utils/dict";
import { ElMessage } from "element-plus";
import { ref } from "vue";

const dicts = loadDicts(["sys_yes_no"]);

const open = ref(false);
const loading = ref(true);
const title = ref("");
const action = ref("add");

const configRef = ref<ElForm>();

const form = ref<SysConfigObj>({});
const rules = ref({
  configName: [{ required: true, message: "参数名称不能为空", trigger: "blur" }],
  configKey: [{ required: true, message: "参数键名不能为空", trigger: "blur" }],
  configValue: [{ required: true, message: "参数键值不能为空", trigger: "blur" }],
});

const emit = defineEmits<{ (event: "dataChange"): void }>();

/** 打开页面 */
function show(param: { action: "add" | "edit"; configId?: string }) {
  open.value = true;
  reset();
  if (param.action == "edit" && param.configId) {
    action.value = "edit";
    title.value = "修改参数";
    loading.value = true;
    getConfig(param.configId)
      .then((response) => {
        response.data && (form.value = response.data);
      })
      .finally(() => {
        loading.value = false;
      });
  } else {
    action.value = "add";
    title.value = "添加参数";
    loading.value = false;
  }
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}
/** 表单重置 */
function reset() {
  form.value = {
    configId: undefined,
    configName: undefined,
    configKey: undefined,
    configValue: undefined,
    configType: "Y",
    remark: undefined,
  };
  configRef.value?.resetFields();
}

/** 提交按钮 */
function submitForm() {
  configRef.value?.validate((valid) => {
    if (valid) {
      const todo = action.value == "edit" ? updateConfig(form.value) : addConfig(form.value);
      loading.value = true;
      todo
        .then((resp) => {
          if (resp.code === 200) {
            ElMessage.success(action.value == "edit" ? "修改成功" : "新增成功");
            open.value = false;
            emit("dataChange");
          } else {
            ElMessage.error(resp.msg);
          }
        })
        .finally(() => {
          loading.value = false;
        });
    }
  });
}

defineExpose({ show });
</script>
