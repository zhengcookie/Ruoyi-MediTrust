import dictStore from "@/stores/dict";
import { getDicts as requestDicts } from "@/api/system/dict/data";
import type { DictObj } from "@/api/system/dict/data";
import { ref } from "vue";

export type DictMapObj = { [key: string]: DictObj[] | undefined };

export const getDicts = (dictType: string | string[]) => {
  const store = dictStore();
  const types = typeof dictType == "string" ? [dictType] : dictType;
  const DictMapObj = new Object() as DictMapObj;

  //查询store中存在的字典数据
  const typesNo: string[] = [];
  types.forEach((type) => {
    const dict = store.getDict(type);
    if (dict) {
      DictMapObj[type] = dict;
    } else {
      typesNo.push(type);
    }
  });

  if (typesNo.length === 0) {
    return new Promise<DictMapObj>((resolve) => {
      resolve(DictMapObj);
    });
  } else {
    //查询store中不存在的字典数据
    const promises = typesNo.map((type) => {
      return new Promise<{ key: string; value: DictObj[] | undefined }>((resolve) => {
        requestDicts(type).then((rsp) => {
          rsp.data?.forEach((dict) => {
            dict.label = dict.dictLabel;
            dict.value = dict.dictValue;
          });
          resolve({
            key: type,
            value: rsp.data,
          });
        });
      });
    });
    return new Promise<DictMapObj>((resolve) => {
      Promise.all(promises).then((rsps) => {
        rsps.forEach((rsp) => {
          rsp.value && store.setDict(rsp.key, rsp.value);
          rsp.value && (DictMapObj[rsp.key] = rsp.value);
        });
        resolve(DictMapObj);
      });
    });
  }
};

/**
 * 初始化字典对象
 * @param dictType
 * @returns vue Ref
 */
export const loadDicts = (dictType: string | string[]) => {
  const dict = ref<DictMapObj>({});
  getDicts(dictType).then((rsp) => {
    dict.value = rsp;
  });
  return dict;
};
