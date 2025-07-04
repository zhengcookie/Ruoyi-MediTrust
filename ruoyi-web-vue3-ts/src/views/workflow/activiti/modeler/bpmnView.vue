<template>
  <div v-loading.fullscreen.lock="loading" class="bpmn-dialog">
    <el-dialog v-model="dialogVisible" :fullscreen="true" draggable @close="close">
      <template #header>
        <el-space>
          <el-button icon="FolderChecked" @click="saveModeler">保存</el-button>
          <el-button icon="Download" @click="downloadProcess('xml')">导出xml</el-button>
          <el-button icon="Download" @click="downloadProcess('bpmn')">导出bpmn</el-button>
          <el-upload ref="uploadRef" :auto-upload="false" :show-file-list="false" :limit="1" accept="text/xml" @change="openProcess">
            <template #trigger>
              <el-button icon="Upload">导入</el-button>
            </template>
          </el-upload>
        </el-space>

        <ul class="icon-btn">
          <li v-for="item in iconBtn1" :key="item.key" :title="item.name" @click="handlerAlign(item)">
            <el-icon>
              <component :is="item.icon"></component>
            </el-icon>
          </li>
        </ul>

        <ul class="icon-btn">
          <li v-for="item in iconBtn2" :key="item.key" :title="item.name" @click="handlerReset(item)">
            <el-icon>
              <component :is="item.icon"></component>
            </el-icon>
          </li>
        </ul>
      </template>
      <splitpanes class="default-theme bpmn-body">
        <pane min-size="20">
          <div ref="canvasRef" class="canvas"></div>
        </pane>
        <!--属性面板将通过该元素呈现到其中-->
        <pane :size="25" min-size="15" style="overflow-y: auto">
          <div id="js-properties-panel" class="panel"></div>
        </pane>
      </splitpanes>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup name="ActDefinitionPage">
import { computed, watch, ref } from "vue";
import { ElMessage, type UploadFile } from "element-plus";
import BpmnModeler from "bpmn-js/lib/Modeler"; // 引入 bpmn-js
import type { ModdleElement } from "bpmn-js/lib/BaseModeler";
// 导入一下有关于bpmn-js的字体库以及样式文件
import "bpmn-js/dist/assets/diagram-js.css";
import "bpmn-js/dist/assets/bpmn-font/css/bpmn.css";
import "bpmn-js/dist/assets/bpmn-font/css/bpmn-codes.css";
import "bpmn-js/dist/assets/bpmn-font/css/bpmn-embedded.css";
// 右侧属性面包样式
import "bpmn-js-properties-panel/dist/assets/properties-panel.css";
import "bpmn-js-properties-panel/dist/assets/element-templates.css";
// 属性面板相关模块
import { BpmnPropertiesPanelModule, BpmnPropertiesProviderModule, CamundaPlatformPropertiesProviderModule } from "bpmn-js-properties-panel";
import CamundaBpmnModdle from "camunda-bpmn-moddle/resources/camunda.json";
// 汉化包
import customTranslate from "../bpmn/i18n";
// 注意这个xml文件
import createBpmnXml, { activitiToCamundaXml, camundaToActivitiXml } from "./xml"; // 放在下面了
// 窗格拆分器插件
import { Splitpanes, Pane } from "splitpanes";
import "splitpanes/dist/splitpanes.css";
import saveAs from "file-saver";
import type CommandStack from "diagram-js/lib/command/CommandStack";
import { getModelerDetail, updateModeler, type ActModelerObj, type XmlMetaInfo } from "@/api/workflow/activiti/modeler";

//外部参数 ################################################
const props = withDefaults(
  defineProps<{
    modelValue: boolean;
    modelId: string;
  }>(),
  {}
);
//ref对象 ################################################
const loading = ref(false);
const iconBtn1 = ref([
  { name: "左对齐", key: "left", icon: "myAlignStartVertical" },
  { name: "水平居中", key: "center", icon: "myAlignCenterVertical" },
  { name: "右对齐", key: "right", icon: "myAlignEndVertical" },
  { name: "上对齐", key: "top", icon: "myAlignStartHorizontal" },
  { name: "垂直居中", key: "middle", icon: "myAlignCenterHorizontal" },
  { name: "下对齐", key: "bottom", icon: "myAlignEndHorizontal" },
]);
const iconBtn2 = ref([
  { name: "撤销", key: "undo", icon: "myUndo" },
  { name: "恢复", key: "redo", icon: "myRedo" },
  { name: "擦除重做", key: "restart", icon: "myEraser" },
]);
//elment ################################################
const canvasRef = ref();
const uploadRef = ref();
//emit
const emit = defineEmits<{ (event: "update:modelValue", value: boolean): void; (event: "close"): void }>();
//内部变量 ################################################
let bpmnModeler: BpmnModeler | undefined = undefined;
let modelData: ActModelerObj = { id: "" };
let bpmnXml: string | undefined = ""; //模型xml

