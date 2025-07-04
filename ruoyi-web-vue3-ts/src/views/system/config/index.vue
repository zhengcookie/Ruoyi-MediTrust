<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryRef" :model="queryParams" :inline="true" label-width="68px">
      <el-form-item label="参数名称" prop="configName">
        <el-input v-model="queryParams.configName" placeholder="请输入参数名称" clearable style="width: 240px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="参数键名" prop="configKey">
        <el-input v-model="queryParams.configKey" placeholder="请输入参数键名" clearable style="width: 240px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="系统内置" prop="configType">
        <el-select v-model="queryParams.configType" placeholder="系统内置" clearable>
          <el-option v-for="dict in dicts.sys_yes_no" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" style="width: 308px">
        <el-date-picker v-model="dateRange" value-format="YYYY-MM-DD" type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button v-hasPermi="['system:config:add']" type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-hasPermi="['system:config:edit']" type="success" plain icon="Edit" :disabled="single" @click="handleUpdate">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-hasPermi="['system:config:remove']" type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-hasPermi="['system:config:export']" type="warning" plain icon="Download" @click="handleExport">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-hasPermi="['system:config:refresh']" type="danger" plain icon="Refresh" @click="handleRefreshCache">刷新缓存</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @query-table="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="configList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="参数主键" align="center" prop="configId" />
      <el-table-column label="参数名称" align="center" prop="configName" :show-overflow-tooltip="true" />
      <el-table-column label="参数键名" align="center" prop="configKey" :show-overflow-tooltip="true" />
      <el-table-column label="参数键值" align="center" prop="configValue" :show-overflow-tooltip="true" />
      <el-table-column label="系统内置" align="center" prop="configType">
        <template #default="scope">
          <dict-tag :options="dicts.sys_yes_no" :value="scope.row.configType" />
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ dayjs(scope.row.createTime).format("YYYY-MM-DD HH:mm:ss") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button v-hasPermi="['system:config:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button v-hasPermi="['system:config:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />

    <!-- 添加或修改参数配置对话框 -->
    <EditDialog ref="editDialogRef" @data-change="dataChange"></EditDialog>
  </div>
</template>

<script lang="ts" setup name="SysConfig">
import type { ElForm, QueryParam } from "@/api/form";
import { listConfig, delConfig, refreshCache, type SysConfigObj } from "@/api/system/config";
import { loadDicts } from "@/utils/dict";
import { addDateRange } from "@/utils/ruoyi";
import { ElMessage, ElMessageBox, dayjs } from "element-plus";
import { ref } from "vue";
import server from "@/utils/request";
import EditDialog from "./edit.vue";

const dicts = loadDicts(["sys_yes_no"]);

const configList = ref<SysConfigObj[]>([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<string[]>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const dateRange = ref([]);

const queryRef = ref<ElForm>();
const editDialogRef = ref();

const queryParams = ref<SysConfigObj & QueryParam>({
  pageNum: 1,
  pageSize: 10,
  configName: undefined,
  configKey: undefined,
  configType: undefined,
});

/** 查询参数列表 */
function getList() {
  loading.value = true;
  listConfig(addDateRange(queryParams.value, dateRange.value)).then((response) => {
    configList.value = response.rows || [];
    total.value = response.total || 0;
    loading.value = false;
  });
}
/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 数据改变 刷新页面 */
function dataChange() {
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  queryRef.value?.resetFields();
  handleQuery();
}
/** 多选框选中数据 */
function handleSelectionChange(selection: SysConfigObj[]) {
  ids.value = selection.map((item) => item.configId || "");
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}
/** 新增按钮操作 */
function handleAdd() {
  editDialogRef.value?.show({ action: "add" });
}
/** 修改按钮操作 */
function handleUpdate(row: SysConfigObj) {
  const configId = row.configId || ids.value.toString();
  editDialogRef.value?.show({ action: "edit", configId: configId });
}
/** 删除按钮操作 */
function handleDelete(row: SysConfigObj) {
  const configIds = row.configId || ids.value.toString();
  ElMessageBox.confirm('是否确认删除参数编号为"' + configIds + '"的数据项？')
    .then(function () {
      return delConfig(configIds);
    })
    .then(() => {
      getList();
      ElMessage.success("删除成功");
    });
}
/** 导出按钮操作 */
function handleExport() {
  server.download(
    "system/config/export",
    {
      ...queryParams.value,
    },
    `config_${new Date().getTime()}.xlsx`
  );
}
/** 刷新缓存按钮操作 */
function handleRefreshCache() {
  refreshCache().then(() => {
    ElMessage.success("刷新缓存成功");
  });
}

getList();
</script>
