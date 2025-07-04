<template>
  <div v-loading.fullscreen.lock="loading" v-element-visibility="onElementVisibility">
    <div ref="canvasRef" class="canvas"></div>
  </div>
</template>

<script lang="ts" setup name="ProcessViewer">
import { vElementVisibility } from "@vueuse/components";
import { ref, watch, onBeforeUnmount } from "vue";
import BpmnViewer from "bpmn-js/lib/Viewer";
// 导入一下有关于bpmn-js的字体库以及样式文件
import "bpmn-js/dist/assets/diagram-js.css";
import "bpmn-js/dist/assets/bpmn-font/css/bpmn.css";
import "bpmn-js/dist/assets/bpmn-font/css/bpmn-codes.css";
import "bpmn-js/dist/assets/bpmn-font/css/bpmn-embedded.css";
import type { Canvas } from "diagram-js/lib/features/palette/Palette";
import type { Element as BpmnElement } from "diagram-js/lib/model";
import { queryViewerData, type TaskHistoryObj } from "@/api/workflow/activiti/task";
import createMarker from "./marker";
import type EventBus from "diagram-js/lib/core/EventBus";
import type Overlays from "diagram-js/lib/features/overlays/overlays";
import { ElMessage } from "element-plus";

//外部参数 ################################################
const props = withDefaults(
  defineProps<{
    instanceId: string; // 流程实例id
    procDefId: string; // 流程定义id
  }>(),
  {}
);

//ref对象 ################################################
// const dicts = loadDicts(["bpm_process_instance_result"]);
const loading = ref(false);

//elment ################################################
const canvasRef = ref();

//内部变量 ################################################
let bpmnViewer: BpmnViewer | undefined = undefined;
let bpmnXml = "";
let actList: TaskHistoryObj[] = [];
let overlays: Overlays | undefined = undefined;

//watch ################################################
// 发生变化时，删除流程图数据
watch(
  () => [props.instanceId, props.procDefId],
  () => {
    bpmnXml = "";
    actList = [];
  }
);

//function ################################################
/**
 * 获取视图对象
 */
const getBpmnModeler = () => {
  if (!bpmnViewer) {
    //初始化视图对象
    bpmnViewer = new BpmnViewer({
      container: canvasRef.value,
      bpmnRenderer: {},
    });
    //初始化监听事件
    initModelListeners();
  }
  return bpmnViewer;
};

/**
 * 当组件显示，重新渲染
 */
const onElementVisibility = (visible: boolean) => {
  visible && queryData();
};

/**
 * 查询流程图相关数据, 并渲染
 */
const queryData = () => {
  if (bpmnXml) {
    drawProcessView();
  } else {
    queryViewerData({
      instanceId: props.instanceId,
      procDefId: props.procDefId,
    }).then((rsp) => {
      bpmnXml = rsp.data?.bpmnXml || "";
      actList = rsp.data?.actList || [];
      drawProcessView();
    });
  }
};

/**
 * 加载xml流程图
 */
const drawProcessView = () => {
  if (!bpmnXml) return;

  // 加载xml流程图
  const viewer = getBpmnModeler();
  viewer.importXML(bpmnXml).then(({ warnings }) => {
    if (warnings && warnings.length > 0) {
      ElMessage.warning(warnings.toString());
      console.warn(warnings);
    }

    const canvas = viewer.get<Canvas>("canvas");
    const defs = canvas.getContainer().getElementsByTagName<"svg">("svg")[0].getElementsByTagName<"defs">("defs")[0];
    //创建流程图svg标记对象(如：箭头)
    createMarker(defs);
    //高亮流程图
    highlightDiagram();
    //视图大小自适应
    canvas.zoom("fit-viewport");
  });
};

/**
 * TODO 以下需要根据自己实际的业务情况来处理
 * 高亮流程图
 */
