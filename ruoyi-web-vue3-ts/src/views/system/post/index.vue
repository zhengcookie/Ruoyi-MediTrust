<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryRef" :model="queryParams" :inline="true">
      <el-form-item label="岗位编码" prop="postCode">
        <el-input v-model="queryParams.postCode" placeholder="请输入岗位编码" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="岗位名称" prop="postName">
        <el-input v-model="queryParams.postName" placeholder="请输入岗位名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="岗位状态" clearable style="width: 200px">
          <el-option v-for="dict in dicts.sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button v-hasPermi="['system:post:add']" type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-hasPermi="['system:post:edit']" type="success" plain icon="Edit" :disabled="single" @click="handleUpdate">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-hasPermi="['system:post:remove']" type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-hasPermi="['system:post:export']" type="warning" plain icon="Download" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @query-table="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="postList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="岗位编号" align="center" prop="postId" />
      <el-table-column label="岗位编码" align="center" prop="postCode" />
      <el-table-column label="岗位名称" align="center" prop="postName" />
      <el-table-column label="岗位排序" align="center" prop="postSort" />
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="dicts.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ dayjs(scope.row.createTime).format("YYYY-MM-DD HH:mm:ss") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button v-hasPermi="['system:post:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button v-hasPermi="['system:post:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />

    <!-- 添加或修改岗位对话框 -->
    <EditDialog ref="editDialogRef" @data-change="dataChange"></EditDialog>
  </div>
</template>

<script lang="ts" setup name="SysPostPage">
import { listPost, delPost, type PostObj } from "@/api/system/post";
import { loadDicts } from "@/utils/dict";
import { ElMessage, ElMessageBox } from "element-plus";
import { ref } from "vue";
import server from "@/utils/request";
import { dayjs } from "element-plus";
import type { ElForm } from "@/api/form";
import EditDialog from "./edit.vue";

const dicts = loadDicts(["sys_normal_disable"]);

const postList = ref<PostObj[]>([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<string[]>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const queryRef = ref<ElForm>();
const editDialogRef = ref();

const queryParams = ref({
  postId: "",
  pageNum: 1,
  pageSize: 10,
  postCode: "",
  postName: "",
  status: "",
});

/** 查询岗位列表 */
function getList() {
  loading.value = true;
  listPost(queryParams.value).then((response) => {
    postList.value = response.rows || [];
    total.value = response.total || 0;
    loading.value = false;
  });
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
function handleSelectionChange(selection: PostObj[]) {
  ids.value = selection.map((item) => item.postId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}
/** 新增按钮操作 */
function handleAdd() {
  editDialogRef.value?.show({ action: "add" });
}
/** 修改按钮操作 */
function handleUpdate(row: PostObj) {
  const postId = row.postId || ids.value.toString();
  editDialogRef.value?.show({ action: "edit", postId: postId });
}
/** 数据改变 刷新页面 */
function dataChange() {
  getList();
}
/** 删除按钮操作 */
function handleDelete(row: PostObj) {
  const postIds = row.postId || ids.value.toString();
  ElMessageBox.confirm('是否确认删除岗位编号为"' + postIds + '"的数据项？')
    .then(function () {
      return delPost(postIds);
    })
    .then(() => {
      getList();
      ElMessage.success("删除成功");
    });
}
/** 导出按钮操作 */
function handleExport() {
  server.download(
    "system/post/export",
    {
      ...queryParams.value,
    },
    `post_${new Date().getTime()}.xlsx`
  );
}

getList();
</script>
