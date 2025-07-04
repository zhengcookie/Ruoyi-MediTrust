<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!--部门数据-->
      <el-col :span="4" :xs="24">
        <div class="head-container">
          <el-input v-model="deptName" placeholder="请输入部门名称" clearable prefix-icon="Search" style="margin-bottom: 20px" />
        </div>
        <div class="head-container">
          <el-tree
            ref="deptTreeRef"
            :data="deptOptions"
            :props="{ label: 'label', children: 'children' }"
            :expand-on-click-node="false"
            :filter-node-method="filterNode"
            node-key="id"
            highlight-current
            default-expand-all
            @node-click="handleNodeClick"
          />
        </div>
      </el-col>
      <!--用户数据-->
      <el-col :span="20" :xs="24">
        <el-form v-show="showSearch" ref="queryRef" :model="queryParams" :inline="true" label-width="68px">
          <el-form-item label="用户名称" prop="userName">
            <el-input v-model="queryParams.userName" placeholder="请输入用户名称" clearable style="width: 240px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="手机号码" prop="phonenumber">
            <el-input v-model="queryParams.phonenumber" placeholder="请输入手机号码" clearable style="width: 240px" @keyup.enter="handleQuery" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="用户状态" clearable style="width: 240px">
              <el-option v-for="dict in dicts.sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="创建时间" style="width: 308px">
            <el-date-picker
              v-model="dateRange"
              value-format="YYYY-MM-DD"
              type="daterange"
              range-separator="-"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
            ></el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button v-hasPermi="['system:user:add']" type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['system:user:edit']" type="success" plain icon="Edit" :disabled="single" @click="handleUpdate">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['system:user:remove']" type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['system:user:import']" type="info" plain icon="Upload" @click="handleImport">导入</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button v-hasPermi="['system:user:export']" type="warning" plain icon="Download" @click="handleExport">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" :columns="columns" @query-table="getList"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column v-if="columns[0].visible" key="userId" label="用户编号" align="center" prop="userId" />
          <el-table-column v-if="columns[1].visible" key="userName" label="用户名称" align="center" prop="userName" :show-overflow-tooltip="true" />
          <el-table-column v-if="columns[2].visible" key="nickName" label="用户昵称" align="center" prop="nickName" :show-overflow-tooltip="true" />
          <el-table-column v-if="columns[3].visible" key="deptName" label="部门" align="center" prop="dept.deptName" :show-overflow-tooltip="true" />
          <el-table-column v-if="columns[4].visible" key="phonenumber" label="手机号码" align="center" prop="phonenumber" width="120" />
          <el-table-column v-if="columns[5].visible" key="status" label="状态" align="center">
            <template #default="scope">
              <el-switch v-model="scope.row.status" active-value="0" inactive-value="1" @change="handleStatusChange(scope.row)"></el-switch>
            </template>
          </el-table-column>
          <el-table-column v-if="columns[6].visible" label="创建时间" align="center" prop="createTime" width="160">
            <template #default="scope">
              <span>{{ dayjs(scope.row.createTime).format("YYYY-MM-DD HH:mm:ss") }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width">
            <template #default="scope">
              <el-tooltip v-if="scope.row.userId !== 1" content="修改" placement="top">
                <el-button v-hasPermi="['system:user:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)"></el-button>
              </el-tooltip>
              <el-tooltip v-if="scope.row.userId !== 1" content="删除" placement="top">
                <el-button v-hasPermi="['system:user:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)"></el-button>
              </el-tooltip>
              <el-tooltip v-if="scope.row.userId !== 1" content="重置密码" placement="top">
                <el-button v-hasPermi="['system:user:resetPwd']" link type="primary" icon="Key" @click="handleResetPwd(scope.row)"></el-button>
              </el-tooltip>
              <el-tooltip v-if="scope.row.userId !== 1" content="分配角色" placement="top">
                <el-button v-hasPermi="['system:user:edit']" link type="primary" icon="CircleCheck" @click="handleAuthRole(scope.row)"></el-button>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>
        <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
      </el-col>
    </el-row>

    <!-- 添加或修改用户配置对话框 -->
    <el-dialog v-model="open" :title="title" width="600px" append-to-body>
      <el-form ref="userRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户昵称" prop="nickName">
              <el-input v-model="form.nickName" placeholder="请输入用户昵称" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="归属部门" prop="deptId">
              <el-tree-select
                v-model="form.deptId"
                :data="deptOptions"
                :props="{ value: 'id', label: 'label', children: 'children' }"
                value-key="id"
                placeholder="请选择归属部门"
                check-strictly
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phonenumber">
              <el-input v-model="form.phonenumber" placeholder="请输入手机号码" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col v-if="!form.userId" :span="12">
            <el-form-item label="用户名称" prop="userName">
              <el-input v-model="form.userName" placeholder="请输入用户名称" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col v-if="!form.userId" :span="12">
            <el-form-item label="用户密码" prop="password">
              <el-input v-model="form.password" placeholder="请输入用户密码" type="password" maxlength="20" show-password />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户性别">
              <el-select v-model="form.sex" placeholder="请选择">
                <el-option v-for="dict in dicts.sys_user_sex" :key="dict.value" :label="dict.label" :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in dicts.sys_normal_disable" :key="dict.value" :label="dict.value">{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位">
              <el-select v-model="form.postIds" multiple placeholder="请选择">
                <el-option v-for="item in postOptions" :key="item.postId" :label="item.postName" :value="item.postId" :disabled="item.status == '1'"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色">
              <el-select v-model="form.roleIds" multiple placeholder="请选择">
                <el-option v-for="item in roleOptions" :key="item.roleId" :label="item.roleName" :value="item.roleId" :disabled="item.status == '1'"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 用户导入对话框 -->
    <el-dialog v-model="upload.open" :title="upload.title" width="400px" append-to-body>
      <el-upload
        ref="uploadRef"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或
          <em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip text-center">
            <div class="el-upload__tip">
              <el-checkbox v-model="upload.updateSupport" />
              是否更新已经存在的用户数据
            </div>
            <span>仅允许导入xls、xlsx格式文件。</span>
            <el-link type="primary" :underline="false" style="font-size: 12px; vertical-align: baseline" @click="importTemplate">下载模板</el-link>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFileForm">确 定</el-button>
          <el-button @click="upload.open = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 用户导入对话框 -->
    <el-dialog v-model="rePwdOpen" title="重置密码" width="400px" append-to-body>
      <el-form ref="rePwdRef" :model="rePwdForm" :rules="rePwdRules" label-width="80px">
        <el-row>
          <el-col :span="22">
            <el-form-item label="新密码" prop="password">
              <el-input v-model="rePwdForm.password" type="password" placeholder="请输入新密码" maxlength="20" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitRePwdForm">确 定</el-button>
          <el-button @click="rePwdOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="UserPage">
import { getToken } from "@/utils/auth";
import { changeUserStatus, listUser, delUser, getUser, updateUser, addUser, deptTreeSelect, type UserInfoObj, resetUserPwd } from "@/api/system/user";
import { useRouter } from "vue-router";
import { loadDicts } from "@/utils/dict";
import { reactive, ref, watch } from "vue";
import type { DeptObj } from "@/api/system/dept";
import { addDateRange } from "@/utils/ruoyi";
import { ElMessage, ElMessageBox, dayjs } from "element-plus";
import server from "@/utils/request";
import type { PostObj } from "@/api/system/post";
import type { RoleObj } from "@/api/system/role";
import type { ElForm, QueryParam } from "@/api/form";

const router = useRouter();
// const { proxy } = getCurrentInstance();
const userRef = ref<ElForm>();
const queryRef = ref<ElForm>();
const deptTreeRef = ref();
const uploadRef = ref();
const dicts = loadDicts(["sys_normal_disable", "sys_user_sex"]);

const userList = ref<UserInfoObj[]>([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<string[]>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const dateRange = ref([]);
const deptName = ref("");
const deptOptions = ref<DeptObj[]>([]);
const postOptions = ref<PostObj[]>([]);
const roleOptions = ref<RoleObj[]>([]);
/*** 用户导入参数 */
const upload = reactive({
  // 是否显示弹出层（用户导入）
  open: false,
  // 弹出层标题（用户导入）
  title: "",
  // 是否禁用上传
  isUploading: false,
  // 是否更新已经存在的用户数据
  updateSupport: 0,
  // 设置上传的请求头部
  headers: { Authorization: "Bearer " + getToken() },
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "/system/user/importData",
});
// 列显隐信息
const columns = ref([
  { key: 0, label: `用户编号`, visible: true },
  { key: 1, label: `用户名称`, visible: true },
  { key: 2, label: `用户昵称`, visible: true },
  { key: 3, label: `部门`, visible: true },
  { key: 4, label: `手机号码`, visible: true },
  { key: 5, label: `状态`, visible: true },
  { key: 6, label: `创建时间`, visible: true },
]);

const queryParams = ref<UserInfoObj & QueryParam>({
  pageNum: 1,
  pageSize: 10,
  userName: "",
  phonenumber: "",
  status: "",
  deptId: "",
  userId: "",
});
const form = ref<UserInfoObj>({ userId: "" });
const rules = ref({
  userName: [
    { required: true, message: "用户名称不能为空", trigger: "blur" },
    {
      min: 2,
      max: 20,
      message: "用户名称长度必须介于 2 和 20 之间",
      trigger: "blur",
    },
  ],
  nickName: [{ required: true, message: "用户昵称不能为空", trigger: "blur" }],
  password: [
    { required: true, message: "用户密码不能为空", trigger: "blur" },
    {
      min: 5,
      max: 20,
      message: "用户密码长度必须介于 5 和 20 之间",
      trigger: "blur",
    },
  ],
  email: [
    {
      type: "email",
      message: "请输入正确的邮箱地址",
      trigger: ["blur", "change"],
    },
  ],
  phonenumber: [
    {
      pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
      message: "请输入正确的手机号码",
      trigger: "blur",
    },
  ],
});

const rePwdRef = ref();
const rePwdOpen = ref(false);
const rePwdForm = ref({
  password: "",
  userId: "",
});
const rePwdRules = ref({
  password: [
    { required: true, message: "用户密码不能为空", trigger: "blur" },
    {
      min: 5,
      max: 20,
      message: "用户密码长度必须介于 5 和 20 之间",
      trigger: "blur",
    },
  ],
});

/** 通过条件过滤节点  */
const filterNode = (value: string, data: { label: string }) => {
  if (!value) return true;
  return data.label.indexOf(value) !== -1;
};
/** 根据名称筛选部门树 */
watch(deptName, (val) => {
  deptTreeRef.value.filter(val);
});
/** 查询部门下拉树结构 */
const getDeptTree = () => {
  deptTreeSelect().then((response) => {
    deptOptions.value = response.data || [];
  });
};
/** 查询用户列表 */
const getList = () => {
  loading.value = true;
  listUser(addDateRange(queryParams.value, dateRange.value)).then((res) => {
    loading.value = false;
    userList.value = res.rows || [];
    total.value = res.total || 0;
  });
};
/** 节点单击事件 */
const handleNodeClick = (data: { id: string }) => {
  queryParams.value.deptId = data.id;
  handleQuery();
};
/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};
/** 重置按钮操作 */
const resetQuery = () => {
  dateRange.value = [];
  queryRef.value?.resetFields();
  queryParams.value.deptId = "";
  deptTreeRef.value.setCurrentKey(null);
  handleQuery();
};
/** 删除按钮操作 */
const handleDelete = (row: UserInfoObj) => {
  const userIds = row.userId || ids.value.toString();
  ElMessageBox.confirm('是否确认删除用户编号为"' + userIds + '"的数据项？')
    .then(() => {
      return delUser(userIds);
    })
    .then(() => {
      getList();
      ElMessage.success("删除成功");
    });
};
/** 导出按钮操作 */
const handleExport = () => {
  server.download(
    "system/user/export",
    {
      ...queryParams.value,
    },
    `user_${new Date().getTime()}.xlsx`
  );
};
/** 用户状态修改  */
const handleStatusChange = (row: UserInfoObj) => {
  const text = row.status === "0" ? "启用" : "停用";
  ElMessageBox.confirm('确认要"' + text + '""' + row.userName + '"用户吗?')
    .then(function () {
      return changeUserStatus(row.userId, row.status || "");
    })
    .then(() => {
      ElMessage.success(text + "成功");
    })
    .catch(function () {
      row.status = row.status === "0" ? "1" : "0";
    });
};
/** 更多操作 */
// function handleCommand(command: string, row: UserInfoObj) {
//   switch (command) {
//     case "handleResetPwd":
//       handleResetPwd(row);
//       break;
//     case "handleAuthRole":
//       handleAuthRole(row);
//       break;
//     default:
//       break;
//   }
// }
/** 跳转角色分配 */
const handleAuthRole = (row: UserInfoObj) => {
  const userId = row.userId;
  router.push("/system/user-auth/role/" + userId);
};
/** 重置密码按钮操作 */
const handleResetPwd = (row: UserInfoObj) => {
  rePwdOpen.value = true;
  rePwdForm.value.userId = row.userId;
};
/** 重置密码提交 */
const submitRePwdForm = () => {
  rePwdRef.value.validate().then((valid: boolean) => {
    if (valid) {
      resetUserPwd(rePwdForm.value.userId, rePwdForm.value.password).then(() => {
        ElMessage.success("修改成功，新密码是：" + rePwdForm.value.password);
        rePwdOpen.value = false;
      });
    }
  });
};
/** 选择条数  */
const handleSelectionChange = (selection: UserInfoObj[]) => {
  ids.value = selection.map((item) => item.userId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};
/** 导入按钮操作 */
const handleImport = () => {
  upload.title = "用户导入";
  upload.open = true;
};
/** 下载模板操作 */
const importTemplate = () => {
  server.download("system/user/importTemplate", {}, `user_template_${new Date().getTime()}.xlsx`);
};
/**文件上传中处理 */
const handleFileUploadProgress = () => {
  upload.isUploading = true;
};
/** 文件上传成功处理 */
const handleFileSuccess = (response: { msg: string }, file: File) => {
  upload.open = false;
  upload.isUploading = false;
  uploadRef.value.handleRemove(file);
  ElMessage.success(response.msg);
  getList();
};
/** 提交上传文件 */
const submitFileForm = () => {
  uploadRef.value.submit();
};
/** 重置操作表单 */
const reset = () => {
  form.value = {
    userId: "",
    deptId: undefined,
    userName: undefined,
    nickName: undefined,
    password: undefined,
    phonenumber: undefined,
    email: undefined,
    sex: undefined,
    status: "0",
    remark: undefined,
    postIds: [],
    roleIds: [],
  };
  userRef.value?.resetFields();
};
/** 取消按钮 */
const cancel = () => {
  open.value = false;
  reset();
};
/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  getUser(form.value.userId).then((response) => {
    postOptions.value = response.posts;
    roleOptions.value = response.roles;
    open.value = true;
    title.value = "添加用户";
    form.value.password = "";
  });
};
/** 修改按钮操作 */
const handleUpdate = (row: UserInfoObj) => {
  reset();
  const userId = row.userId || ids.value.toString();
  getUser(userId).then((response) => {
    form.value = response.data || { userId: "" };
    postOptions.value = response.posts;
    roleOptions.value = response.roles;
    form.value.postIds = response.postIds || [];
    form.value.roleIds = response.roleIds || [];
    open.value = true;
    title.value = "修改用户";
    form.value.password = "";
  });
};
/** 提交按钮 */
const submitForm = () => {
  userRef.value?.validate((valid) => {
    if (valid) {
      if (form.value.userId) {
        updateUser(form.value).then(() => {
          ElMessage.success("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addUser(form.value).then(() => {
          ElMessage.success("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
};

getDeptTree();
getList();
</script>
