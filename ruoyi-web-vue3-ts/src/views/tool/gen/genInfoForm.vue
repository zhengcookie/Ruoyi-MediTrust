<template>
  <el-form ref="genInfoForm" :model="info" :rules="rules" label-width="150px">
    <el-row>
      <el-col :span="12">
        <el-form-item prop="tplCategory">
          <template #label>生成模板</template>
          <el-select v-model="genInfo.tplCategory" @change="tplSelectChange">
            <el-option label="单表（增删改查）" value="crud" />
            <el-option label="树表（增删改查）" value="tree" />
            <el-option label="主子表（增删改查）" value="sub" />
          </el-select>
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="tplWebType">
          <template #label>前端类型</template>
          <el-select v-model="genInfo.tplWebType">
            <el-option label="Vue2 Element UI 模版" value="element-ui" />
            <el-option label="Vue3 Element Plus 模版" value="element-plus" />
            <el-option label="Vue3 Element Plus ts 模版" value="element-plus-ts" />
          </el-select>
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="packageName">
          <template #label>
            生成包路径
            <el-tooltip content="生成在哪个java包下，例如 com.ruoyi.system" placement="top">
              <el-icon><question-filled /></el-icon>
            </el-tooltip>
          </template>
          <el-input v-model="genInfo.packageName" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="moduleName">
          <template #label>
            生成模块名
            <el-tooltip content="可理解为子系统名，例如 system" placement="top">
              <el-icon><question-filled /></el-icon>
            </el-tooltip>
          </template>
          <el-input v-model="genInfo.moduleName" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="businessName">
          <template #label>
            生成业务名
            <el-tooltip content="可理解为功能英文名，例如 user" placement="top">
              <el-icon><question-filled /></el-icon>
            </el-tooltip>
          </template>
          <el-input v-model="genInfo.businessName" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="functionName">
          <template #label>
            生成功能名
            <el-tooltip content="用作类描述，例如 用户" placement="top">
              <el-icon><question-filled /></el-icon>
            </el-tooltip>
          </template>
          <el-input v-model="genInfo.functionName" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="genType">
          <template #label>
            生成代码方式
            <el-tooltip content="默认为zip压缩包下载，也可以自定义生成路径" placement="top">
              <el-icon><question-filled /></el-icon>
            </el-tooltip>
          </template>
          <el-radio v-model="genInfo.genType" label="0">zip压缩包</el-radio>
          <el-radio v-model="genInfo.genType" label="1">自定义路径</el-radio>
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item>
          <template #label>
            上级菜单
            <el-tooltip content="分配到指定菜单下，例如 系统管理" placement="top">
              <el-icon><question-filled /></el-icon>
            </el-tooltip>
          </template>
          <el-tree-select
            v-model:value="genInfo.parentMenuId"
            :options="menuOptions"
            :obj-map="{
              value: 'menuId',
              label: 'menuName',
              children: 'children',
            }"
            placeholder="请选择系统菜单"
          />
        </el-form-item>
      </el-col>

      <el-col v-if="genInfo.genType == '1'" :span="24">
        <el-form-item prop="genPath">
          <template #label>
            自定义路径
            <el-tooltip content="填写磁盘绝对路径，若不填写，则生成到当前Web项目下" placement="top">
              <el-icon><question-filled /></el-icon>
            </el-tooltip>
          </template>
          <el-input v-model="genInfo.genPath">
            <template #append>
              <el-dropdown>
                <el-button type="primary">
                  最近路径快速选择
                  <i class="el-icon-arrow-down el-icon--right"></i>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="genInfo.genPath = '/'">恢复默认的生成基础路径</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </el-input>
        </el-form-item>
      </el-col>
    </el-row>

    <template v-if="genInfo.tplCategory == 'tree'">
      <h4 class="form-header">其他信息</h4>
      <el-row v-show="genInfo.tplCategory == 'tree'">
        <el-col :span="12">
          <el-form-item>
            <template #label>
              树编码字段
              <el-tooltip content="树显示的编码字段名， 如：dept_id" placement="top">
                <el-icon><question-filled /></el-icon>
              </el-tooltip>
            </template>
            <el-select v-model="genInfo.treeCode" placeholder="请选择">
              <el-option v-for="(column, index) in genInfo.columns" :key="index" :label="column.columnName + '：' + column.columnComment" :value="column.columnName"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item>
            <template #label>
              树父编码字段
              <el-tooltip content="树显示的父编码字段名， 如：parent_Id" placement="top">
                <el-icon><question-filled /></el-icon>
              </el-tooltip>
            </template>
            <el-select v-model="genInfo.treeParentCode" placeholder="请选择">
              <el-option v-for="(column, index) in genInfo.columns" :key="index" :label="column.columnName + '：' + column.columnComment" :value="column.columnName"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item>
            <template #label>
              树名称字段
              <el-tooltip content="树节点的显示名称字段名， 如：dept_name" placement="top">
                <el-icon><question-filled /></el-icon>
              </el-tooltip>
            </template>
            <el-select v-model="genInfo.treeName" placeholder="请选择">
              <el-option v-for="(column, index) in genInfo.columns" :key="index" :label="column.columnName + '：' + column.columnComment" :value="column.columnName"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </template>

    <template v-if="genInfo.tplCategory == 'sub'">
      <h4 class="form-header">关联信息</h4>
      <el-row>
        <el-col :span="12">
          <el-form-item>
            <template #label>
              关联子表的表名
              <el-tooltip content="关联子表的表名， 如：sys_user" placement="top">
                <el-icon><question-filled /></el-icon>
              </el-tooltip>
            </template>
            <el-select v-model="genInfo.subTableName" placeholder="请选择" @change="subSelectChange">
              <el-option v-for="(table, index) in tables" :key="index" :label="table.tableName + '：' + table.tableComment" :value="table.tableName"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item>
            <template #label>
              子表关联的外键名
              <el-tooltip content="子表关联的外键名， 如：user_id" placement="top">
                <el-icon><question-filled /></el-icon>
              </el-tooltip>
            </template>
            <el-select v-model="genInfo.subTableFkName" placeholder="请选择">
              <el-option v-for="(column, index) in subColumns" :key="index" :label="column.columnName + '：' + column.columnComment" :value="column.columnName"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </template>
  </el-form>
