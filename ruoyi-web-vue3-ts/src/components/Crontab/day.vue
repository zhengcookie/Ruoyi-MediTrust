<template>
  <el-form>
    <el-form-item>
      <el-radio v-model="radioValue" :label="1">日，允许的通配符[, - * ? / L W]</el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio v-model="radioValue" :label="2">不指定</el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio v-model="radioValue" :label="3">
        周期从
        <el-input-number v-model="cycle01" :min="1" :max="30" />
        -
        <el-input-number v-model="cycle02" :min="cycle01 + 1" :max="31" />
        日
      </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio v-model="radioValue" :label="4">
        从
        <el-input-number v-model="average01" :min="1" :max="30" />
        号开始，每
        <el-input-number v-model="average02" :min="1" :max="31 - average01" />
        日执行一次
      </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio v-model="radioValue" :label="5">
        每月
        <el-input-number v-model="workday" :min="1" :max="31" />
        号最近的那个工作日
      </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio v-model="radioValue" :label="6">本月最后一天</el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio v-model="radioValue" :label="7">
        指定
        <el-select v-model="checkboxList" clearable placeholder="可多选" multiple :multiple-limit="10">
          <el-option v-for="item in 31" :key="item" :label="item" :value="item" />
        </el-select>
      </el-radio>
    </el-form-item>
  </el-form>
</template>
<script lang="ts" setup name="CompCrontabDay">
import { computed, ref, watch } from "vue";

const emit = defineEmits<{ (event: "update", name: "second" | "min" | "hour" | "day" | "month" | "week" | "year", value: string): void }>();

const props = withDefaults(
  defineProps<{
    cron: {
      second: string;
      min: string;
      hour: string;
      day: string;
      month: string;
      week: string;
      year: string;
    };
    check: (value: number, minLimit: number, maxLimit: number) => number;
  }>(),
  {
    cron: () => {
      return {
        second: "*",
        min: "*",
        hour: "*",
        day: "*",
        month: "*",
        week: "?",
        year: "",
      };
    },
    check: (value: number) => {
      return value;
    },
  }
);

const radioValue = ref(1);
const cycle01 = ref(1);
const cycle02 = ref(2);
const average01 = ref(1);
const average02 = ref(1);
const workday = ref(1);
const checkboxList = ref<number[]>([]);
const checkCopy = ref([1]);
const cycleTotal = computed(() => {
  return cycle01.value + "-" + cycle02.value;
});
const averageTotal = computed(() => {
  return average01.value + "/" + average02.value;
});
const workdayTotal = computed(() => {
  return workday.value + "W";
});
const checkboxString = computed(() => {
  return checkboxList.value.join(",");
});
watch(
  () => props.cron.day,
  (value) => changeRadioValue(value)
);
watch([radioValue, cycleTotal, averageTotal, workdayTotal, checkboxString], () => onRadioChange());
function changeRadioValue(value: string) {
  if (value === "*") {
    radioValue.value = 1;
  } else if (value === "?") {
    radioValue.value = 2;
  } else if (value.indexOf("-") > -1) {
    const indexArr = value.split("-");
    cycle01.value = props.check(Number(indexArr[0]), 1, 30);
    cycle02.value = props.check(Number(indexArr[1]), cycle01.value + 1, 31);
    radioValue.value = 3;
  } else if (value.indexOf("/") > -1) {
    const indexArr = value.split("/");
    average01.value = props.check(Number(indexArr[0]), 1, 30);
    average02.value = props.check(Number(indexArr[1]), 1, 31 - average01.value);
    radioValue.value = 4;
  } else if (value.indexOf("W") > -1) {
    const indexArr = value.split("W");
    workday.value = props.check(Number(indexArr[0]), 1, 31);
    radioValue.value = 5;
  } else if (value === "L") {
    radioValue.value = 6;
  } else {
    checkboxList.value = [...new Set<number>(value.split(",").map((item) => Number(item)))];
    radioValue.value = 7;
  }
}
// 单选按钮值变化时
function onRadioChange() {
  if (radioValue.value === 2 && props.cron.week === "?") {
    emit("update", "week", "*");
  }
  if (radioValue.value !== 2 && props.cron.week !== "?") {
    emit("update", "week", "?");
  }
  switch (radioValue.value) {
    case 1:
      emit("update", "day", "*");
      break;
    case 2:
      emit("update", "day", "?");
      break;
    case 3:
      emit("update", "day", cycleTotal.value);
      break;
    case 4:
      emit("update", "day", averageTotal.value);
      break;
    case 5:
      emit("update", "day", workdayTotal.value);
      break;
    case 6:
      emit("update", "day", "L");
      break;
    case 7:
      if (checkboxList.value.length === 0) {
        checkboxList.value.push(checkCopy.value[0]);
      } else {
        checkCopy.value = checkboxList.value;
      }
      emit("update", "day", checkboxString.value);
      break;
  }
}
</script>

<style lang="less" scoped>
.el-input-number--small,
.el-select,
.el-select--small {
  margin: 0 0.2rem;
}

.el-select,
.el-select--small {
  width: 18.8rem;
}
</style>
