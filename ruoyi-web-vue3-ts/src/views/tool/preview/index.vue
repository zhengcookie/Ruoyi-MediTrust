<template>
  <div class="app-container">
    <el-row :gutter="24">
      <el-col :xs="12" :sm="12" :md="12" :lg="12" :xl="12">
        相关文档：
        <a href="https://501351981.github.io/vue-office/examples/docs/guide/" target="_blank">vue-office</a>
      </el-col>
    </el-row>

    <el-divider content-position="left">Preview Word</el-divider>

    <el-space>
      <el-button type="primary" @click="view('word')">PreviewWord</el-button>
      <el-button type="primary" @click="view('excel')">PreviewExcel</el-button>
      <el-button type="primary" @click="view('pdf')">PreviewPdf</el-button>
    </el-space>
    <el-dialog v-model="dialogVisible" :title="'Preview ' + fileType" :fullscreen="true" draggable class="preview-dialog">
      <vue-office-docx v-if="fileType === 'word'" :key="'word'" :src="word" style="height: 100%" @rendered="renderedHandler" @error="errorHandler" />
      <vue-office-excel v-else-if="fileType === 'excel'" :key="'excel'" :src="excel" style="height: 100%" @rendered="renderedHandler" @error="errorHandler" />
      <vue-office-pdf v-else-if="fileType === 'pdf'" :key="'pdf'" :src="pdf" style="height: 100%" @rendered="renderedHandler" @error="errorHandler" />
    </el-dialog>
  </div>
</template>
<script lang="ts" setup name="ToolPreivewPage">
//引入VueOfficeDocx组件
import VueOfficeDocx from "@vue-office/docx";
//引入相关样式
import "@vue-office/docx/lib/index.css";
//引入VueOfficeExcel组件
import VueOfficeExcel from "@vue-office/excel";
//引入相关样式
import "@vue-office/excel/lib/index.css";
//引入VueOfficePdf组件
import VueOfficePdf from "@vue-office/pdf";

import { ref } from "vue";

const dialogVisible = ref(false);
const fileType = ref("");
const word = ref("/word/test.docx"); //设置文档网络地址，可以是相对地址
const excel = ref("/excel/test.xlsx"); //设置文档网络地址，可以是相对地址
const pdf = ref("/pdf/test.pdf"); //设置文档网络地址，可以是相对地址

const renderedHandler = () => {
  console.log("渲染完成");
};
const errorHandler = () => {
  console.log("渲染失败");
};
const view = (type: string) => {
  dialogVisible.value = true;
  fileType.value = type;
};
</script>
<style lang="less" scoped>
:deep(.preview-dialog) {
  .el-dialog__body {
    padding: 0;
    height: calc(100vh - 54px);
    overflow-y: auto;
  }
}
</style>
