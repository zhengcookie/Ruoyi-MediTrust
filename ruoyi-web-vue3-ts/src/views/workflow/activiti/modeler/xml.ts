import type { XmlMetaInfo } from "@/api/workflow/activiti/modeler";

/**
 * bpmn2.0标准
 * activiti标准转化为camunda标准
 * @param xml
 */
export const activitiToCamundaXml = (xml: string, metaInfo?: XmlMetaInfo) => {
  //替换activiti标准
  xml = xml.replace("xmlns:activiti", "xmlns:camunda");
  xml = xml.replace("http://activiti.org/bpmn", "http://camunda.org/schema/1.0/bpmn");
  xml = xml.replaceAll("activiti:", "camunda:");
  //显示版本信息
  if (metaInfo && metaInfo.version) {
    xml.replace("<bpmn2:process", `<bpmn2:process camunda:versionTag="${metaInfo.version}"`);
  }
  return xml;
};

/**
 * bpmn2.0标准
 * camunda标准转化为activiti标准
 * @param xml
 */
export const camundaToActivitiXml = (xml: string) => {
  //追加头部信息
  xml = xml.replace(
    "<bpmn2:definitions",
    `<bpmn2:definitions typeLanguage="http://www.w3.org/2001/XMLSchema" 
    expressionLanguage="http://www.w3.org/1999/XPath"`
  );
  //删除版本号
  // xml = xml.replace(/camunda:versionTag="*"/, "");
  //替换camunda标准
  xml = xml.replace("xmlns:camunda", "xmlns:activiti");
  xml = xml.replace("http://camunda.org/schema/1.0/bpmn", "http://activiti.org/bpmn");
  xml = xml.replaceAll("camunda:", "activiti:");
  return xml;
};

/**
 * bpmn2.0标准
 * 初始化一个camunda标准的xml
 * @param {XmlMetaInfo} metaInfo
 * @returns {string}
 */
const createBpmnXml = ({ key, name, description, version }: XmlMetaInfo) => {
  const keyStr = key ? key : "Process_1";
  const nameStr = name ? name : "";
  const descriptionStr = description ? `<bpmn2:documentation>${description}</bpmn2:documentation>` : "";
  const versionStr = version ? version : 1;

  const xmlStr = `<?xml version="1.0" encoding="UTF-8"?>
    <bpmn2:definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL"
      xmlns:bpmn2="http://www.omg.org/spec/BPMN/20100524/MODEL" 
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
      xmlns:camunda="http://camunda.org/schema/1.0/bpmn" 
      xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" 
      xmlns:omgdc="http://www.omg.org/spec/DD/20100524/DC" 
      xmlns:omgdi="http://www.omg.org/spec/DD/20100524/DI" 
      xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
      targetNamespace="http://www.activiti.org/test">
      <bpmn2:process id="${keyStr}" name="${nameStr}" isExecutable="true" camunda:versionTag="${versionStr}" >
        ${descriptionStr}
      </bpmn2:process>
      <bpmndi:BPMNDiagram id="BpmnDiagram_1">
        <bpmndi:BPMNPlane id="BpmnPlane_1" bpmnElement="${keyStr}" />
      </bpmndi:BPMNDiagram>
    </bpmn2:definitions>`;
  return xmlStr;
};
export default createBpmnXml;
