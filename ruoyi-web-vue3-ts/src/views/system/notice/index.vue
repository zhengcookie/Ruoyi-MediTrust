<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryRef" :model="queryParams" :inline="true">
      <el-form-item label="公告标题" prop="noticeTitle">
        <el-input v-model="queryParams.noticeTitle" placeholder="请输入公告标题" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="操作人员" prop="createBy">
        <el-input v-model="queryParams.createBy" placeholder="请输入操作人员" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="类型" prop="noticeType">
        <el-select v-model="queryParams.noticeType" placeholder="公告类型" clearable style="width: 200px">
          <el-option v-for="dict in dicts.sys_notice_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button v-hasPermi="['system:notice:add']" type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-hasPermi="['system:notice:edit']" type="success" plain icon="Edit" :disabled="single" @click="handleUpdate">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-hasPermi="['system:notice:remove']" type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @query-table="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="noticeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" prop="noticeId" width="100" />
      <el-table-column label="公告标题" align="center" prop="noticeTitle" :show-overflow-tooltip="true" />
      <el-table-column label="公告类型" align="center" prop="noticeType" width="100">
        <template #default="scope">
          <dict-tag :options="dicts.sys_notice_type" :value="scope.row.noticeType" />
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <dict-tag :options="dicts.sys_notice_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建者" align="center" prop="createBy" width="100" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="100">
        <template #default="scope">
          <span>{{ dayjs(scope.row.createTime).format("YYYY-MM-DD") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button v-hasPermi="['system:notice:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button v-hasPermi="['system:notice:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />

    <!-- 添加或修改公告对话框 -->
    <EditDialog ref="editDialogRef" @data-change="dataChange"></EditDialog>
  </div>
</template>

<script lang="ts" setup name="SysNotice">
import type { ElForm } from "@/api/form";
import { listNotice, delNotice, type NoticeObj } from "@/api/system/notice";
import { loadDicts } from "@/utils/dict";
import { ElMessage, ElMessageBox, dayjs } from "element-plus";
import { ref } from "vue";
import EditDialog from "./edit.vue";

const dicts = loadDicts(["sys_notice_status", "sys_notice_type"]);

const noticeList = ref<NoticeObj[]>([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<string[]>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const editDialogRef = ref();
const queryRef = ref<ElForm>();

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  noticeId: "",
  noticeTitle: undefined,
  noticeType: undefined,
  createBy: undefined,
  status: undefined,
});

/** 查询公告列表 */
function getList() {
  loading.value = true;
  listNotice(queryParams.value).then((response) => {
    noticeList.value = response.rows || [];
    total.value = response.total || 0;
    loading.value = false;
  });
}
/** 数据改变 刷新页面 */
function dataChange() {
  getList();
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}
/** 重置按钮操作 */
function resetQuery() {
  queryRef.value?.resetFields();
  handleQuery();
}
/** 多选框选中数据 */
function handleSelectionChange(selection: NoticeObj[]) {
  ids.value = selection.map((item) => item.noticeId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}
/** 新增按钮操作 */
function handleAdd() {
  editDialogRef.value?.show({ action: "add" });
}
/**修改按钮操作 */
function handleUpdate(row: NoticeObj) {
  const noticeId = row.noticeId || ids.value.toString();
  editDialogRef.value?.show({ action: "edit", noticeId: noticeId });
}
/** 删除按钮操作 */
function handleDelete(row: NoticeObj) {
  const noticeIds = row.noticeId || ids.value.toString();
  ElMessageBox.confirm('是否确认删除公告编号为"' + noticeIds + '"的数据项？')
    .then(function () {
      return delNotice(noticeIds);
    })
    .then(() => {
      getList();
      ElMessage.success("删除成功");
    });
}

getList();
</script>
