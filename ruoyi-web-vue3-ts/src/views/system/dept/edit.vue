<template>
  <!-- 添加或修改部门对话框 -->
  <el-dialog v-model="open" :title="title" width="600px" append-to-body>
    <el-form ref="deptRef" :model="form" :rules="rules" label-width="80px">
      <el-row>
        <el-col v-if="form.parentId != '0'" :span="24">
          <el-form-item label="上级部门" prop="parentId">
            <el-tree-select
              v-model="form.parentId"
              :data="deptOptions"
              :props="{
                value: 'deptId',
                label: 'deptName',
                children: 'children',
              }"
              value-key="deptId"
              placeholder="选择上级部门"
              check-strictly
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="部门名称" prop="deptName">
            <el-input v-model="form.deptName" placeholder="请输入部门名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="显示排序" prop="orderNum">
            <el-input-number v-model="form.orderNum" controls-position="right" :min="0" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="负责人" prop="leader">
            <el-input v-model="form.leader" placeholder="请输入负责人" maxlength="20" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="11" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="部门状态">
            <el-radio-group v-model="form.status">
              <el-radio v-for="dict in dicts.sys_normal_disable" :key="dict.value" :label="dict.value">{{ dict.label }}</el-radio>
            </el-radio-group>
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

<script lang="ts" setup name="DeptEditDialog">
import { listDept, getDept, addDept, updateDept, listDeptExcludeChild, type DeptObj } from "@/api/system/dept";
import { loadDicts } from "@/utils/dict";
import { ref } from "vue";
import { handleTree } from "@/utils/ruoyi";
import { ElMessage } from "element-plus";
import type { ElForm } from "@/api/form";
const dicts = loadDicts(["sys_normal_disable"]);

const open = ref(false);
const loading = ref(true);
const title = ref("");
const deptOptions = ref<DeptObj[]>([]);
const action = ref("add");
const deptRef = ref<ElForm>();

const form = ref<DeptObj>({
  deptId: "",
});
const rules = ref({
  parentId: [{ required: true, message: "上级部门不能为空", trigger: "blur" }],
  deptName: [{ required: true, message: "部门名称不能为空", trigger: "blur" }],
  orderNum: [{ required: true, message: "显示排序不能为空", trigger: "blur" }],
  email: [
    {
      type: "email",
      message: "请输入正确的邮箱地址",
      trigger: ["blur", "change"],
    },
  ],
  phone: [
    {
      pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
      message: "请输入正确的手机号码",
      trigger: "blur",
    },
  ],
});

const emit = defineEmits<{ (event: "dataChange"): void }>();

/** 打开页面 */
function show(param: { action: "add" | "edit"; deptId?: string }) {
  open.value = true;
  reset();

  let todo = listDept();
  if (param.action == "edit" && param.deptId) {
    todo = listDeptExcludeChild(param.deptId);
    action.value = "edit";
    title.value = "修改部门";
  } else {
    action.value = "add";
    title.value = "添加部门";
    param.deptId && (form.value.parentId = param.deptId);
  }

  loading.value = true;
  todo
    .then((resp) => {
      resp.data && (deptOptions.value = handleTree(resp.data, "deptId"));
      return action.value == "edit" && param.deptId ? getDept(param.deptId) : null;
    })
    .then((resp) => {
      resp && resp.data && (form.value = resp.data);
    })
    .finally(() => {
      loading.value = false;
    });
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}
/** 表单重置 */
function reset() {
  form.value = {
    deptId: "",
    parentId: undefined,
    deptName: undefined,
    orderNum: 0,
    leader: undefined,
    phone: undefined,
    email: undefined,
    status: "0",
  };
  deptRef.value?.resetFields();
}

/** 提交按钮 */
function submitForm() {
  deptRef.value?.validate((valid) => {
    if (valid) {
      const todo = action.value == "edit" ? updateDept(form.value) : addDept(form.value);
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
