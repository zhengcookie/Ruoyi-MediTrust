<template>
  <!--审批历史对话框-->
  <el-dialog v-model="visible" title="审批历史" width="1000px" append-to-body>
    <!--审批历史表格-->
    <div>
      <el-form v-show="showSearch" ref="queryRef" :model="queryParams" :inline="true" label-width="70px">
        <el-form-item label="任务名称" prop="actName">
          <el-input v-model="queryParams.actName" placeholder="请输入任务名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="办理人ID" prop="assigneeName">
          <el-input v-model="queryParams.assigneeName" placeholder="请输入办理人ID" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8">
        <right-toolbar v-model:show-search="showSearch" @query-table="getHistoryList"></right-toolbar>
      </el-row>

      <el-table v-loading="loading" :data="historyList">
        <el-table-column v-if="false" label="活动ID" align="center" prop="actId" width="60" />
        <el-table-column label="任务名称" align="center" prop="actName" width="140" />
        <el-table-column label="办理人ID" align="center" prop="assignee" width="90" />
        <el-table-column label="办理人" align="center" prop="assigneeName" width="80" />
        <el-table-column label="状态" align="center" prop="status" width="80">
          <template #default="scope">
            <dict-tag :options="dicts.activiti_flow_type" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="审批意见" align="center" prop="comment">
          <template #default="scope">
            {{ scope.row.actParamsObj?.comment }}
          </template>
        </el-table-column>
        <el-table-column label="开始时间" align="center" prop="startTime" width="160" />
        <el-table-column label="结束时间" align="center" prop="endTime" width="160" />
      </el-table>

      <!--v-show="total>0"-->
      <pagination v-show="false" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getHistoryList" />
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">关闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup name="CompApplyAfter">
import { loadDicts } from "@/utils/dict";
import { watch } from "vue";
import { computed, ref } from "vue";
import { queryHistoryList, type TaskHistoryObj } from "@/api/workflow/activiti/task";
import type { ElForm } from "@/api/form";

//外部参数 ################################################
const props = withDefaults(
  defineProps<{
    modelValue: boolean; // 页面展示
    instanceId: string;
  }>(),
  {}
);

//ref对象 ################################################
const dicts = loadDicts("activiti_flow_type");
// 遮罩层
const loading = ref(false);
// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 999,
  instanceId: "",
  actName: "",
  assigneeName: "",
});
// 显示搜索条件
const showSearch = ref(true);
// 总条数
const total = ref(0);
const historyList = ref<TaskHistoryObj[]>([]);

//element ################################################
const queryRef = ref<ElForm>();

//emit ################################################
const emit = defineEmits<{ (event: "update:modelValue", value: boolean): void }>();
//computed ################################################
const visible = computed({
  get() {
    return props.modelValue;
  },
  set(value) {
    emit("update:modelValue", value);
  },
});

//watch ################################################
watch(
  () => props.instanceId,
  (value) => {
    queryParams.value.instanceId = value;
    if (value) {
      handleQuery();
    } else {
      historyList.value = [];
      total.value = 0;
    }
  }
);

//方法 ################################################
/** 搜索按钮操作 */
const getHistoryList = () => {
  loading.value = true;
  queryHistoryList({ ...queryParams.value })
    .then((rsp) => {
      if (rsp.rows) {
        historyList.value = rsp.rows.map((item) => {
          item.actParams && (item.actParamsObj = JSON.parse(item.actParams));
          return item;
        });
      } else {
        historyList.value = rsp.rows || [];
      }
      total.value = rsp.total || 0;
    })
    .finally(() => {
      loading.value = false;
    });
};
/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getHistoryList();
};
/** 重置按钮操作 */
const resetQuery = () => {
  queryRef.value?.resetFields();
  handleQuery();
};
</script>

<style lang="less" scoped></style>