</template>

<script lang="ts" setup name="ToolGenInfoForm">
import { listMenu, type MenuObj } from "@/api/system/menu";
import { ref, watch } from "vue";
import { handleTree } from "@/utils/ruoyi";
import type { GenInfoObj, GenEColumn, GenTColumn } from "@/api/tool/gen";

const props = withDefaults(
  defineProps<{
    info: GenInfoObj;
    tables?: GenTColumn[];
  }>(),
  {
    info: () => {
      return {};
    },
    tables: () => {
      return [];
    },
  }
);

const subColumns = ref<GenEColumn[]>([]);
const menuOptions = ref<MenuObj[]>([]);
const genInfo = ref<GenInfoObj>({});

// 表单校验
const rules = ref({
  tplCategory: [{ required: true, message: "请选择生成模板", trigger: "blur" }],
  packageName: [{ required: true, message: "请输入生成包路径", trigger: "blur" }],
  moduleName: [{ required: true, message: "请输入生成模块名", trigger: "blur" }],
  businessName: [{ required: true, message: "请输入生成业务名", trigger: "blur" }],
  functionName: [{ required: true, message: "请输入生成功能名", trigger: "blur" }],
});

const emit = defineEmits<{ (event: "dataChange", value: GenInfoObj): void }>();

function subSelectChange() {
  genInfo.value.subTableFkName = "";
}
function tplSelectChange(value: string) {
  if (value !== "sub") {
    genInfo.value.subTableName = "";
    genInfo.value.subTableFkName = "";
  }
}
function setSubTableColumns(value: string) {
  for (const item in props.tables) {
    const name = props.tables[item].tableName;
    if (value === name) {
      subColumns.value = props.tables[item].columns || [];
      break;
    }
  }
}
/** 查询菜单下拉树结构 */
function getMenuTreeselect() {
  listMenu().then((response) => {
    response.data && (menuOptions.value = handleTree(response.data, "menuId"));
  });
}

watch(
  () => props.info,
  (val) => {
    Object.assign(genInfo.value, val);
  }
);

watch(
  () => genInfo.value.subTableName,
  (val) => {
    val && setSubTableColumns(val);
  }
);

watch(
  () => genInfo.value.tplWebType,
  (val) => {
    if (val === "") {
      genInfo.value.tplWebType = "element-plus-ts";
    }
  }
);

watch(
  () => genInfo.value,
  (val) => {
    emit("dataChange", val);
  },
  {
    deep: true,
  }
);

getMenuTreeselect();
</script>
