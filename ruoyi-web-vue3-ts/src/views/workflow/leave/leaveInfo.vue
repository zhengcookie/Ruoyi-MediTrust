<template>
  <el-form ref="formRef" :model="form" label-width="100px">
    <el-form-item label="请假类型" prop="type">
      <el-select v-model="form.type" disabled>
        <el-option v-for="dict in dicts.activiti_leave_type" :key="dict.dictValue" :label="dict.dictLabel" :value="dict.dictValue"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="标题" prop="title">
      <el-input v-model="form.title" disabled />
    </el-form-item>
    <el-form-item label="原因" prop="reason">
      <el-input v-model="form.reason" type="textarea" disabled />
    </el-form-item>
    <el-form-item label="开始时间" prop="leaveStartTime">
      <el-date-picker v-model="form.leaveStartTime" disabled type="date" value-format="YYYY-MM-DD"></el-date-picker>
    </el-form-item>
    <el-form-item label="结束时间" prop="leaveEndTime">
      <el-date-picker v-model="form.leaveEndTime" disabled type="date" value-format="YYYY-MM-DD"></el-date-picker>
    </el-form-item>
  </el-form>
</template>

<script lang="ts" setup name="TaskLeaveInfo">
import { getLeave, type LeaveObj } from "@/api/workflow/leave";
import { loadDicts } from "@/utils/dict";
import { watch, ref } from "vue";

//外部参数 ################################################
const props = withDefaults(
  defineProps<{
    businessKey: string;
  }>(),
  {}
);
//ref对象 ################################################
//字典数据
const dicts = loadDicts(["activiti_leave_type", "activiti_flow_type"]);
// 遮罩层
const loading = ref(true);
// 表单参数
const form = ref<LeaveObj>({
  id: "",
  type: undefined,
  title: undefined,
  reason: undefined,
  leaveStartTime: undefined,
  leaveEndTime: undefined,
  instanceId: undefined,
  state: undefined,
});

// watch ################################################
watch(
  () => props.businessKey,
  (val) => {
    val && getDatail();
  }
);

// Function ################################################
/** 查询请假列表 */
const getDatail = () => {
  loading.value = true;
  // 此处通过businessKey查询， 或者可以通过instanceId查询
  getLeave(props.businessKey)
    .then((rsp) => {
      rsp.data && (form.value = rsp.data);
    })
    .finally(() => {
      loading.value = false;
    });
};
</script>
