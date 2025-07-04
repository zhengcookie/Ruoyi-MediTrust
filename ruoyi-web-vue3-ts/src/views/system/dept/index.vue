<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryRef" :model="queryParams" :inline="true">
      <el-form-item label="部门名称" prop="deptName">
        <el-input v-model="queryParams.deptName" placeholder="请输入部门名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="部门状态" clearable style="width: 200px">
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
        <el-button v-hasPermi="['system:dept:add']" type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="Sort" @click="toggleExpandAll">展开/折叠</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @query-table="getList"></right-toolbar>
    </el-row>

    <el-table
      v-if="refreshTable"
      v-loading="loading"
      :data="deptList"
      row-key="deptId"
      :default-expand-all="isExpandAll"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column prop="deptName" label="部门名称" width="260"></el-table-column>
      <el-table-column prop="orderNum" label="排序" width="200"></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <dict-tag :options="dicts.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="200">
        <template #default="scope">
          <span>{{ dayjs(scope.row.createTime).format("YYYY-MM-DD HH:mm:ss") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button v-hasPermi="['system:dept:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button v-hasPermi="['system:dept:add']" link type="primary" icon="Plus" @click="handleAdd(scope.row)">新增</el-button>
          <el-button v-if="scope.row.parentId != 0" v-hasPermi="['system:dept:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改部门对话框 -->
    <EditDialog ref="editDialogRef" @data-change="handleQuery"></EditDialog>
  </div>
</template>

<script lang="ts" setup name="Dept">
import { listDept, delDept, type DeptObj } from "@/api/system/dept";
import { loadDicts } from "@/utils/dict";
import { nextTick, ref } from "vue";
import { handleTree } from "@/utils/ruoyi";
import { ElMessage, ElMessageBox, dayjs } from "element-plus";
import type { ElForm } from "@/api/form";
import EditDialog from "./edit.vue";

const dicts = loadDicts(["sys_normal_disable"]);

const deptList = ref<DeptObj[]>([]);
const loading = ref(true);
const showSearch = ref(true);
const isExpandAll = ref(true);
const refreshTable = ref(true);
const queryRef = ref<ElForm>();
const editDialogRef = ref();

const queryParams = ref({
  deptId: "",
  deptName: "",
  status: "",
});

/** 查询部门列表 */
function getList() {
  loading.value = true;
  listDept(queryParams.value).then((response) => {
    response.data && (deptList.value = handleTree(response.data, "deptId"));
    loading.value = false;
  });
}
/** 搜索按钮操作 */
function handleQuery() {
  getList();
}
/** 重置按钮操作 */
function resetQuery() {
  queryRef.value?.resetFields();
  handleQuery();
}
/** 新增按钮操作 */
function handleAdd(row: DeptObj | undefined) {
  editDialogRef.value?.show({ action: "add", deptId: row?.deptId });
}
/** 展开/折叠操作 */
function toggleExpandAll() {
  refreshTable.value = false;
  isExpandAll.value = !isExpandAll.value;
  nextTick(() => {
    refreshTable.value = true;
  });
}
/** 修改按钮操作 */
function handleUpdate(row: DeptObj) {
  editDialogRef.value?.show({ action: "edit", deptId: row.deptId });
}

/** 删除按钮操作 */
function handleDelete(row: DeptObj) {
  ElMessageBox.confirm('是否确认删除名称为"' + row.deptName + '"的数据项?')
    .then(function () {
      return delDept(row.deptId);
    })
    .then(() => {
      getList();
      ElMessage.success("删除成功");
    });
}

getList();
</script>
