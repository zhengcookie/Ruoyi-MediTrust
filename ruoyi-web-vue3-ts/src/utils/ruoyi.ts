/**
 * 通用js方法封装处理
 * Copyright (c) 2019 ruoyi
 */

import type { ElForm } from "@/api/form";
import type { DictObj } from "@/api/system/dict/data";
import type { Ref } from "vue";

/**
 * 表单重置
 * @param vueRef
 */
export function resetForm(vueRef: Ref<ElForm>) {
  vueRef.value?.resetFields();
}

// 添加日期范围
export function addDateRange<T extends { beginTime?: string; endTime?: string }>(params: T, dateRange: string[]) {
  if (params instanceof Object) {
    dateRange = Array.isArray(dateRange) ? dateRange : [];
    params["beginTime"] = dateRange[0];
    params["endTime"] = dateRange[1];
  }
  return params;
}

// 回显数据字典
export function selectDictLabel(datas: DictObj[], value: string) {
  if (!value) {
    return "";
  }

  for (let i = 0; i < datas.length; i++) {
    const dict = datas[i];
    if (dict.value === value) {
      return dict.label;
    }
  }

  return "";
}

// 回显数据字典（字符串、数组）
export function selectDictLabels(datas: { [keys: string]: { value: string; label: string } }, value: string, separator: string) {
  if (value === undefined || value.length === 0) {
    return "";
  }
  if (Array.isArray(value)) {
    value = value.join(",");
  }
  const actions: string[] = [];
  const currentSeparator = undefined === separator ? "," : separator;
  const temp: string[] = value.split(currentSeparator);
  Object.keys(value.split(currentSeparator)).some((val) => {
    let match = false;
    Object.keys(datas).some((key) => {
      if (datas[key].value == "" + temp[Number(val)]) {
        actions.push(datas[key].label + currentSeparator);
        match = true;
      }
    });
    if (!match) {
      actions.push(temp[Number(val)] + currentSeparator);
    }
  });
  return actions.join("").substring(0, actions.join("").length - 1);
}

// 转换字符串，undefined,null等转化为""
export function parseStrEmpty(str: string) {
  if (!str || str == "undefined" || str == "null") {
    return "";
  }
  return str;
}

// 数据合并
// export function mergeRecursive(source: { [keys: string]: unknown }, target: { [keys: string]: unknown }) {
//   for (const p in target) {
//     try {
//       if (target[p]?.constructor == Object) {
//         source[p] = mergeRecursive(source[p], target[p]);
//       } else {
//         source[p] = target[p];
//       }
//     } catch (e) {
//       source[p] = target[p];
//     }
//   }
//   return source;
// }

/**
 * 构造树型结构数据
 * @param {*} data 数据源
 * @param {*} id id字段 默认 'id'
 * @param {*} parentId 父节点字段 默认 'parentId'
 * @param {*} children 孩子节点字段 默认 'children'
 */
export function handleTree<T extends { [key: string]: unknown }>(data: T[], id?: string, parentId?: string, children?: string) {
  const config = {
    id: id || "id",
    parentId: parentId || "parentId",
    childrenList: children || "children",
  };

  const childrenListMap: { [keys: string]: unknown } = {};
  const nodeIds: { [keys: string]: unknown } = {};
  const tree: T[] = [];

  for (const d of data) {
    const pId = d[config.parentId];
    if ((typeof pId === "string" || typeof pId === "number") && !childrenListMap[pId]) {
      childrenListMap[pId] = [];
    }
    const dataId = d[config.id];
    (typeof dataId === "string" || typeof dataId === "number") && (nodeIds[dataId] = d);
    if (typeof pId === "string" || typeof pId === "number") {
      const cList = childrenListMap[pId];
      cList instanceof Array && cList.push(d);
    }
  }

  for (const d of data) {
    const pId = d[config.parentId];
    if ((typeof pId === "string" || typeof pId === "number") && nodeIds[pId] == null) {
      tree.push(d);
    }
  }

  for (const t of tree) {
    adaptToChildrenList(t);
  }

  function adaptToChildrenList(o: { [keys: string]: unknown }) {
    const pId = o[config.id];
    if ((typeof pId === "string" || typeof pId === "number") && childrenListMap[pId] !== null) {
      o[config.childrenList] = childrenListMap[pId];
    }
    const cList = o[config.childrenList];
    if (cList instanceof Array) {
      for (const c of cList) {
        adaptToChildrenList(c);
      }
    }
  }
  return tree;
}

/**
 * 参数处理
 * @param {*} params  参数
 */
export function tansParams(params: { [keys: string]: unknown }) {
  let result = "";
  for (const propName of Object.keys(params)) {
    const value = params[propName];
    const part = encodeURIComponent(propName) + "=";
    if (value !== null && value !== "" && typeof value !== "undefined") {
      if (value instanceof Object) {
        for (const [ckey, cVal] of Object.entries(value)) {
          if (typeof cVal === "string" || typeof cVal === "number" || typeof cVal === "boolean") {
            const temp = propName + "[" + ckey + "]";
            const subPart = encodeURIComponent(temp) + "=";
            result += subPart + encodeURIComponent(cVal) + "&";
          }
        }
      } else if (typeof value === "string" || typeof value === "number" || typeof value === "boolean") {
        result += part + encodeURIComponent(value) + "&";
      }
    }
  }
  return result;
}

/**
 * 验证是否为blob格式
 * @param data
 * @returns
 */
export async function blobValidate(data: Blob) {
  try {
    const text = await data.text();
    JSON.parse(text);
    return false;
  } catch (error) {
    return true;
  }
}
