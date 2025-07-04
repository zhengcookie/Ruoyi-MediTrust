<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryFormRef" :model="queryParams" :inline="true">
      <el-form-item label="表名称" prop="tableName">
        <el-input v-model="queryParams.tableName" placeholder="请输入表名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="表描述" prop="tableComment">
        <el-input v-model="queryParams.tableComment" placeholder="请输入表描述" clearable style="width: 200px" @keyup.enter="handleQuery" />
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
        <el-button v-hasPermi="['tool:gen:code']" type="primary" plain icon="Download" @click="handleGenTable">生成</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-hasPermi="['tool:gen:import']" type="info" plain icon="Upload" @click="openImportTable">导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-hasPermi="['tool:gen:edit']" type="success" plain icon="Edit" :disabled="single" @click="handleEditTable">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-hasPermi="['tool:gen:remove']" type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @query-table="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="tableList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" align="center" width="55"></el-table-column>
      <el-table-column label="序号" type="index" width="50" align="center">
        <template #default="scope">
          <span>{{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="表名称" align="center" prop="tableName" :show-overflow-tooltip="true" />
      <el-table-column label="表描述" align="center" prop="tableComment" :show-overflow-tooltip="true" />
      <el-table-column label="实体" align="center" prop="className" :show-overflow-tooltip="true" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="更新时间" align="center" prop="updateTime" width="160" />
      <el-table-column label="操作" align="center" width="330" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-tooltip content="预览" placement="top">
            <el-button v-hasPermi="['tool:gen:preview']" link type="primary" icon="View" @click="handlePreview(scope.row)"></el-button>
          </el-tooltip>
          <el-tooltip content="编辑" placement="top">
            <el-button v-hasPermi="['tool:gen:edit']" link type="primary" icon="Edit" @click="handleEditTable(scope.row)"></el-button>
          </el-tooltip>
          <el-tooltip content="删除" placement="top">
            <el-button v-hasPermi="['tool:gen:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)"></el-button>
          </el-tooltip>
          <el-tooltip content="同步" placement="top">
            <el-button v-hasPermi="['tool:gen:edit']" link type="primary" icon="Refresh" @click="handleSynchDb(scope.row)"></el-button>
          </el-tooltip>
          <el-tooltip content="生成代码" placement="top">
            <el-button v-hasPermi="['tool:gen:code']" link type="primary" icon="Download" @click="handleGenTable(scope.row)"></el-button>
          </el-tooltip>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    <!-- 预览界面 -->
    <el-dialog v-model="preview.open" :title="preview.title" width="80%" top="5vh" append-to-body class="scrollbar">
      <el-tabs v-model="preview.activeName">
        <el-tab-pane
          v-for="item in preview.data"
          :key="item.value"
          :label="item.key.substring(item.key.lastIndexOf('/') + 1, item.key.indexOf('.vm'))"
          :name="item.key.substring(item.key.lastIndexOf('/') + 1, item.key.indexOf('.vm'))"
        >
          <el-link v-copyText="item.value" v-copyText:callback="copyTextSuccess" :underline="false" icon="DocumentCopy" style="float: right">&nbsp;复制</el-link>
          <pre>{{ item.value }}</pre>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
    <import-table ref="importRef" @ok="handleQuery" />
  </div>
</template>

<script lang="ts" setup name="ToolGen">
import { listTable, previewTable, delTable, genCode, synchDb, type GenInfoObj, type GenTColumn } from "@/api/tool/gen";
import router from "@/router";
import importTable from "./importTable.vue";
import { useRoute } from "vue-router";
import { onActivated, ref } from "vue";
import { addDateRange } from "@/utils/ruoyi";
import { ElMessage, ElMessageBox } from "element-plus";
import server from "@/utils/request";
import type { ElForm, QueryParam } from "@/api/form";

const route = useRoute();

const tableList = ref<GenInfoObj[]>([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<string[]>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const tableNames = ref<string[]>([]);
const dateRange = ref<string[]>([]);
const uniqueId = ref("");
const queryFormRef = ref<ElForm>();
const importRef = ref();

const queryParams = ref<GenTColumn & QueryParam>({
  pageNum: 1,
  pageSize: 10,
  tableName: undefined,
  tableComment: undefined,
});
const preview = ref<{
  open: boolean;
  title: string;
  data: { key: string; value: string }[];
  activeName: string;
}>({
  open: false,
  title: "代码预览",
  data: [],
  activeName: "domain.java",
});

onActivated(() => {
  const time = route.query.t;
  if (typeof time === "string" && time != uniqueId.value) {
    uniqueId.value = time;
    queryParams.value.pageNum = Number(route.query.pageNum);
    dateRange.value = [];
    queryFormRef.value?.resetFields();
    getList();
  }
});

/** 查询表集合 */
function getList() {
  loading.value = true;
  listTable(addDateRange(queryParams.value, dateRange.value)).then((response) => {
    tableList.value = response.rows || [];
    total.value = response.total || 0;
    loading.value = false;
  });
}
/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}
/** 生成代码操作 */
function handleGenTable(row: GenInfoObj) {
  const tbNames = row.tableName || tableNames.value;
  if (tbNames == "") {
    ElMessage.error("请选择要生成的数据");
    return;
  }
  if (row.genType === "1") {
    genCode(row.tableName || "").then(() => {
      ElMessage.success("成功生成到自定义路径：" + row.genPath);
    });
  } else {
    server.download("/tool/gen/batchGenCode?tables=" + tbNames, {}, "ruoyi.zip");
  }
}
/** 同步数据库操作 */
function handleSynchDb(row: GenInfoObj) {
  const tableName = row.tableName;
  ElMessageBox.confirm('确认要强制同步"' + tableName + '"表结构吗？')
    .then(function () {
      return synchDb(tableName || "");
    })
    .then(() => {
      ElMessage.success("同步成功");
    });
}
/** 打开导入表弹窗 */
function openImportTable() {
  importRef.value.show();
}
/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  queryFormRef.value?.resetFields();
  handleQuery();
}
/** 预览按钮 */
function handlePreview(row: GenInfoObj) {
  row.tableId &&
    previewTable(row.tableId).then((response) => {
      preview.value.data = [];
      if (response.data) {
        for (const key in response.data) {
          preview.value.data.push({
            key: key,
            value: response.data[key],
          });
        }
      }
      preview.value.open = true;
      preview.value.activeName = "domain.java";
    });
}
/** 复制代码成功 */
function copyTextSuccess(status: boolean) {
  ElMessage.success(status ? "复制成功" : "复制失败");
}
// 多选框选中数据
function handleSelectionChange(selection: GenInfoObj[]) {
  ids.value = selection.map((item) => item.tableId || "");
  tableNames.value = selection.map((item) => item.tableName || "");
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}
/** 修改按钮操作 */
function handleEditTable(row: GenInfoObj) {
  const tableId = row.tableId || ids.value[0];
  router.push({
    path: "/tool/gen-edit/index/" + tableId,
    query: { pageNum: queryParams.value.pageNum },
  });
}
/** 删除按钮操作 */
function handleDelete(row: GenInfoObj) {
  const tableIds = row.tableId || ids.value.toString();
  ElMessageBox.confirm('是否确认删除表编号为"' + tableIds + '"的数据项？')
    .then(function () {
      return delTable(tableIds);
    })
    .then(() => {
      getList();
      ElMessage.success("删除成功");
    });
}

getList();
</script>