const highlightDiagram = () => {
  if (actList.length === 0) return;

  // 在此基础上，增加不同审批结果的颜色等等
  const viewer = getBpmnModeler();
  const canvas = viewer.get<Canvas>("canvas");
  const elements = viewer.getDefinitions().rootElements[0].flowElements;
  elements?.forEach((element: BpmnElement) => {
    const activity = actList.find((m) => m.actKey === element.id); // 找到对应的活动

    // 用户任务 ##############################################
    if (element.$type === "bpmn:UserTask" && activity) {
      const status = activity?.status ? activity?.status : "0"; //0-未处理, 1-同意，2-拒绝，3-取消
      const color = getResultCss(status);
      // 设置节点颜色
      canvas.addMarker(element.id, color);

      // 处理 outgoing 出线
      if (status === "0") return;
      const outgoing = getActivityOutgoing(activity.actKey);
      outgoing?.forEach((outLine) => {
        //无条件往线【bpmn:SequenceFlow】
        if (outLine.conditionExpression?.$type == "bpmn:SequenceFlow") {
          canvas.addMarker(outLine.id, color);
          canvas.findRoot(outLine.id + "_label") && canvas.addMarker(outLine.id + "_label", color); // 高亮连线（文字）
          return;
        }
        //有条件往线【bpmn:FormalExpression】
        if (outLine.conditionExpression?.$type == "bpmn:FormalExpression" && outLine.conditionExpression?.body) {
          if (activity.actParams && executeConditionExpression(activity.actParams, outLine.conditionExpression?.body)) {
            canvas.addMarker(outLine.id, color);
            canvas.findRoot(outLine.id + "_label") && canvas.addMarker(outLine.id + "_label", color); // 高亮连线（文字）
            canvas.addMarker(outLine.id, "start-marker"); //条件连线
          }
          return;
        }
      });
      return;
    }

    // 排它网关 ##############################################
    if (element.$type === "bpmn:ExclusiveGateway" && activity) {
      const color = "highlight";
      // 设置【bpmn:ExclusiveGateway】排它网关的高亮
      canvas.addMarker(element.id, color); // 高亮节点（自己）
      canvas.findRoot(element.id + "_label") && canvas.addMarker(element.id + "_label", color); // 高亮节点（文字）
      // 设置往线的高亮
      element.outgoing?.forEach((outLine) => {
        if (outLine.conditionExpression?.body) {
          if (activity.actParams && executeConditionExpression(activity.actParams, outLine.conditionExpression?.body)) {
            canvas.addMarker(outLine.id, color);
            canvas.findRoot(outLine.id + "_label") && canvas.addMarker(outLine.id + "_label", color); // 高亮连线（文字）
          }
        }
      });
      return;
    }

    // 并行网关 ##############################################
    if (element.$type === "bpmn:ParallelGateway" && activity) {
      const color = "highlight";

      // 网关来线任务是否都已经完成
      let complete = true;
      element.incoming?.forEach((inLine) => {
        const targetActivity = actList.find((m) => m.actKey === inLine.targetRef.id);
        if (!targetActivity?.status || ["1", "2", "3"].indexOf(targetActivity.status) === -1) {
          complete = false;
        }
      });

      if (complete) {
        // 设置【bpmn:ParallelGateway】并行网关的高亮
        canvas.addMarker(element.id, color);
        canvas.findRoot(element.id + "_label") && canvas.addMarker(element.id + "_label", color); // 高亮网关（文字）
        // 设置往线的高亮
        element.outgoing?.forEach((outLine) => {
          canvas.addMarker(outLine.id, color);
          canvas.findRoot(outLine.id + "_label") && canvas.addMarker(outLine.id + "_label", color); // 高亮连线（文字）
        });
      }
      return;
    }

    // 开始节点 ##############################################
    if (element.$type === "bpmn:StartEvent" && activity) {
      const color = "highlight";
      canvas.addMarker(element.id, color); // 高亮【bpmn:StartEvent】开始节点（自己）
      canvas.findRoot(element.id + "_label") && canvas.addMarker(element.id + "_label", color); // 高亮【bpmn:StartEvent】开始节点（文字）
      element.outgoing?.forEach((outLine) => {
        canvas.addMarker(outLine.id, color); // 高亮【bpmn:SequenceFlow】连线
      });
      return;
    }

    // 结束节点 ##############################################
    if (element.$type === "bpmn:EndEvent" && activity) {
      const color = "highlight";
      canvas.addMarker(element.id, color);
      canvas.findRoot(element.id + "_label") && canvas.addMarker(element.id + "_label", color); // 高亮结束（文字）
    }
  });
};

