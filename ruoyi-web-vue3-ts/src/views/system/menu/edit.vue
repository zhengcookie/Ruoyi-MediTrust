<template>
  <!-- 添加或修改菜单对话框 -->
  <el-dialog v-model="open" :title="title" width="680px" append-to-body>
    <el-form ref="menuRef" v-loading="loading" :model="form" :rules="rules" label-width="100px">
      <el-row>
        <el-col :span="24">
          <el-form-item label="上级菜单">
            <el-tree-select
              v-model="form.parentId"
              :data="menuOptions"
              :props="{
                value: 'menuId',
                label: 'menuName',
                children: 'children',
              }"
              value-key="menuId"
              placeholder="选择上级菜单"
              check-strictly
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="菜单类型" prop="menuType">
            <el-radio-group v-model="form.menuType" @change="menuTypeChange">
              <el-radio label="M">目录</el-radio>
              <el-radio label="C">菜单</el-radio>
              <el-radio label="F">按钮</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col v-if="form.menuType != 'F'" :span="24">
          <el-form-item label="菜单图标" prop="icon">
            <el-icon-picker v-model="form.icon"></el-icon-picker>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="菜单名称" prop="menuName">
            <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="显示排序" prop="orderNum">
            <el-input-number v-model="form.orderNum" controls-position="right" :min="0" />
          </el-form-item>
        </el-col>
        <el-col v-if="form.menuType == 'C'" :span="12">
          <el-form-item>
            <template #label>
              <span>
                <el-tooltip content="选择是外链则路由地址需要以`http(s)://`开头" placement="top">
                  <el-icon><question-filled /></el-icon>
                </el-tooltip>
                是否外链
              </span>
            </template>
            <el-radio-group v-model="form.isFrame">
              <el-radio label="0">是</el-radio>
              <el-radio label="1">否</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col v-if="form.menuType != 'F'" :span="12">
          <el-form-item prop="path">
            <template #label>
              <span>
                <el-tooltip content="访问的路由地址，如：`user`" placement="top">
                  <el-icon><question-filled /></el-icon>
                </el-tooltip>
                路由地址
              </span>
            </template>
            <el-input v-model="form.path" placeholder="请输入路由地址" />
          </el-form-item>
        </el-col>
        <el-col v-if="form.menuType != 'F'" :span="12">
          <el-form-item prop="component">
            <template #label>
              <span>
                <el-tooltip :content="form.isFrame == '1' ? '访问的组件路径，如：`system/user/index`，默认在`views`目录下' : '外网地址需以`http(s)://`开头'" placement="top">
                  <el-icon><question-filled /></el-icon>
                </el-tooltip>
                {{ form.isFrame == "1" ? "组件路径" : "外部链接" }}
              </span>
            </template>
            <el-input v-model="form.component" :placeholder="form.isFrame == '1' ? '请输入组件路径' : '请输入外部链接'" />
          </el-form-item>
        </el-col>
        <el-col v-if="form.menuType != 'M'" :span="12">
          <el-form-item>
            <el-input v-model="form.perms" placeholder="请输入权限标识" maxlength="100" />
            <template #label>
              <span>
                <el-tooltip content="控制器中定义的权限字符，如：@PreAuthorize(`@ss.hasPermi('system:user:list')`)" placement="top">
                  <el-icon><question-filled /></el-icon>
                </el-tooltip>
                权限字符
              </span>
            </template>
          </el-form-item>
        </el-col>
        <el-col v-if="form.menuType == 'C'" :span="12">
          <el-form-item>
            <el-input v-model="form.query" placeholder="请输入路由参数" maxlength="255" />
            <template #label>
              <span>
                <el-tooltip content='访问路由的默认传递参数，如：`{"id": 1, "name": "ry"}`' placement="top">
                  <el-icon><question-filled /></el-icon>
                </el-tooltip>
                路由参数
              </span>
            </template>
          </el-form-item>
        </el-col>
        <el-col v-if="form.menuType == 'C'" :span="12">
          <el-form-item>
            <template #label>
              <span>
                <el-tooltip content="选择是则会被`keep-alive`缓存，需要匹配组件的`name`和地址保持一致" placement="top">
                  <el-icon><question-filled /></el-icon>
                </el-tooltip>
                是否缓存
              </span>
            </template>
            <el-radio-group v-model="form.isCache">
              <el-radio label="0">缓存</el-radio>
              <el-radio label="1">不缓存</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col v-if="form.menuType != 'F'" :span="12">
          <el-form-item>
            <template #label>
              <span>
                <el-tooltip content="选择隐藏则路由将不会出现在侧边栏，但仍然可以访问" placement="top">
                  <el-icon><question-filled /></el-icon>
                </el-tooltip>
                显示状态
              </span>
            </template>
            <el-radio-group v-model="form.visible">
              <el-radio v-for="dict in dicts.sys_show_hide" :key="dict.value" :label="dict.value">{{ dict.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item>
            <template #label>
              <span>
                <el-tooltip content="选择停用则路由将不会出现在侧边栏，也不能被访问" placement="top">
                  <el-icon><question-filled /></el-icon>
                </el-tooltip>
                菜单状态
              </span>
            </template>
            <el-radio-group v-model="form.status">
              <el-radio v-for="dict in dicts.sys_normal_disable" :key="dict.value" :label="dict.value">{{ dict.label }}</el-radio>
            </el-radio-group>
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
</template>

<script lang="ts" setup name="SysMenuEdit">
import { addMenu, getMenu, listMenu, updateMenu, type MenuObj } from "@/api/system/menu";
import ElIconPicker from "@/components/ElIconPicker/index.vue";
import { loadDicts } from "@/utils/dict";
import { ref } from "vue";
import { handleTree } from "@/utils/ruoyi";
import { ElMessage } from "element-plus";
import type { ElForm } from "@/api/form";

// 外部参数 ####################################################
// const props = withDefaults(defineProps<{
//   value: boolean
// }>(), {
//   value: false
// })

// 内部变量 #####################################################
// 数据字典
const dicts = loadDicts(["sys_show_hide", "sys_normal_disable"]);

const open = ref(false);
const loading = ref(true);
const title = ref("");
const action = ref("add");
const menuOptions = ref<MenuObj[]>([]);

const form = ref<MenuObj>({ menuId: "" });
const rules = ref({
  menuName: [{ required: true, message: "菜单名称不能为空", trigger: "blur" }],
  orderNum: [{ required: true, message: "菜单顺序不能为空", trigger: "blur" }],
  path: [{ required: true, message: "路由地址不能为空", trigger: "blur" }],
});
// ref 元素
const menuRef = ref<ElForm>();

// emit事件 #####################################################
const emit = defineEmits<{
  (event: "refreshData"): void;
}>();

// function #####################################################
/**
 * 显示编辑窗口
 * @param options
 */
function show(options: { action: "add" | "edit"; parentId?: string; menuId?: string }) {
  reset();
  open.value = true;

  if (options.action === "edit" && options.menuId) {
    action.value = "edit";
    title.value = "修改菜单";
    const id = options.menuId;
    getTreeselect().then(() => {
      getMenuDetail(id);
    });
  } else {
    action.value = "add";
    title.value = "新增菜单";
    getTreeselect().then(() => {
      form.value.parentId = options.parentId ? options.parentId : "0";
    });
  }
}

/** 查询菜单下拉树结构 */
function getTreeselect() {
  menuOptions.value = [];
  loading.value = true;
  return listMenu()
    .then((response) => {
      const menu: MenuObj = { menuId: "0", menuName: "主类目", children: [] };
      menu.children = handleTree(response.data || [], "menuId");
      menuOptions.value.push(menu);
      return menu;
    })
    .finally(() => {
      loading.value = false;
    });
}

function menuTypeChange() {
  if (form.value.menuType == "M" || form.value.menuType == "F") {
    form.value.isFrame = "1";
  }
}
/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}
/** 表单重置 */
function reset() {
  form.value = {
    menuId: "",
    parentId: "0",
    menuName: undefined,
    icon: undefined,
    menuType: "M",
    orderNum: undefined,
    isFrame: "1",
    isCache: "0",
    visible: "0",
    status: "0",
  };
  menuRef.value?.resetFields();
}

/** 查询菜单详情 */
function getMenuDetail(menuId: string) {
  loading.value = true;
  getMenu(menuId)
    .then((response) => {
      response.data && (form.value = response.data);
      // id后台为number类型，0的场合显示有问题（本来想将所有id换成uuid，暂时没得时间，就现在这样吧）
      // form.value.parentId = form.value.parentId?.toString();
    })
    .finally(() => {
      loading.value = false;
    });
}

/** 提交按钮 */
function submitForm() {
  menuRef.value?.validate((valid) => {
    if (valid) {
      if (action.value === "edit") {
        updateMenu(form.value).then(() => {
          ElMessage.success("修改成功");
          open.value = false;
          emit("refreshData");
        });
      } else {
        addMenu(form.value).then(() => {
          ElMessage.success("新增成功");
          open.value = false;
          emit("refreshData");
        });
      }
    }
  });
}

defineExpose({
  show,
});
</script>