//computed ################################################
const dialogVisible = computed({
  get() {
    return props.modelValue;
  },
  set(value) {
    emit("update:modelValue", value);
  },
});

// watch ################################################
watch(dialogVisible, (value) => {
  if (value) {
    refreshModelerData();
  }
});

// Function ################################################
/**
 * 获取BpmnModeler
 */
const getBpmnModeler = () => {
  if (bpmnModeler) {
    return bpmnModeler;
  }

  // 将汉化包装成一个模块
  const customTranslateModule = {
    translate: ["value", customTranslate],
  };
  // 生成实例
  bpmnModeler = new BpmnModeler({
    container: canvasRef.value, // 获取到属性ref为“canvasRef”的dom节点
    propertiesPanel: {
      parent: "#js-properties-panel",
    },
    additionalModules: [
      BpmnPropertiesPanelModule,
      BpmnPropertiesProviderModule,
      CamundaPlatformPropertiesProviderModule,
      customTranslateModule, //  汉化模块
    ],
    moddleExtensions: {
      camunda: CamundaBpmnModdle,
    },
  });
  return bpmnModeler;
};

/**刷新视图数据 */
const refreshModelerData = () => {
  loading.value = true;
  getModelerDetail(props.modelId)
    .then(({ data }) => {
      data && (modelData = data);
      bpmnXml = data?.bpmnXml ? data?.bpmnXml : createBpmnXmlByMetaInfo();
      bpmnXml = activitiToCamundaXml(bpmnXml);
      loadBpmnXml(bpmnXml);
    })
    .finally(() => {
      loading.value = false;
    });
};

/**
 * 对齐操作
 */
const handlerAlign = ({ key }: { key: string }) => {
  if (bpmnModeler) {
    const modeling = bpmnModeler.get("modeling");
    const selection = bpmnModeler.get<{ get: () => ModdleElement[] }>("selection");
    const align = bpmnModeler.get<ModdleElement>("alignElements");

    if (modeling && selection) {
      const SelectedElements = selection.get();
      if (!SelectedElements || SelectedElements.length <= 1) {
        return ElMessage.warning("请按住 Shift 键选择多个元素对齐");
      }
      align.trigger(SelectedElements, key);
    }
  }
};

/**
 * 重置操作
 */
const handlerReset = ({ key }: { key: string }) => {
  if (bpmnModeler) {
    const command = bpmnModeler.get<CommandStack>("commandStack");
    switch (key) {
      case "undo":
        command && command.canUndo() && command.undo();
        break;
      case "redo":
        command && command.canRedo() && command.redo();
        break;
      case "restart":
        command && command.clear();
        bpmnXml = createBpmnXmlByMetaInfo();
        loadBpmnXml(bpmnXml);
        break;
    }
  }
};

/**
 * 创建一个新的流程文件xml
 */
const createBpmnXmlByMetaInfo = () => {
  const metaInfo = modelData.metaInfo ? (JSON.parse(modelData.metaInfo) as XmlMetaInfo) : {};
  const param = {
    key: modelData.key,
    name: modelData.name,
    description: metaInfo?.description,
    version: metaInfo?.version,
  };
  return createBpmnXml(param);
};

/**
 * 打开文件
 */
const openProcess = (rawFile: UploadFile) => {
  if (!rawFile.raw) {
    return false;
  }

  loading.value = true;
  const reader = new FileReader();
  reader.readAsText(rawFile.raw, "utf8");
  reader.onload = () => {
    if (typeof reader.result === "string") {
      if (reader.result.indexOf("bpmn2:definitions") <= 0) {
        ElMessage.error("请选择bpmn20标准的流程文件！");
        loading.value = false;
      } else {
        const modeler = getBpmnModeler();
        modeler
          .importXML(reader.result)
          .then(({ warnings }) => {
            warnings && warnings.length > 0 && console.warn(warnings);
            warnings && warnings.length > 0 && ElMessage.warning(warnings.toString());
          })
          .finally(() => {
            loading.value = false;
          });
      }
    } else {
      loading.value = false;
    }
    uploadRef.value.clearFiles();
  };
};
/**
 * 加载bpmn.xml配置文件
 */