/** 根据活动状态，设置高亮样式 */
const getResultCss = (result?: string) => {
  if (result === "0") {
    // 待办
    return "highlight-todo";
  } else if (result === "1") {
    // 已办-同意
    return "highlight";
  } else if (result === "2") {
    // 拒绝
    return "highlight-reject";
  } else if (result === "3") {
    // 撤销
    return "highlight-cancel";
  }
  return "no-marker";
};

/** 执行条件表达式 */
const executeConditionExpression = (params: string, expression: string) => {
  const paramsObj = JSON.parse(params) as { [key: string]: string };
  let evalStr = expression;
  for (const [key, value] of Object.entries(paramsObj)) {
    evalStr = evalStr.replaceAll(key, `'${value}'`);
  }

  return eval(evalStr.replace("${", "").replace("}", "")) as boolean;
};

/**
 * 获取活动 outgoing 出线
 */
const getActivityOutgoing = (actKey?: string): BpmnElement[] => {
  if (!actKey) {
    return [] as BpmnElement[];
  }

  // 如果没有，则遍历获得起点为它的【bpmn:SequenceFlow】节点们。原因是：bpmn-js 的 UserTask 拿不到 outgoing
  const viewer = getBpmnModeler();
  const flowElements = viewer.getDefinitions().rootElements[0].flowElements;
  const outgoing: BpmnElement[] = [];
  flowElements.forEach((item: BpmnElement) => {
    if (item.$type !== "bpmn:SequenceFlow") {
      return;
    }
    if (item.sourceRef.id === actKey) {
      outgoing.push(item);
    }
  });
  return outgoing;
};

/**
 * 初始化视图监听器
 */
const initModelListeners = () => {
  const viewer = getBpmnModeler();
  const eventBus = viewer.get<EventBus>("eventBus");
  // 注册需要的监听事件
  eventBus.on<{ element: BpmnElement }>("element.hover", elementHover);
  eventBus.on("element.out", clearOverlays);
};

/**
 * 获取Overlays对象
 */
const getOverlays = () => {
  if (!overlays) {
    const viewer = getBpmnModeler();
    overlays = viewer.get<Overlays>("overlays");
  }
  return overlays;
};

/**
 * 流程图的元素 hover事件
 */
const elementHover = (eventObj: { element: BpmnElement }) => {
  if (!eventObj) return;

  const element = eventObj.element;
  const tOverlays = getOverlays();
  const act = actList.find((item) => item.actKey === element.id); //活动
  let html = "";

  // 展示信息(开始节点)
  if (element.type === "bpmn:StartEvent" && act) {
    html = `<p>发起人：${act.assigneeName}</p>`;
    tOverlays.add(element.id, {
      position: { left: 0, bottom: 0 },
      html: `<div class="element-overlays">${html}</div>`,
    });
  }
  // 展示信息(审批成功的任务节点)
  if (element.type === "bpmn:UserTask" && act?.status && ["1", "2"].indexOf(act?.status) >= 0) {
    const params = act.actParams ? JSON.parse(act.actParams) : { comment: "" };
    html = `<p>审批人：${act.assigneeName}</p>
              <p>审批结果：${act.status === "1" ? "同意" : "驳回"}</p>
              <p>审批意见：${params.comment}</p>`;
    tOverlays.add(element.id, {
      position: { left: 0, bottom: 0 },
      html: `<div class="element-overlays">${html}</div>`,
    });
  }
};
/**
 * 清除任务信息展示元素
 */
const clearOverlays = () => {
  const tOverlays = getOverlays();
  tOverlays.clear();
};

// 初始化 ##############################################

