<template>
  <!-- 添加或修改岗位对话框 -->
  <el-dialog v-model="open" :title="title" width="500px" append-to-body>
    <el-form ref="postRef" v-loading="loading" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="岗位名称" prop="postName">
        <el-input v-model="form.postName" placeholder="请输入岗位名称" />
      </el-form-item>
      <el-form-item label="岗位编码" prop="postCode">
        <el-input v-model="form.postCode" placeholder="请输入编码名称" />
      </el-form-item>
      <el-form-item label="岗位顺序" prop="postSort">
        <el-input-number v-model="form.postSort" controls-position="right" :min="0" />
      </el-form-item>
      <el-form-item label="岗位状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio v-for="dict in dicts.sys_normal_disable" :key="dict.value" :label="dict.value">{{ dict.label }}</el-radio>
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

<script lang="ts" setup name="SysPostEditPage">
import { addPost, getPost, updatePost, type PostObj } from "@/api/system/post";
import { loadDicts } from "@/utils/dict";
import { ElMessage } from "element-plus";
import { ref } from "vue";
import type { ElForm } from "@/api/form";

const dicts = loadDicts(["sys_normal_disable"]);

const action = ref("add");
const open = ref(false);
const loading = ref(true);
const title = ref("");
const postRef = ref<ElForm>();

const form = ref<PostObj>({
  postId: "",
});

const rules = ref({
  postName: [{ required: true, message: "岗位名称不能为空", trigger: "blur" }],
  postCode: [{ required: true, message: "岗位编码不能为空", trigger: "blur" }],
  postSort: [{ required: true, message: "岗位顺序不能为空", trigger: "blur" }],
});

const emit = defineEmits<{ (event: "dataChange"): void }>();

/** 打开页面 */
function show(param: { action: "add" | "edit"; postId?: string }) {
  open.value = true;
  reset();
  if (param.action == "edit" && param.postId) {
    action.value = "edit";
    title.value = "修改岗位";
    loading.value = true;
    getPost(param.postId)
      .then((response) => {
        response.data && (form.value = response.data);
      })
      .finally(() => {
        loading.value = false;
      });
  } else {
    action.value = "add";
    title.value = "添加岗位";
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
    postId: "",
    postCode: undefined,
    postName: undefined,
    postSort: 0,
    status: "0",
    remark: undefined,
  };
  postRef.value?.resetFields();
}

/** 提交按钮 */
function submitForm() {
  postRef.value?.validate((valid) => {
    if (valid) {
      const todo = action.value == "edit" ? updatePost(form.value) : addPost(form.value);
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
