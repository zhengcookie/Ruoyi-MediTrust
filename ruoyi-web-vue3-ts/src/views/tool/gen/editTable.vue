<template>
  <el-card>
    <el-tabs v-model="activeName">
      <el-tab-pane label="基本信息" name="basic">
        <basic-info-form ref="basicInfoRef" :info="info" />
      </el-tab-pane>
      <el-tab-pane label="字段信息" name="columnInfo">
        <el-table ref="dragTable" :data="columns" row-key="columnId" :max-height="tableHeight">
          <el-table-column label="序号" type="index" min-width="5%" />
          <el-table-column label="字段列名" prop="columnName" min-width="10%" :show-overflow-tooltip="true" />
          <el-table-column label="字段描述" min-width="10%">
            <template #default="scope">
              <el-input v-model="scope.row.columnComment"></el-input>
            </template>
          </el-table-column>
          <el-table-column label="物理类型" prop="columnType" min-width="10%" :show-overflow-tooltip="true" />
          <el-table-column label="Java类型" min-width="11%">
            <template #default="scope">
              <el-select v-model="scope.row.javaType">
                <el-option label="Long" value="Long" />
                <el-option label="String" value="String" />
                <el-option label="Integer" value="Integer" />
                <el-option label="Double" value="Double" />
                <el-option label="BigDecimal" value="BigDecimal" />
                <el-option label="Date" value="Date" />
                <el-option label="Boolean" value="Boolean" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="java属性" min-width="10%">
            <template #default="scope">
              <el-input v-model="scope.row.javaField"></el-input>
            </template>
          </el-table-column>

          <el-table-column label="插入" min-width="5%">
            <template #default="scope">
              <el-checkbox v-model="scope.row.isInsert" true-label="1" false-label="0"></el-checkbox>
            </template>
          </el-table-column>
          <el-table-column label="编辑" min-width="5%">
            <template #default="scope">
              <el-checkbox v-model="scope.row.isEdit" true-label="1" false-label="0"></el-checkbox>
            </template>
          </el-table-column>
          <el-table-column label="列表" min-width="5%">
            <template #default="scope">
              <el-checkbox v-model="scope.row.isList" true-label="1" false-label="0"></el-checkbox>
            </template>
          </el-table-column>
          <el-table-column label="查询" min-width="5%">
            <template #default="scope">
              <el-checkbox v-model="scope.row.isQuery" true-label="1" false-label="0"></el-checkbox>
            </template>
          </el-table-column>
          <el-table-column label="查询方式" min-width="10%">
            <template #default="scope">
              <el-select v-model="scope.row.queryType">
                <el-option label="=" value="EQ" />
                <el-option label="!=" value="NE" />
                <el-option label=">" value="GT" />
                <el-option label=">=" value="GTE" />
                <el-option label="<" value="LT" />
                <el-option label="<=" value="LTE" />
                <el-option label="LIKE" value="LIKE" />
                <el-option label="BETWEEN" value="BETWEEN" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="必填" min-width="5%">
            <template #default="scope">
              <el-checkbox v-model="scope.row.isRequired" true-label="1" false-label="0"></el-checkbox>
            </template>
          </el-table-column>
          <el-table-column label="显示类型" min-width="12%">
            <template #default="scope">
              <el-select v-model="scope.row.htmlType">
                <el-option label="文本框" value="input" />
                <el-option label="文本域" value="textarea" />
                <el-option label="下拉框" value="select" />
                <el-option label="单选框" value="radio" />
                <el-option label="复选框" value="checkbox" />
                <el-option label="日期控件" value="datetime" />
                <el-option label="图片上传" value="imageUpload" />
                <el-option label="文件上传" value="fileUpload" />
                <el-option label="富文本控件" value="editor" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="字典类型" min-width="12%">
            <template #default="scope">
              <el-select v-model="scope.row.dictType" clearable filterable placeholder="请选择">
                <el-option v-for="dict in dictOptions" :key="dict.dictType" :label="dict.dictName" :value="dict.dictType">
                  <span style="float: left">{{ dict.dictName }}</span>
                  <span style="float: right; color: #8492a6; font-size: 13px">{{ dict.dictType }}</span>
                </el-option>
              </el-select>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="生成信息" name="genInfo">
        <gen-info-form ref="genInfoRef" :info="info" :tables="tables" @data-change="genInfoChange" />
      </el-tab-pane>
    </el-tabs>
    <el-form label-width="100px">
      <div style="text-align: center; margin-left: -100px; margin-top: 10px">
        <el-button type="primary" @click="submitForm()">提交</el-button>
        <el-button @click="close()">返回</el-button>
      </div>
    </el-form>
  </el-card>
</template>

<script lang="ts" setup name="ToolGenEdit">
import { getGenTable, updateGenTable, type GenInfoObj, type GenEColumn, type GenTColumn } from "@/api/tool/gen";
import { optionselect as getDictOptionselect, type DictTypeObj } from "@/api/system/dict/type";
import basicInfoForm from "./basicInfoForm.vue";
import genInfoForm from "./genInfoForm.vue";
import { useRoute } from "vue-router";
import { onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import type { ElForm } from "@/api/form";
import tagsStore from "@/stores/tagsView";

const tStore = tagsStore();

const route = useRoute();

const activeName = ref("columnInfo");
const tableHeight = ref(document.documentElement.scrollHeight - 245 + "px");
const tables = ref<GenTColumn[]>([]);
const columns = ref<GenEColumn[]>([]);
const dictOptions = ref<DictTypeObj[]>([]);
const info = ref<GenInfoObj>({});
const basicInfoRef = ref();
const genInfoRef = ref();
let genInfo: GenInfoObj = {};

/**
 * genInfo数据改变
 * @param {GenInfoObj} data
 */
function genInfoChange(data: GenInfoObj) {
  genInfo = data;
}

/** 提交按钮 */
function submitForm() {
  const basicForm = basicInfoRef.value.$refs.basicInfoForm as ElForm;
  const genForm = genInfoRef.value.$refs.genInfoForm as ElForm;
  Promise.all([basicForm.validate(), genForm.validate()]).then((res) => {
    const validateResult = res.every((item: boolean) => !!item);
    if (validateResult) {
      const genTable = Object.assign({}, genInfo);
      genTable.columns = columns.value;
      genTable.params = {
        treeCode: genInfo.treeCode,
        treeName: genInfo.treeName,
        treeParentCode: genInfo.treeParentCode,
        parentMenuId: genInfo.parentMenuId,
      };
      updateGenTable(genTable).then((res) => {
        ElMessage.success(res.msg);
        if (res.code === 200) {
          close();
        }
      });
    } else {
      ElMessage.error("表单校验未通过，请重新检查提交内容");
    }
  });
}

function close() {
  tStore.closeOpenPage("/tool/gen");
}

onMounted(() => {
  const tableId = route.params && route.params.tableId;
  if (typeof tableId === "string") {
    // 获取表详细信息
    getGenTable(tableId).then((res) => {
      columns.value = res.data?.rows || [];
      info.value = res.data?.info || {};
      tables.value = res.data?.tables || [];
    });
    /** 查询字典下拉列表 */
    getDictOptionselect().then((response) => {
      dictOptions.value = response.data || [];
    });
  }
});
</script>
