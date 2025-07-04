import type { FormContext, FormValidateCallback, FormValidationResult } from "element-plus";
// import type { App } from "vue";

export type ElForm = FormContext & {
  readonly validate: (callback?: FormValidateCallback | undefined) => FormValidationResult;
};

export type QueryParam = {
  pageNum: number;
  pageSize: number;
  beginTime?: string;
  endTime?: string;
  orderByColumn?: string;
  isAsc?: string;
};
