<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" :inline="true">
      <el-form-item label="流程KEY" prop="key">
        <el-input v-model="queryParams.key" placeholder="请输入流程KEY" clearable style="width: 240px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入名称" clearable style="width: 240px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="所属分类" prop="name">
        <el-input v-model="queryParams.category" placeholder="请输入所属分类" clearable style="width: 240px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button v-hasPermi="['activiti:definition:deploy']" type="primary" icon="Upload" @click="handleUpload">部署流程</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @query-table="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="definitionList">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="流程ID" align="center" prop="id" width="120" />
      <el-table-column label="流程KEY" align="center" prop="key" width="120" />
      <el-table-column label="流程名称" align="center" prop="name" width="150" />
      <el-table-column label="版本" align="center" prop="version" width="90" />
      <el-table-column label="流程描述" align="center" prop="description" width="150" />
      <!-- <el-table-column label="所属分类" align="center" prop="category" width="120" /> -->
      <el-table-column label="部署时间" align="center" prop="deploymentTime" width="180">
        <template #default="scope">
          <span>{{ dayjs(scope.row.deploymentTime).format("YYYY-MM-DD HH:mm:ss") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="suspendState" width="90">
        <template #default="scope">
          {{ scope.row.suspendState === 2 ? "已挂起" : "已激活" }}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template #default="scope">
          <el-button v-hasPermi="['activiti:definition:state']" link icon="Sort" @click="handleState(scope.row)">
            {{ scope.row.suspendState === 2 ? "激活" : "挂起" }}
          </el-button>
          <el-button v-hasPermi="['activiti:definition:tomodel']" link icon="Refresh" @click="handleConvert(scope.row)">转模型</el-button>
          <el-button v-hasPermi="['activiti:definition:delete']" link icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />

    <!-- 部署流程定义对话框 -->
    <el-dialog v-model="open" :title="title" width="400px" append-to-body>
      <el-upload ref="uploadRef" v-model:file-list="uploadFileList" :auto-upload="false" :limit="1" accept=".bpmn" :before-upload="uploadBeforeUpload" :http-request="submitUpload">
        <el-button type="primary" icon="Plus">选择文件</el-button>
      </el-upload>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="close">取消</el-button>
          <el-button type="primary" :disabled="uploadFileList.length === 0" @click="handelConfirm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup name="ActDefinitionPage">
import type { ElForm } from "@/api/form";
import { listDefinition, delDefinition, suspendOrActiveApply, convert2Model, uploadDefinition, type ActDefinitionObj } from "@/api/workflow/activiti/definition";
import { ElMessage, ElMessageBox, dayjs, type UploadRawFile, type UploadRequestOptions } from "element-plus";
import { ref } from "vue";

//ref对象 ################################################
// 遮罩层
const loading = ref(true);
// 显示搜索条件
const showSearch = ref(true);
// 总条数
const total = ref(0);
// 流程定义表格数据
const definitionList = ref<ActDefinitionObj[]>([]);
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
  id: "",
  key: undefined,
  name: undefined,
  category: undefined,
});
const uploadFileList = ref([]);
//elment ################################################
const queryRef = ref<ElForm>();
const uploadRef = ref();

// Function ################################################
/** 查询流程定义列表 */
const getList = () => {
  loading.value = true;
  listDefinition(queryParams.value).then((response) => {
    definitionList.value = response.rows || [];
    total.value = response.total || 0;
    loading.value = false;
  });
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

/**
 * 新增按钮操作
 */
const handleUpload = () => {
  open.value = true;
  title.value = "部署流程定义";
};

/**
 * 关闭上传文件窗口
 */
const close = () => {
  open.value = false;
  uploadRef.value.clearFiles();
};

/**
 * 提交上传文件
 */
const handelConfirm = () => {
  uploadRef.value.submit();
};

/**
 * 上传文件之前处理
 */
const uploadBeforeUpload = (file: UploadRawFile) => {
  const isRightSize = file.size / 1024 / 1024 < 2;
  if (!isRightSize) {
    ElMessage.error("文件大小超过 2MB");
  }
  return isRightSize;
};

/**
 * 上传文件
 */
const submitUpload = (options: UploadRequestOptions) => {
  uploadDefinition({ file: options.file }).then((response) => {
    if (response.code === 200) {
      ElMessage.success("部署成功");
      getList();
      close();
    } else {
      ElMessage.error(response.msg);
    }
  });
};

/** 激活挂起按钮操作 */
const handleState = (row: ActDefinitionObj) => {
  const pid = row.id;
  const suspendState = row.suspendState;
  const opt = row.suspendState === 2 ? "激活" : "挂起";
  ElMessageBox.confirm("是否确认" + opt + 'ID为"' + pid + '"的流程定义?')
    .then(() => {
      loading.value = true;
      const data = { id: pid, suspendState: suspendState };
      return suspendOrActiveApply(data);
    })
    .then(() => {
      ElMessage.success("操作成功");
      getList();
    })
    .finally(() => {
      loading.value = false;
    });
};

/** 转模型按钮操作 */
const handleConvert = (row: ActDefinitionObj) => {
  const pid = row.id;
  ElMessageBox.confirm('是否确认将ID为"' + pid + '"的流程定义转换成流程模型?')
    .then(() => {
      loading.value = true;
      const data = { processDefinitionId: pid };
      return convert2Model(data);
    })
    .then(() => {
      ElMessage.success("操作成功");
      getList();
    })
    .finally(() => {
      loading.value = false;
    });
};
/** 删除按钮操作 */
const handleDelete = (row: ActDefinitionObj) => {
  const deploymentId = row.deploymentId;
  deploymentId &&
    ElMessageBox.confirm('是否确认删除ID为"' + deploymentId + '"的数据项?')
      .then(() => {
        loading.value = true;
        return delDefinition(deploymentId);
      })
      .then(() => {
        getList();
        ElMessage.success("删除成功");
      })
      .finally(() => {
        loading.value = false;
      });
};

getList();
</script>

<style lang="less" scoped></style>