const loadBpmnXml = (xml: string) => {
  loading.value = true;
  const bpmnXml = xml;
  const modeler = getBpmnModeler();
  modeler
    .importXML(bpmnXml)
    .then(({ warnings }) => {
      warnings && warnings.length > 0 && ElMessage.warning(warnings.toString());
    })
    .finally(() => {
      loading.value = false;
    });
};

/**
 * 保存模型
 */
const saveModeler = () => {
  loading.value = true;
  if (!bpmnModeler) {
    return;
  }
  // 读取xml、svg
  Promise.all([bpmnModeler.saveXML({}), bpmnModeler.saveSVG()])
    .then(([rsp1, rsp2]) => {
      const { error, xml } = rsp1;
      const { svg } = rsp2;
      if (error) {
        return { code: 500, msg: error.message };
      }
      if (!xml) {
        return { code: 500, msg: "xml文件不存在！" };
      }
      if (!svg) {
        return { code: 500, msg: "svg文件不存在！" };
      }
      return updateModeler({
        id: modelData.id,
        name: modelData.name,
        description: modelData.description,
        bpmnXml: camundaToActivitiXml(xml),
        svgXml: svg,
      });
    })
    .then((rsp) => {
      if (rsp.code === 200) {
        ElMessage.success("保存成功");
      } else {
        ElMessage.success(rsp.msg);
      }
    })
    .finally(() => {
      loading.value = false;
    });
};

// 下载流程图到本地
/**
 * @param {string} type
 * @param {string} name
 */
const downloadProcess = (type = "xml", name = "diagram") => {
  loading.value = true;
  bpmnModeler &&
    bpmnModeler
      .saveXML({})
      .then(({ error, xml }) => {
        // 读取异常时抛出异常
        if (error || !xml) {
          console.error(`[Process Designer Warn ]:`, error);
        } else {
          const blob = new Blob([camundaToActivitiXml(xml)], {
            type: type === "xml" ? "text/xml" : "bpmn20-xml",
          });
          saveAs(blob, `${name}.${type}`);
        }
      })
      .finally(() => {
        loading.value = false;
      });
};

const close = () => {
  emit("close");
};
</script>

<style lang="less" scoped>
.bpmn-dialog {
  :deep(.el-dialog__body) {
    padding: 0;
    width: 100%;
    height: calc(100vh - 65px);
    overflow-y: auto;
  }

  .bpmn-body {
    width: 100%;
    height: 100%;
    min-height: 600px;

    .canvas {
      width: 100%;
      height: 100%;
      background: url("data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGRlZnM+PHBhdHRlcm4gaWQ9ImEiIHdpZHRoPSI0MCIgaGVpZ2h0PSI0MCIgcGF0dGVyblVuaXRzPSJ1c2VyU3BhY2VPblVzZSI+PHBhdGggZD0iTTAgMTBoNDBNMTAgMHY0ME0wIDIwaDQwTTIwIDB2NDBNMCAzMGg0ME0zMCAwdjQwIiBmaWxsPSJub25lIiBzdHJva2U9IiNlMGUwZTAiIG9wYWNpdHk9Ii4yIi8+PHBhdGggZD0iTTQwIDBIMHY0MCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjZTBlMGUwIi8+PC9wYXR0ZXJuPjwvZGVmcz48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSJ1cmwoI2EpIi8+PC9zdmc+")
        repeat !important;
    }

    .panel {
      width: 100%;
    }
  }
}

.icon-btn {
  margin: 0;
  padding: 0;
  display: inline-flex;
  margin-left: 30px;

  li {
    border-left: 1px solid var(--el-border-color);
    border-top: 1px solid var(--el-border-color);
    border-bottom: 1px solid var(--el-border-color);
    color: var(--el-text-color-regular);
    list-style: none;
    font-size: 24px;
    text-align: center;
    cursor: pointer;
    padding: 3px 8px 0 8px;
    height: 32px;
  }

  li:hover {
    background-color: #ecf5ff;
  }

  li:first-child {
    border-top-left-radius: 4px;
    border-bottom-left-radius: 4px;
  }

  li:last-child {
    border-top-right-radius: 4px;
    border-bottom-right-radius: 4px;
    border-right: 1px solid var(--el-border-color);
  }

  .icon-popper {
    width: 60px;
  }
}
</style>
