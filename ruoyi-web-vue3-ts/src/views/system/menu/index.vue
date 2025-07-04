<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryRef" :model="queryParams" :inline="true">
      <el-form-item label="菜单名称" prop="menuName">
        <el-input v-model="queryParams.menuName" placeholder="请输入菜单名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="菜单状态" clearable style="width: 200px">
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
        <el-button v-hasPermi="['system:menu:add']" type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="Sort" @click="toggleExpandAll">展开/折叠</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @query-table="getList"></right-toolbar>
    </el-row>

    <el-table
      v-if="refreshTable"
      v-loading="loading"
      :data="menuList"
      row-key="menuId"
      :default-expand-all="isExpandAll"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column prop="menuName" label="菜单名称" :show-overflow-tooltip="true" width="160"></el-table-column>
      <el-table-column prop="icon" label="图标" align="center" width="100">
        <template #default="scope">
          <el-icon v-if="scope.row.icon" :key="scope.row.id + 'icon1'">
            <component :is="scope.row.icon" />
          </el-icon>
          <span v-else :key="scope.row.id + 'icon2'">#</span>
        </template>
      </el-table-column>
      <el-table-column prop="orderNum" label="排序" width="60"></el-table-column>
      <el-table-column prop="perms" label="权限标识" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="component" label="组件路径" :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="scope">
          <dict-tag :options="dicts.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" width="160" prop="createTime">
        <template #default="scope">
          <span>{{ dayjs(scope.row.createTime).format("YYYY-MM-DD HH:mm:ss") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="210" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button v-hasPermi="['system:menu:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button v-hasPermi="['system:menu:add']" link type="primary" icon="Plus" @click="handleAdd(scope.row)">新增</el-button>
          <el-button v-hasPermi="['system:menu:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改菜单对话框 -->
    <EditDialog ref="editDialogRef" @refresh-data="getList"></EditDialog>
  </div>
</template>

<script lang="ts" setup name="SysMenu">
import EditDialog from "./edit.vue";
import { delMenu, listMenu, type MenuObj } from "@/api/system/menu";
import { loadDicts } from "@/utils/dict";
import { nextTick, ref } from "vue";
import { handleTree } from "@/utils/ruoyi";
import { ElMessage, ElMessageBox, dayjs } from "element-plus";
import type { ElForm } from "@/api/form";

// 内部变量 #####################################################
// 数据字典
const dicts = loadDicts(["sys_show_hide", "sys_normal_disable"]);

const menuList = ref<MenuObj[]>([]);
const loading = ref(true);
const showSearch = ref(true);
const isExpandAll = ref(false);
const refreshTable = ref(true);
const queryRef = ref<ElForm>();

const queryParams = ref({
  menuId: "",
  menuName: undefined,
  visible: undefined,
  status: undefined,
});

// ref 元素
const editDialogRef = ref();

// function #####################################################
/** 查询菜单列表 */
function getList() {
  loading.value = true;
  listMenu(queryParams.value)
    .then((response) => {
      menuList.value = handleTree(response.data || [], "menuId");
    })
    .finally(() => {
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
function handleAdd(row: MenuObj) {
  editDialogRef.value.show({ action: "add", parentId: row.menuId });
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
function handleUpdate(row: MenuObj) {
  editDialogRef.value.show({ action: "edit", menuId: row.menuId });
}

/** 删除按钮操作 */
function handleDelete(row: MenuObj) {
  ElMessageBox.confirm('是否确认删除名称为"' + row.menuName + '"的数据项?')
    .then(function () {
      return delMenu(row.menuId);
    })
    .then(() => {
      getList();
      ElMessage.success("删除成功");
    });
}

getList();
</script>
