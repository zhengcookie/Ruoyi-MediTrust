<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryRef" :model="queryParams" :inline="true" label-width="68px">
      <el-form-item label="字典名称" prop="dictName">
        <el-input v-model="queryParams.dictName" placeholder="请输入字典名称" clearable style="width: 240px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="字典类型" prop="dictType">
        <el-input v-model="queryParams.dictType" placeholder="请输入字典类型" clearable style="width: 240px" @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="字典状态" clearable style="width: 240px">
          <el-option v-for="dict in dicts.sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
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
        <el-button v-hasPermi="['system:dict:add']" type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-hasPermi="['system:dict:edit']" type="success" plain icon="Edit" :disabled="single" @click="handleUpdate">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-hasPermi="['system:dict:remove']" type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-hasPermi="['system:dict:export']" type="warning" plain icon="Download" @click="handleExport">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-hasPermi="['system:dict:remove']" type="danger" plain icon="Refresh" @click="handleRefreshCache">刷新缓存</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @query-table="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="typeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="字典编号" align="center" prop="dictId" />
      <el-table-column label="字典名称" align="center" prop="dictName" :show-overflow-tooltip="true" />
      <el-table-column label="字典类型" align="center" :show-overflow-tooltip="true">
        <template #default="scope">
          <router-link :to="'/system/dict-data/index/' + scope.row.dictId" class="link-type">
            <span>{{ scope.row.dictType }}</span>
          </router-link>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="dicts.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ dayjs(scope.row.createTime).format("YYYY-MM-DD HH:mm:ss") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button v-hasPermi="['system:dict:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button v-hasPermi="['system:dict:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />

    <!-- 添加或修改参数配置对话框 -->
    <el-dialog v-model="open" :title="title" width="500px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="form.dictName" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item label="字典类型" prop="dictType">
          <el-input v-model="form.dictType" placeholder="请输入字典类型" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in dicts.sys_normal_disable" :key="dict.value" :label="dict.value">{{ dict.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup name="DictPage">
import { loadDicts } from "@/utils/dict";
import { onBeforeMount, ref } from "vue";
import { addDateRange } from "@/utils/ruoyi";
import { listType, getType, delType, addType, updateType, refreshCache } from "@/api/system/dict/type";
import server from "@/utils/request";
import type { DictTypeObj } from "@/api/system/dict/type";
import { ElMessage, ElMessageBox, dayjs } from "element-plus";
import dictStore from "@/stores/dict";
import type { ElForm, QueryParam } from "@/api/form";

const dStore = dictStore();
const dicts = loadDicts("sys_normal_disable");

// 遮罩层
const loading = ref(true);
// 选中数组
const ids = ref<string[]>([]);
// 非单个禁用
const single = ref(true);
// 非多个禁用
const multiple = ref(true);
// 显示搜索条件
const showSearch = ref(true);
// 总条数
const total = ref(0);
// 字典表格数据
const typeList = ref<DictTypeObj[]>([]);
// 弹出层标题
const title = ref("");
// 是否显示弹出层
const open = ref(false);
// 日期范围
const dateRange = ref<string[]>([]);
// 查询参数
const queryParams = ref<DictTypeObj & QueryParam>({
  pageNum: 1,
  pageSize: 10,
  dictName: undefined,
  dictType: undefined,
  status: undefined,
});
// 表单参数
const form = ref<DictTypeObj>({
  dictName: undefined,
  dictType: undefined,
  status: undefined,
});
// 表单校验
const rules = ref({
  dictName: [{ required: true, message: "字典名称不能为空", trigger: "blur" }],
  dictType: [{ required: true, message: "字典类型不能为空", trigger: "blur" }],
});

const formRef = ref<ElForm>();
const queryRef = ref<ElForm>();

/** 查询字典类型列表 */
const getList = () => {
  loading.value = true;
  listType(addDateRange(queryParams.value, dateRange.value)).then((response) => {
    typeList.value = response.rows ? response.rows : [];
    total.value = response.total ? response.total : 0;
    loading.value = false;
  });
};

// 表单重置
const reset = () => {
  form.value = {
    dictId: undefined,
    dictName: undefined,
    dictType: undefined,
    status: "0",
    remark: undefined,
  };
  formRef.value?.resetFields();
};

// 取消按钮
const cancel = () => {
  open.value = false;
  reset();
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
  title.value = "添加字典类型";
};

// 多选框选中数据
const handleSelectionChange = (selection: DictTypeObj[]) => {
  ids.value = [];
  selection.forEach((item) => {
    item.dictId && ids.value.push(item.dictId);
  });
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

/** 修改按钮操作 */
const handleUpdate = (row: DictTypeObj) => {
  reset();
  const dictId = row.dictId || ids.value[0];
  getType(dictId).then((response) => {
    response.data && (form.value = response.data);
    open.value = true;
    title.value = "修改字典类型";
  });
};

/** 提交按钮 */
const submitForm = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      const add = form.value.dictId ? false : true;
      const promise = add ? addType(form.value) : updateType(form.value);
      promise.then(() => {
        ElMessage.success(add ? "新增成功" : "修改成功");
        open.value = false;
        getList();
      });
    }
  });
};

/** 删除按钮操作 */
const handleDelete = (row: DictTypeObj) => {
  const dictIds = row.dictId || ids.value.toString();
  ElMessageBox.confirm(`是否确认删除字典编号为"${dictIds}"的数据项？`, {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      return delType(dictIds);
    })
    .then(() => {
      getList();
      ElMessage.success("删除成功");
    });
};

/** 导出按钮操作 */
const handleExport = () => {
  server.download(
    "system/dict/type/export",
    {
      ...queryParams,
    },
    `type_${new Date().getTime()}.xlsx`
  );
};

/** 刷新缓存按钮操作 */
const handleRefreshCache = () => {
  refreshCache().then(() => {
    ElMessage.success("刷新成功");
    dStore.cleanDict();
  });
};

//渲染前
onBeforeMount(() => {
  getList();
});
</script>
