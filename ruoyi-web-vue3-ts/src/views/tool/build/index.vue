<template>
  <div class="app-container">
    <el-row :gutter="24">
      <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
        相关文档：
        <a href="https://www.form-create.com/v3/guide/" target="_blank">form-create v3</a>
        ;
        <a href="https://github.com/xaboy/form-create-designer" target="_blank">form-create-designer</a>
      </el-col>
    </el-row>

    <el-divider content-position="left">表单构建</el-divider>
    <el-button type="primary" @click="dialogVisible = true">构建</el-button>
    <el-dialog v-model="dialogVisible" title="formCreate" :fullscreen="true" draggable class="form-create-dialog">
      <fc-designer></fc-designer>
    </el-dialog>
  </div>
</template>
<script lang="ts" setup name="ToolBuildPage">
// import En from "@form-create/designer/locale/en.js";
// import ZhCn from "@/i18n/formCreate/zh-cn";
// import En from "@/i18n/formCreate/en";
import { ref } from "vue";

// const locale = ref(ZhCn);
const dialogVisible = ref(false);
</script>

<style lang="less" scoped>
:deep(.form-create-dialog) {
  .el-dialog__body {
    padding: 0;
    height: calc(100vh - 54px);
    overflow-y: auto;
  }
}
</style>
