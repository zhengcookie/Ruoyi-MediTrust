<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" :inline="true" label-width="68px">
      <el-form-item label="请假类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择请假类型" clearable>
          <el-option v-for="dict in dicts.activiti_leave_type" :key="dict.dictValue" :label="dict.dictLabel" :value="dict.dictValue" />
        </el-select>
      </el-form-item>
      <el-form-item label="标题" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入标题" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <!-- <el-form-item label="状态" prop="state">
        <el-select v-model="queryParams.state" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dicts.activiti_flow_type" :key="dict.dictValue" :label="dict.dictLabel" :value="dict.dictValue" />
        </el-select>
      </el-form-item> -->
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button v-hasPermi="['workflow:leave:add']" type="primary" icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-hasPermi="['workflow:leave:export']" type="warning" icon="Download" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar v-model:show-search="showSearch" @query-table="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="leaveList">
      <el-table-column label="请假类型" align="center" prop="type">
        <template #default="scope">
          <dict-tag :options="dicts.activiti_leave_type" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column label="标题" align="center" prop="title" />
      <el-table-column label="原因" align="center" prop="reason" />
      <el-table-column label="开始时间" align="center" prop="leaveStartTime" width="180">
        <template #default="scope">
          {{ dayjs(scope.row.leaveStartTime).format("YYYY-MM-DD") }}
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="leaveEndTime" width="180">
        <template #default="scope">
          {{ dayjs(scope.row.leaveEndTime).format("YYYY-MM-DD") }}
        </template>
      </el-table-column>
      <!-- <el-table-column label="状态" align="center">
        <template #default="scope">
          <dict-tag :options="dicts.activiti_flow_type" :value="scope.row.jobGroup" />
        </template>
      </el-table-column> -->
    </el-table>

    <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />

    <el-dialog v-model="open" :title="title" width="500px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="测试流程" prop="processType">
          <el-select v-model="form.processType" placeholder="请选择测试流程" clearable>
            <el-option v-for="dict in dicts.activiti_task_type" :key="dict.dictValue" :label="dict.dictLabel" :value="dict.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item label="请假类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择请假类型" @change="chooseMedicine">
            <el-option v-for="dict in dicts.activiti_leave_type" :key="dict.dictValue" :label="dict.dictLabel" :value="dict.dictValue"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="原因" prop="reason">
          <el-input v-model="form.reason" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="开始时间" prop="leaveStartTime">
          <el-date-picker v-model="form.leaveStartTime" clearable style="width: 200px" type="date" value-format="YYYY-MM-DD" placeholder="选择开始时间"></el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="leaveEndTime">
          <el-date-picker v-model="form.leaveEndTime" clearable style="width: 200px" type="date" value-format="YYYY-MM-DD" placeholder="选择结束时间"></el-date-picker>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup name="LeavePage">
import { listLeave, addLeave, updateLeave, type LeaveObj } from "@/api/workflow/leave";
import { loadDicts } from "@/utils/dict";
import { ref } from "vue";
import { ElMessage, dayjs } from "element-plus";
import userStore from "@/stores/user";
import server from "@/utils/request";
import type { ElForm } from "@/api/form";

//store对象 ################################################
const uStore = userStore();

//ref对象 ################################################
//字典数据
const dicts = loadDicts(["activiti_leave_type", "activiti_task_type"]);
const createName = ref("");
// 遮罩层
const loading = ref(true);
// 显示搜索条件
const showSearch = ref(true);
// 总条数
const total = ref(0);
// 请假表格数据
const leaveList = ref<LeaveObj[]>([]);
// 弹出层标题
const title = ref("");
// 是否显示弹出层
const open = ref(false);
// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  id: "",
  type: undefined,
  title: undefined,
  reason: undefined,
  leaveStartTime: undefined,
  leaveEndTime: undefined,
  instanceId: undefined,
  state: undefined,
  createBy: undefined,
});
// 表单参数
const form = ref<LeaveObj>({
  id: "",
  type: undefined,
  title: undefined,
  reason: undefined,
  leaveStartTime: undefined,
  leaveEndTime: undefined,
  instanceId: undefined,
  state: undefined,
});
// 表单校验
const rules = ref({
  processType: [{ required: true, message: "测试流程不能为空", trigger: "change" }],
  type: [{ required: true, message: "请假类型不能为空", trigger: "change" }],
  title: [{ required: true, message: "标题不能为空", trigger: "blur" }],
  reason: [{ required: true, message: "原因不能为空", trigger: "blur" }],
  leaveStartTime: [{ required: true, message: "开始时间不能为空", trigger: "blur" }],
  leaveEndTime: [{ required: true, message: "结束时间不能为空", trigger: "blur" }],
});

//elment ################################################
const queryFormRef = ref<ElForm>();
const formRef = ref<ElForm>();

// Function ################################################
/** 查询请假列表 */
const getList = () => {
  loading.value = true;
  listLeave(queryParams.value).then((response) => {
    leaveList.value = response.rows || [];
    total.value = response.total || 0;
    loading.value = false;
  });
};
// 取消按钮
const cancel = () => {
  open.value = false;
  reset();
};
// 表单重置
const reset = () => {
  form.value = {
    id: "",
    processType: undefined,
    type: undefined,
    title: undefined,
    reason: undefined,
    leaveStartTime: undefined,
    leaveEndTime: undefined,
    instanceId: undefined,
    state: undefined,
    createBy: undefined,
    createTime: undefined,
    updateTime: undefined,
  };
  formRef.value?.resetFields();
};
/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};
/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
};
/** 新增按钮操作 */
const handleAdd = () => {
  createName.value = uStore.nickName || "";
  reset();
  open.value = true;
  title.value = "添加请假";
};

/** 提交按钮 */
const submitForm = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      // 随机测试，实际根据自己的业务来
      if (form.value.processType == "leave2") {
        form.value.department = new Date().getTime() % 2 == 0 ? "A1" : "A2";
      }
      if (form.value.id) {
        updateLeave(form.value).then(() => {
          ElMessage.success("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addLeave(form.value).then(() => {
          ElMessage.success("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
};
/** 导出按钮操作 */
const handleExport = () => {
  server.download(
    "/workflow/leave/export",
    {
      ...queryParams.value,
    },
    `Leave_${new Date().getTime()}.xlsx`
  );
};
const chooseMedicine = () => {
  form.value.title = createName.value + "的" + dicts.value.activiti_leave_type?.find((item) => item.dictValue == form.value.type)?.dictLabel + "申请";
};

// 初始化 ################################################
getList();
</script>