// 销毁前 ##############################################
onBeforeUnmount(() => {
  const viewer = getBpmnModeler();
  const eventBus = viewer.get<EventBus>("eventBus");
  // 注销监听事件
  eventBus.off("element.hover", elementHover);
  eventBus.off("element.out", clearOverlays);
});
//使用此属性导出实例方法
// defineExpose({
//   drawProcessView
// });
</script>

<style lang="less" scoped>
.canvas {
  height: 100%;
  min-height: 300px;
}

/** 通过 */
:deep(.highlight) {
  &.djs-shape > .djs-visual {
    // 节点边框（开始、结束、任务）
    > circle:nth-child(1),
    > rect:nth-child(1),
    > polygon:nth-child(1) {
      fill: green !important;
      stroke: green !important;
      fill-opacity: 0.2 !important;
    }

    // 节点文字（开始、结束、任务）,连线文字, 网关图标
    > text,
    > path {
      fill: green !important;
    }
  }

  // 普通连接线
  &.djs-connection > .djs-visual > path {
    stroke: green !important;
    marker-end: url("#sequenceflow-arrow-end-green") !important;
    z-index: 10;
  }

  // 条件连接线 marker-start
  &.start-marker > .djs-visual > path {
    marker-start: url("#conditional-flow-start-green") !important;
    z-index: 10;
  }
}

/** 处理中 */
:deep(.highlight-todo) {
  &.djs-shape > .djs-visual {
    // 节点边框（开始、结束、任务）
    > circle:nth-child(1),
    > rect:nth-child(1),
    > polygon:nth-child(1) {
      fill: #1890ff !important;
      stroke: #1890ff !important;
      fill-opacity: 0.2 !important;
      stroke-dasharray: 4px !important;
    }

    // 节点文字（开始、结束、任务）,连线文字, 网关图标
    > text,
    > path {
      fill: #1890ff !important;
    }
  }

  // 普通连接线
  &.djs-connection > .djs-visual > path {
    stroke: #1890ff !important;
    stroke-dasharray: 4px !important;
    marker-end: url("#sequenceflow-arrow-end-blue") !important;
    z-index: 10;
  }

  // 条件连接线 marker-start
  &.start-marker > .djs-visual > path {
    marker-start: url("#conditional-flow-start-blue") !important;
    z-index: 10;
  }
}

/** 不通过 */
:deep(.highlight-reject) {
  &.djs-shape > .djs-visual {
    // 节点边框（开始、结束、任务）
    > circle:nth-child(1),
    > rect:nth-child(1),
    > polygon:nth-child(1) {
      fill: red !important;
      stroke: red !important;
      fill-opacity: 0.2 !important;
    }

    // 节点文字（开始、结束、任务）,连线文字, 网关图标
    > text,
    > path {
      fill: red !important;
    }
  }

  // 普通连接线
  &.djs-connection > .djs-visual > path {
    stroke: red !important;
    marker-end: url("#sequenceflow-arrow-end-red") !important;
    z-index: 10;
  }

  // 条件连接线 marker-start
  &.start-marker > .djs-visual > path {
    marker-start: url("#conditional-flow-start-red") !important;
    z-index: 10;
  }
}

/** 已取消 */
:deep(.highlight-cancel) {
  &.djs-shape > .djs-visual {
    // 节点边框（开始、结束、任务）
    > circle:nth-child(1),
    > rect:nth-child(1),
    > polygon:nth-child(1) {
      fill: grey !important;
      stroke: grey !important;
      fill-opacity: 0.2 !important;
    }

    // 节点文字（开始、结束、任务）,连线文字, 网关图标
    > text,
    > path {
      fill: grey !important;
    }
  }

  // 普通连接线
  &.djs-connection > .djs-visual > path {
    stroke: grey !important;
    marker-end: url("#sequenceflow-arrow-end-grey") !important;
  }

  // 条件连接线 marker-start
  &.start-marker > .djs-visual > path {
    marker-start: url("#conditional-flow-start-grey") !important;
  }
}

:deep(.element-overlays) {
  box-sizing: border-box;
  padding: 8px;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 4px;
  color: #fafafa;

  > p {
    margin: 0;
    min-width: 200px;
  }
}
</style>
