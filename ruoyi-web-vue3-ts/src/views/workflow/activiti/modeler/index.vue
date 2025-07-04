<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" :inline="true" label-width="80px">
      <el-form-item label="流程KEY" prop="key">
        <el-input v-model="queryParams.key" placeholder="请输入流程KEY" clearable style="width: 240px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入名称" clearable style="width: 240px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button v-hasPermi="['activiti:modeler:add']" type="primary" icon="Plus" @click="handleAdd">创建新模型</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @query-table="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="modelerList">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="120" />
      <el-table-column label="KEY" align="center" prop="key" width="120" />
      <el-table-column label="名称" align="center" prop="name" width="150" />
      <el-table-column label="版本" align="center" prop="version" width="150" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ dayjs(scope.row.createTime).format("YYYY-MM-DD HH:mm:ss") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="lastUpdateTime" width="180">
        <template #default="scope">
          <span>{{ dayjs(scope.row.lastUpdateTime).format("YYYY-MM-DD HH:mm:ss") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button v-hasPermi="['activiti:modeler:edit']" link icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button v-hasPermi="['activiti:modeler:deploy']" link icon="Upload" @click="handleDeploy(scope.row)">部署</el-button>
          <el-button v-hasPermi="['activiti:modeler:download']" link icon="Download" @click="handleExport(scope.row)">导出</el-button>
          <el-button v-hasPermi="['activiti:modeler:delete']" link icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />

    <!-- 添加或修改模型对话框 -->
    <el-dialog v-model="open" :title="title" width="500px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="KEY" prop="key">
          <el-input v-model="form.key" placeholder="请输入KEY" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!--bpmnjs在线流程设计器-->
    <bpmn-view v-model="bpmnViewVisible" :model-id="modelId" @close="handleQuery"></bpmn-view>
  </div>
</template>

<script lang="ts" setup name="ActModelerPage">
import server from "@/utils/request";
import bpmnView from "./bpmnView.vue";
import { listModeler, delModeler, addModeler, deployModeler, type ActModelerObj } from "@/api/workflow/activiti/modeler";
import { ref } from "vue";
import { ElMessage, ElMessageBox, dayjs } from "element-plus";
import type { ElForm } from "@/api/form";

//ref对象 ################################################
const bpmnViewVisible = ref(false);
const modelId = ref<string>("");
// 遮罩层
const loading = ref(true);
// 显示搜索条件
const showSearch = ref(true);
// 总条数
const total = ref(0);
// 模型表格数据
const modelerList = ref<ActModelerObj[]>([]);
// 弹出层标题
const title = ref("");
// 是否显示弹出层
const open = ref(false);
// 日期范围
const dateRange = ref<string[]>([]);
// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  key: "",
  name: "",
  id: "",
});
// 表单参数
const form = ref<ActModelerObj>({ id: "" });
// 表单校验
const rules = ref({
  key: [{ required: true, message: "KEY不能为空", trigger: "blur" }],
  name: [{ required: true, message: "名称不能为空", trigger: "blur" }],
});

//elment ################################################
const queryRef = ref<ElForm>();
const formRef = ref<ElForm>();

// Function ################################################
/** 查询模型列表 */
const getList = () => {
  loading.value = true;
  listModeler(queryParams.value).then((response) => {
    modelerList.value = response.rows || [];
    total.value = response.total || 0;
    loading.value = false;
  });
};
// 取消按钮
const cancel = () => {
  open.value = false;
  formRef.value?.resetFields();
};
// 表单重置
const reset = () => {
  form.value = {
    id: "",
    name: "",
    key: "",
  };
  queryRef.value?.resetFields();
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
  handleQuery();
};
/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  open.value = true;
  title.value = "添加模型";
};
/** 修改按钮操作 */
const handleUpdate = (row: ActModelerObj) => {
  reset();
  modelId.value = row.id;
  bpmnViewVisible.value = true;
};
/** 提交按钮 */
const submitForm = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      loading.value = true;
      addModeler(form.value)
        .then((response) => {
          if (response.code === 200) {
            open.value = false;
            if (response.data) {
              modelId.value = response.data;
              bpmnViewVisible.value = true;
            }
          }
        })
        .finally(() => {
          loading.value = false;
        });
    }
  });
};
/** 删除按钮操作 */
const handleDelete = (row: ActModelerObj) => {
  const modelIds = row.id;
  ElMessageBox.confirm('是否确认删除ID为"' + modelIds + '"的数据项?')
    .then(function () {
      loading.value = true;
      return delModeler(modelIds);
    })
    .then(() => {
      getList();
      ElMessage.success("删除成功");
    })
    .finally(() => {
      loading.value = false;
    });
};
/** 导出按钮操作 */
const handleExport = (row: ActModelerObj) => {
  server.download("/activiti/modeler/export/" + row.id, {}, `modeler_${new Date().getTime()}.bpmn`);
};
/** 部署按钮操作 */
const handleDeploy = (row: ActModelerObj) => {
  const modelId = row.id;
  ElMessageBox.confirm('是否确认部署ID为"' + modelId + '"的数据项?')
    .then(function () {
      loading.value = true;
      return deployModeler(modelId);
    })
    .then(() => {
      ElMessage.success("部署成功");
    })
    .finally(() => {
      loading.value = false;
    });
};

getList();
</script>

<style lang="less" scoped></style>
