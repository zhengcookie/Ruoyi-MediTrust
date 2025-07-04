<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryRef" :model="queryParams" :inline="true" label-width="68px">
      <el-form-item label="任务名称" prop="jobName">
        <el-input v-model="queryParams.jobName" placeholder="请输入任务名称" clearable style="width: 240px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="任务组名" prop="jobGroup">
        <el-select v-model="queryParams.jobGroup" placeholder="请选择任务组名" clearable style="width: 240px">
          <el-option v-for="dict in dicts.sys_job_group" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="执行状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择执行状态" clearable style="width: 240px">
          <el-option v-for="dict in dicts.sys_common_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="执行时间" style="width: 308px">
        <el-date-picker v-model="dateRange" value-format="YYYY-MM-DD" type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button v-hasPermi="['monitor:job:remove']" type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-hasPermi="['monitor:job:remove']" type="danger" plain icon="Delete" @click="handleClean">清空</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-hasPermi="['monitor:job:export']" type="warning" plain icon="Download" @click="handleExport">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Close" @click="handleClose">关闭</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @query-table="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jobLogList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="日志编号" width="80" align="center" prop="jobLogId" />
      <el-table-column label="任务名称" align="center" prop="jobName" :show-overflow-tooltip="true" />
      <el-table-column label="任务组名" align="center" prop="jobGroup" :show-overflow-tooltip="true">
        <template #default="scope">
          <dict-tag :options="dicts.sys_job_group" :value="scope.row.jobGroup" />
        </template>
      </el-table-column>
      <el-table-column label="调用目标字符串" align="center" prop="invokeTarget" :show-overflow-tooltip="true" />
      <el-table-column label="日志信息" align="center" prop="jobMessage" :show-overflow-tooltip="true" />
      <el-table-column label="执行状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="dicts.sys_common_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="执行时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ dayjs(scope.row.createTime).format("YYYY-MM-DD HH:mm:ss") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button v-hasPermi="['monitor:job:query']" link type="primary" icon="View" @click="handleView(scope.row)">详细</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />

    <!-- 调度日志详细 -->
    <el-dialog v-model="open" title="调度日志详细" width="700px" append-to-body>
      <el-form :model="form" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="日志序号：">{{ form.jobLogId }}</el-form-item>
            <el-form-item label="任务名称：">{{ form.jobName }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务分组：">{{ form.jobGroup }}</el-form-item>
            <el-form-item label="执行时间：">{{ form.createTime }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="调用方法：">{{ form.invokeTarget }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="日志信息：">{{ form.jobMessage }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="执行状态：">
              <div v-if="form.status == '0'">正常</div>
              <div v-else-if="form.status == '1'">失败</div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item v-if="form.status == '1'" label="异常信息：">{{ form.exceptionInfo }}</el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="open = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup name="JobLogPage">
import server from "@/utils/request";
import { loadDicts } from "@/utils/dict";
import { addDateRange } from "@/utils/ruoyi";
import { getJob } from "@/api/monitor/job";
import { listJobLog, delJobLog, cleanJobLog, type JobLogObj } from "@/api/monitor/jobLog";
import { ref } from "vue";
import { useRoute } from "vue-router";
import tagsStore from "@/stores/tagsView";
import { dayjs, ElMessage, ElMessageBox } from "element-plus";
import type { ElForm, QueryParam } from "@/api/form";

const tStore = tagsStore();
const dicts = loadDicts(["sys_common_status", "sys_job_group"]);

const jobLogList = ref<JobLogObj[]>([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<string[]>([]);
const multiple = ref(true);
const total = ref(0);
const dateRange = ref<string[]>([]);
const route = useRoute();
const queryRef = ref<ElForm>();

const form = ref<JobLogObj>({
  jobLogId: "",
});
const queryParams = ref<JobLogObj & QueryParam>({
  pageNum: 1,
  pageSize: 10,
  jobLogId: "",
  jobName: undefined,
  jobGroup: undefined,
});

/** 查询调度日志列表 */
function getList() {
  loading.value = true;
  listJobLog(addDateRange(queryParams.value, dateRange.value)).then((response) => {
    jobLogList.value = response.rows || [];
    total.value = response.total || 0;
    loading.value = false;
  });
}
// 返回按钮
function handleClose() {
  tStore.closeOpenPage("/monitor/job");
}
/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}
/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  queryRef.value?.resetFields();
  handleQuery();
}
// 多选框选中数据
function handleSelectionChange(selection: JobLogObj[]) {
  ids.value = selection.map((item) => item.jobLogId);
  multiple.value = !selection.length;
}
/** 详细按钮操作 */
function handleView(row: JobLogObj) {
  open.value = true;
  form.value = row;
}
/** 删除按钮操作 */
function handleDelete() {
  ElMessageBox.confirm('是否确认删除调度日志编号为"' + ids.value.toString() + '"的数据项?')
    .then(function () {
      return delJobLog(ids.value.toString());
    })
    .then(() => {
      getList();
      ElMessage.success("删除成功");
    });
}
/** 清空按钮操作 */
function handleClean() {
  ElMessageBox.confirm("是否确认清空所有调度日志数据项?")
    .then(function () {
      return cleanJobLog();
    })
    .then(() => {
      getList();
      ElMessage.success("清空成功");
    });
}
/** 导出按钮操作 */
function handleExport() {
  server.download(
    "monitor/jobLog/export",
    {
      ...queryParams.value,
    },
    `job_log_${new Date().getTime()}.xlsx`
  );
}

(() => {
  const jobId = route.params && route.params.jobId;
  if (jobId !== undefined && typeof jobId === "string" && jobId != "0") {
    getJob(jobId).then(({ data }) => {
      if (data) {
        queryParams.value.jobName = data.jobName;
        queryParams.value.jobGroup = data.jobGroup;
      }
      getList();
    });
  } else {
    getList();
  }
})();

getList();
</script>
