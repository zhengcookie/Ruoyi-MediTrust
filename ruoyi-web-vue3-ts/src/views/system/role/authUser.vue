<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryRef" :model="queryParams" :inline="true">
      <el-form-item label="用户名称" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入用户名称" clearable style="width: 240px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="手机号码" prop="phonenumber">
        <el-input v-model="queryParams.phonenumber" placeholder="请输入手机号码" clearable style="width: 240px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button v-hasPermi="['system:role:add']" type="primary" plain icon="Plus" @click="openSelectUser">添加用户</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-hasPermi="['system:role:remove']" type="danger" plain icon="CircleClose" :disabled="multiple" @click="cancelAuthUserAll">批量取消授权</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Close" @click="handleClose">关闭</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @query-table="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="用户名称" prop="userName" :show-overflow-tooltip="true" />
      <el-table-column label="用户昵称" prop="nickName" :show-overflow-tooltip="true" />
      <el-table-column label="邮箱" prop="email" :show-overflow-tooltip="true" />
      <el-table-column label="手机" prop="phonenumber" :show-overflow-tooltip="true" />
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
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button v-hasPermi="['system:role:remove']" link type="primary" icon="CircleClose" @click="cancelAuthUser(scope.row)">取消授权</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    <select-user ref="selectRef" :role-id="queryParams.roleId" @ok="handleQuery" />
  </div>
</template>

<script lang="ts" setup name="AuthUser">
import { useRoute } from "vue-router";
import selectUser from "./selectUser.vue";
import { allocatedUserList, authUserCancel, authUserCancelAll } from "@/api/system/role";
import { loadDicts } from "@/utils/dict";
import { reactive, ref } from "vue";
import type { UserInfoObj } from "@/api/system/user";
import { ElMessage, ElMessageBox, dayjs } from "element-plus";
import tagsStore from "@/stores/tagsView";
import type { ElForm } from "@/api/form";

const tStore = tagsStore();
const route = useRoute();
// const { proxy } = getCurrentInstance();
// const { sys_normal_disable } = proxy.useDict("sys_normal_disable");
const dicts = loadDicts(["sys_normal_disable"]);

const userList = ref<UserInfoObj[]>([]);
const loading = ref(true);
const showSearch = ref(true);
const multiple = ref(true);
const total = ref(0);
const userIds = ref<string[]>([]);
const queryRef = ref<ElForm>();
const selectRef = ref();

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  roleId: typeof route.params.roleId === "string" ? route.params.roleId : "",
  userName: "",
  phonenumber: "",
});

/** 查询授权用户列表 */
function getList() {
  loading.value = true;
  allocatedUserList(queryParams).then((response) => {
    userList.value = response.rows || [];
    total.value = response.total || 0;
    loading.value = false;
  });
}
// 返回按钮
function handleClose() {
  tStore.closeOpenPage("/system/role");
}
/** 搜索按钮操作 */
function handleQuery() {
  queryParams.pageNum = 1;
  getList();
}
/** 重置按钮操作 */
function resetQuery() {
  queryRef.value?.resetFields();
  handleQuery();
}
// 多选框选中数据
function handleSelectionChange(selection: UserInfoObj[]) {
  userIds.value = selection.map((item) => item.userId);
  multiple.value = !selection.length;
}
/** 打开授权用户表弹窗 */
function openSelectUser() {
  selectRef.value.show();
}
/** 取消授权按钮操作 */
function cancelAuthUser(row: UserInfoObj) {
  ElMessageBox.confirm('确认要取消该用户"' + row.userName + '"角色吗？')
    .then(function () {
      return authUserCancel({ userId: row.userId, roleId: queryParams.roleId });
    })
    .then(() => {
      getList();
      ElMessage.success("取消授权成功");
    });
}
/** 批量取消授权按钮操作 */
function cancelAuthUserAll() {
  const roleId = queryParams.roleId;
  const uIds = userIds.value.join(",");
  ElMessageBox.confirm("是否取消选中用户授权数据项?")
    .then(function () {
      return authUserCancelAll({ roleId: roleId, userIds: uIds });
    })
    .then(() => {
      getList();
      ElMessage.success("取消授权成功");
    });
}

getList();
</script>
