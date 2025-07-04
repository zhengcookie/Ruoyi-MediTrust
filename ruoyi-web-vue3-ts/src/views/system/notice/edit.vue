<template>
  <!-- 添加或修改公告对话框 -->
  <el-dialog v-model="open" :title="title" width="1200px" append-to-body>
    <el-form ref="noticeRef" v-loading="loading" :model="form" :rules="rules" label-width="80px">
      <el-row>
        <el-col :span="12">
          <el-form-item label="公告标题" prop="noticeTitle">
            <el-input v-model="form.noticeTitle" placeholder="请输入公告标题" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="公告类型" prop="noticeType">
            <el-select v-model="form.noticeType" placeholder="请选择">
              <el-option v-for="dict in dicts.sys_notice_type" :key="dict.value" :label="dict.label" :value="dict.value"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="状态">
            <el-radio-group v-model="form.status">
              <el-radio v-for="dict in dicts.sys_notice_status" :key="dict.value" :label="dict.value">{{ dict.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="内容">
            <div>
              <Toolbar style="border-bottom: 1px solid #ccc" :editor="editorRef" :default-config="toolbarConfig" mode="default" />
              <Editor v-model="form.noticeContent" style="height: 300px; overflow-y: hidden" :default-config="editorConfig" mode="default" @on-created="handleCreated" />
            </div>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup name="SysNoticeEditDialog">
import type { ElForm } from "@/api/form";
import { getNotice, addNotice, updateNotice, type NoticeObj } from "@/api/system/notice";
import { loadDicts } from "@/utils/dict";
import { ElMessage } from "element-plus";
import { onBeforeUnmount, ref } from "vue";
import "@wangeditor/editor/dist/css/style.css"; // 引入 css
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";
import type { IDomEditor } from "@wangeditor/editor";

const dicts = loadDicts(["sys_notice_status", "sys_notice_type"]);

const action = ref("add");
const open = ref(false);
const loading = ref(true);
const title = ref("");
const noticeRef = ref<ElForm>();
const editorRef = ref<IDomEditor>();

const form = ref<NoticeObj>({
  noticeId: "",
});
const rules = ref({
  noticeTitle: [{ required: true, message: "公告标题不能为空", trigger: "blur" }],
  noticeType: [{ required: true, message: "公告类型不能为空", trigger: "change" }],
});

const toolbarConfig = {};
const editorConfig = { placeholder: "请输入内容..." };

const handleCreated = (editor: IDomEditor) => {
  editorRef.value = editor; // 记录 editor 实例，重要！
};

const emit = defineEmits<{ (event: "dataChange"): void }>();

/** 打开页面 */
function show(param: { action: "add" | "edit"; noticeId?: string }) {
  open.value = true;
  reset();
  if (param.action == "edit" && param.noticeId) {
    action.value = "edit";
    title.value = "修改公告";
    loading.value = true;
    getNotice(param.noticeId)
      .then((response) => {
        response.data && (form.value = response.data);
        open.value = true;
      })
      .finally(() => {
        loading.value = false;
      });
  } else {
    loading.value = false;
    action.value = "add";
    title.value = "添加公告";
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
    noticeId: "",
    noticeTitle: undefined,
    noticeType: undefined,
    noticeContent: undefined,
    status: "0",
  };
  noticeRef.value?.resetFields();
}
/** 提交按钮 */
function submitForm() {
  noticeRef.value?.validate((valid) => {
    if (valid) {
      const todo = action.value == "edit" ? updateNotice(form.value) : addNotice(form.value);
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

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  editorRef.value?.destroy();
});

defineExpose({ show });
</script>
