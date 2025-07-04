<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryFormRef" :model="queryParams" :inline="true" label-width="80px">
      <el-form-item label="任务类别" prop="definitionKey">
        <el-select v-model="queryParams.definitionKey" placeholder="请选择任务类别" clearable>
          <el-option v-for="dict in dicts.activiti_task_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="任务内容" prop="taskContent">
        <el-input v-model="queryParams.taskContent" placeholder="请输入任务内容" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar v-model:show-search="showSearch" @query-table="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="taskList">
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <el-table-column label="任务ID" align="center" prop="taskId" width="150" />
      <el-table-column label="任务类别" align="center" prop="definitionKey" width="150">
        <template #default="scope">
          <dict-tag :options="dicts.activiti_task_type" :value="scope.row.definitionKey" />
        </template>
      </el-table-column>
      <el-table-column label="任务内容" align="center" prop="taskContent" width="150" />
      <el-table-column label="流程实例ID" align="center" prop="instanceId" width="150" />
      <el-table-column label="提交人" align="center" prop="createUserName" width="150" />
      <el-table-column label="提交时间" align="center" prop="startTime" width="180" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button type="primary" link icon="Check" @click="handleTodo(scope.row)">办理</el-button>
          <!-- <el-button link icon="User" @click="handleViewUserList" >转办</el-button> -->
          <el-button type="primary" link icon="Time" @click="handleShowHistory(scope.row)">审批历史</el-button>
          <el-button type="primary" link icon="Search" @click="handleProcessView(scope.row)">进度查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />

    <task-complete
      v-model="completeDialog.visible"
      :task-id="completeDialog.taskId"
      :task-content="completeDialog.taskContent"
      :business-key="completeDialog.businessKey"
      :definition-key="completeDialog.definitionKey"
      @ok="getList"
    ></task-complete>
    <task-history v-model="histroyDialog.visible" :instance-id="histroyDialog.instanceId"></task-history>
    <process-viewer v-model="processViewDialog.visible" :instance-id="processViewDialog.instanceId" :proc-def-id="processViewDialog.procDefId"></process-viewer>
  </div>
</template>

<script setup lang="ts" name="todoListPage">
import { loadDicts } from "@/utils/dict";
import { queryTodoList, type ActTaskObj } from "@/api/workflow/activiti/task";
import { ref } from "vue";
import TaskComplete from "../components/complete.vue";
import TaskHistory from "../components/history.vue";
import ProcessViewer from "../../activiti/processViewer/dialog.vue";
import type { ElForm } from "@/api/form";

//ref对象 ################################################
const dicts = loadDicts("activiti_task_type");
// 遮罩层
const loading = ref(true);
// 显示搜索条件
const showSearch = ref(true);
// 总条数
const total = ref(0);
// 待办表格数据
const taskList = ref<ActTaskObj[]>([]);
// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  definitionKey: undefined,
  taskContent: undefined,
});

const completeDialog = ref({
  visible: false,
  taskId: "",
  taskContent: "",
  definitionKey: "",
  businessKey: "",
});
const histroyDialog = ref({
  visible: false,
  instanceId: "",
});
const processViewDialog = ref({
  visible: false,
  instanceId: "",
  procDefId: "",
});
// Element ################################################
const queryFormRef = ref<ElForm>();

// Function ################################################
/** 查询待办列表 */
const getList = () => {
  loading.value = true;
  queryTodoList(queryParams.value)
    .then((rsp) => {
      taskList.value = rsp.rows || [];
      total.value = rsp.total || 0;
    })
    .finally(() => {
      loading.value = false;
    });
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

/** 办理 */
const handleTodo = (row: ActTaskObj) => {
  completeDialog.value.visible = true;
  completeDialog.value.taskId = row.taskId || "";
  completeDialog.value.taskContent = row.taskContent || "详情";
  completeDialog.value.definitionKey = row.definitionKey || "";
  completeDialog.value.businessKey = row.businessKey || "";
};

/** 查看历史 */
const handleShowHistory = (row: ActTaskObj) => {
  histroyDialog.value.visible = true;
  histroyDialog.value.instanceId = row.instanceId || "";
};
/** 查看流程图 */
const handleProcessView = (row: ActTaskObj) => {
  processViewDialog.value.visible = true;
  processViewDialog.value.instanceId = row.instanceId || "";
  processViewDialog.value.procDefId = row.procDefId || "";
};

// 初始化 ################################################
getList();
</script>
