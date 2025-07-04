<template>
  <div ref="homePage" class="page">
    <!-- 待办、已办、办结、消息、预警 -->
    <el-row :gutter="20" class="mgb20">
      <el-col v-for="(item, index) in messageList" :key="index" :span="4">
        <el-card shadow="hover" :body-style="{ padding: '0px' }" @click="handleMessageClick(item)">
          <div class="grid-message">
            <el-icon class="grid-msg-icon" :style="{ background: item.color }">
              <component :is="item.icon" />
            </el-icon>
            <div class="grid-msg-right">
              <div class="grid-msg-num" :style="{ color: item.color }">
                {{ item.count }}
              </div>
              <div>{{ item.name }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 市场占比 销售数量 总销售额 成本-->
    <el-row :gutter="20" class="mgb20">
      <el-col :span="6">
        <el-card shadow="hover" :body-style="{ padding: '0px 10px' }">
          <div class="chart-cont">
            <h1>
              <span>市场占比</span>
              <el-dropdown>
                <span class="el-dropdown-link">
                  产品
                  <el-icon class="el-icon--right">
                    <arrow-down />
                  </el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item>电脑A</el-dropdown-item>
                    <el-dropdown-item>手机A</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </h1>
            <p>80%</p>
            <div>
              <p>型号：HW20349</p>
              <el-progress :text-inside="true" :stroke-width="24" :percentage="80" status="success" />
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" :body-style="{ padding: '0px 10px' }">
          <div class="chart-cont">
            <h1>
              <span>销售数量</span>
              <el-dropdown>
                <span class="el-dropdown-link">
                  季度
                  <el-icon class="el-icon--right">
                    <arrow-down />
                  </el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item>一季度</el-dropdown-item>
                    <el-dropdown-item>二季度</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </h1>
            <p>40000</p>
            <div ref="refChart1"></div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" :body-style="{ padding: '0px 10px' }">
          <div class="chart-cont">
            <h1>
              <span>总销售额</span>
              <el-dropdown>
                <span class="el-dropdown-link">
                  季度
                  <el-icon class="el-icon--right">
                    <arrow-down />
                  </el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item>一季度</el-dropdown-item>
                    <el-dropdown-item>二季度</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </h1>
            <p>￥400,000.00</p>
            <div ref="refChart2"></div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card shadow="hover" :body-style="{ padding: '0px 10px' }">
          <div class="chart-cont">
            <h1>
              <span>投入成本</span>
            </h1>
            <p>￥380,000.00</p>
            <div ref="refChart3"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- -->
    <el-row :gutter="20" class="mgb20">
      <el-col :span="8">
        <el-card shadow="hover" :body-style="{ padding: '10px' }">
          <div ref="refChart4" class="chart-cont2"></div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card shadow="hover" :body-style="{ padding: '10px' }">
          <div ref="refChart5" class="chart-cont2"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- -->
    <el-row :gutter="20" class="mgb20">
      <el-col :span="16">
        <el-card shadow="hover" :body-style="{ padding: '10px' }">
          <template #header>
            <div class="card-header">
              <span>人员信息</span>
              <el-button class="button" text>更多</el-button>
            </div>
          </template>
          <el-table :data="tableData1" border style="width: 100%" size="small">
            <el-table-column prop="name" label="姓名" width="120" />
            <el-table-column prop="sex" label="性别" width="80" />
            <el-table-column prop="date" label="出生日期" width="140" />
            <el-table-column prop="email" label="邮箱" width="180" />
            <el-table-column prop="address" label="地址" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" :body-style="{ padding: '10px' }">
          <template #header>
            <div class="card-header">
              <span>我的消息</span>
              <el-button class="button" text>更多</el-button>
            </div>
          </template>
          <el-table :data="tableData2" style="width: 100%" size="small">
            <el-table-column prop="no" label="序号" width="80" />
            <el-table-column prop="text" label="消息" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script lang="ts" setup name="homePage">
import { nextTick, onMounted, ref } from "vue";
import * as echarts from "echarts";
import { onBeforeUnmount } from "vue";
import { watch } from "vue";
import { useElementSize } from "@vueuse/core";
import { queryTaskCount } from "@/api/workflow/activiti/task";
import { useRouter } from "vue-router";

const router = useRouter();
const messageList = ref([
  {
    type: "mytask",
    name: "我的申请",
    count: 0,
    color: "#409EFF",
    icon: "Promotion",
    linkUrl: "/workbench/workflow/task/myProcess",
  },
  {
    type: "todo",
    name: "待办",
    count: 0,
    color: "#409EFF",
    icon: "Flag",
    linkUrl: "/workbench/workflow/task/todo",
  },
  {
    type: "done",
    name: "已办",
    count: 0,
    color: "#67C23A",
    icon: "SuccessFilled",
    linkUrl: "/workbench/workflow/task/done",
  },
  {
    type: "conclude",
    name: "办结",
    count: 0,
    color: "#67C23A",
    icon: "Checked",
    linkUrl: "/workbench/workflow/task/done",
  },
  {
    type: "message",
    name: "我的消息",
    count: 0,
    color: "#E6A23C",
    icon: "BellFilled",
    linkUrl: "#",
  },
  {
    type: "warninng",
    name: "预警信息",
    count: 0,
    color: "#F56C6C",
    icon: "WarningFilled",
    linkUrl: "#",
  },
]);

const tableData1 = [
  {
    date: "2016-05-03",
    name: "张三",
    sex: "男",
    email: "zhangsan@134.com",
    address: "四川省成都市双流区",
  },
  {
    date: "2016-05-02",
    name: "李四",
    sex: "男",
    email: "lisi@134.com",
    address: "四川省成都市高新区",
  },
  {
    date: "2016-05-04",
    name: "王五",
    sex: "男",
    email: "wangwu@134.com",
    address: "四川省成都市武侯区",
  },
  {
    date: "2016-05-01",
    name: "小明",
    sex: "男",
    email: "xiaoming@134.com",
    address: "四川省成都市郫都区",
  },
  {
    date: "2016-05-01",
    name: "小红",
    sex: "女",
    email: "xiaohong@134.com",
    address: "四川省成都市锦江区",
  },
];

const tableData2 = [
  {
    no: 1,
    text: "张三发来一条消息",
  },
  {
    no: 2,
    text: "李四发来一条消息",
  },
  {
    no: 3,
    text: "王五发来一条消息",
  },
  {
    no: 4,
    text: "小红发来一条消息",
  },
  {
    no: 5,
    text: "小明发来一条消息",
  },
];

// 图标1
const refChart1 = ref();
let myChart1: echarts.EChartsType;
const initChart1 = () => {
  if (refChart1.value) {
    //初始化echarts实例
    myChart1 = echarts.init(refChart1.value);
    // 指定图表的配置项和数据
    const option = {
      grid: {
        left: 3,
        top: 0,
        right: 3,
        bottom: 0,
      },
      tooltip: {
        trigger: "axis",
        axisPointer: {
          type: "shadow",
        },
      },
      xAxis: {
        type: "category",
        boundaryGap: false,
        data: ["手机A", "手机B", "手机C", "电脑A", "电脑B", "电脑C", "电脑D"],
        show: false,
      },
      yAxis: {
        type: "value",
        max: function (value: { max: number }) {
          return value.max + 100;
        },
        show: false,
      },
      series: [
        {
          data: [820, 932, 901, 934, 1290, 1330, 1320],
          type: "line",
          areaStyle: {},
        },
      ],
    };
    // 使用刚指定的配置项和数据显示图表。
    myChart1.setOption(option);
  }
};

// 图标1
const refChart2 = ref();
let myChart2: echarts.EChartsType;
const initChart2 = () => {
  if (refChart2.value) {
    //初始化echarts实例
    myChart2 = echarts.init(refChart2.value);
    // 指定图表的配置项和数据
    const option = {
      grid: {
        left: 3,
        top: 0,
        right: 3,
        bottom: 0,
      },
      tooltip: {
        trigger: "axis",
        axisPointer: {
          type: "shadow",
        },
      },
      xAxis: [
        {
          type: "category",
          data: ["手机A", "手机B", "手机C", "电脑A", "电脑B", "电脑C", "电脑D"],
          axisTick: {
            alignWithLabel: true,
          },
          show: false,
        },
      ],
      yAxis: [
        {
          type: "value",
          show: false,
        },
      ],
      series: [
        {
          name: "",
          type: "bar",
          barWidth: "60%",
          data: [20000, 40000, 30000, 90000, 50000, 110000, 130000],
        },
      ],
    };
    // 使用刚指定的配置项和数据显示图表。
    myChart2.setOption(option);
  }
};

// 图标3
const refChart3 = ref();
let myChart3: echarts.EChartsType;
const initChart3 = () => {
  if (refChart3.value) {
    //初始化echarts实例
    myChart3 = echarts.init(refChart3.value);
    // 指定图表的配置项和数据
    const option = {
      grid: {
        left: 3,
        top: 0,
        right: 3,
        bottom: 0,
      },
      tooltip: {
        trigger: "axis",
      },
      xAxis: {
        type: "category",
        boundaryGap: false,
        data: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
      },
      yAxis: {
        type: "value",
      },
      series: [
        {
          name: "电脑",
          type: "line",
          stack: "Total",
          data: [120, 132, 101, 134, 90, 230, 210],
        },
        {
          name: "手机",
          type: "line",
          stack: "Total",
          data: [220, 182, 191, 234, 290, 330, 310],
        },
      ],
    };
    // 使用刚指定的配置项和数据显示图表。
    myChart3.setOption(option);
  }
};

// 图标3
const refChart4 = ref();
let myChart4: echarts.EChartsType;
const initChart4 = () => {
  if (refChart4.value) {
    //初始化echarts实例
    myChart4 = echarts.init(refChart4.value);
    // 指定图表的配置项和数据
    const option = {
      title: {
        text: "推广统计",
        left: "center",
      },
      tooltip: {
        trigger: "item",
      },
      legend: {
        type: "scroll",
        orient: "horizontal",
        left: "center",
        bottom: 0,
      },
      series: [
        {
          name: "数量",
          type: "pie",
          radius: "50%",
          data: [
            { value: 1048, name: "搜索引擎" },
            { value: 735, name: "直接" },
            { value: 580, name: "邮件" },
            { value: 484, name: "联合广告" },
            { value: 300, name: "视频广告" },
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: "rgba(0, 0, 0, 0.5)",
            },
          },
        },
      ],
    };
    // 使用刚指定的配置项和数据显示图表。
    myChart4.setOption(option);
  }
};

// 图标5
const refChart5 = ref();
let myChart5: echarts.EChartsType;
const initChart5 = () => {
  if (refChart5.value) {
    //初始化echarts实例
    myChart5 = echarts.init(refChart5.value);
    // 指定图表的配置项和数据
    const option = {
      title: {
        text: "天气",
        left: "center",
      },
      tooltip: {
        trigger: "axis",
        axisPointer: {
          type: "cross",
          crossStyle: {
            color: "#999",
          },
        },
      },
      toolbox: {
        feature: {
          dataView: { title: "数据视图", show: true, readOnly: false },
          magicType: {
            title: { line: "折线图", bar: "柱状图" },
            show: true,
            type: ["line", "bar"],
          },
          restore: { title: "刷新", show: true },
          saveAsImage: { title: "下载", show: true },
        },
      },
      legend: {
        data: ["蒸发量", "降水", "温度"],
        bottom: 0,
      },
      xAxis: [
        {
          type: "category",
          data: ["1月", "2月", "3月", "4月", "5月", "6月", "7月"],
          axisPointer: {
            type: "shadow",
          },
        },
      ],
      yAxis: [
        {
          type: "value",
          name: "降水",
          min: 0,
          max: 250,
          interval: 50,
          axisLabel: {
            formatter: "{value} ml",
          },
        },
        {
          type: "value",
          name: "温度",
          min: 0,
          max: 25,
          interval: 5,
          axisLabel: {
            formatter: "{value} °C",
          },
        },
      ],
      series: [
        {
          name: "蒸发量",
          type: "bar",
          tooltip: {
            valueFormatter: function (value: string) {
              return value + " ml";
            },
          },
          data: [2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3],
        },
        {
          name: "降水",
          type: "bar",
          tooltip: {
            valueFormatter: function (value: string) {
              return value + " ml";
            },
          },
          data: [2.6, 5.9, 9.0, 26.4, 28.7, 70.7, 175.6, 182.2, 48.7, 18.8, 6.0, 2.3],
        },
        {
          name: "温度",
          type: "line",
          yAxisIndex: 1,
          tooltip: {
            valueFormatter: function (value: string) {
              return value + " °C";
            },
          },
          data: [2.0, 2.2, 3.3, 4.5, 6.3, 10.2, 20.3, 23.4, 23.0, 16.5, 12.0, 6.2],
        },
      ],
    };
    // 使用刚指定的配置项和数据显示图表。
    myChart5.setOption(option);
  }
};

//重新渲染图表
const resizeChart = () => {
  // 折叠板延迟时间
  setTimeout(() => {
    myChart1?.resize();
    myChart2?.resize();
    myChart3?.resize();
    myChart4?.resize();
    myChart5?.resize();
  }, 400);
};

// 监听页面大小变化
const homePage = ref();
const { width: pageWidth } = useElementSize(homePage);
watch(pageWidth, () => {
  resizeChart();
});

// 查询任务总数
const getTaskCount = () => {
  queryTaskCount().then(({ data }) => {
    if (data) {
      messageList.value[0].count = data.myProcessCount;
      messageList.value[1].count = data.todoCount;
      messageList.value[2].count = data.doneCount;
      messageList.value[3].count = data.finishCount;
    }
  });
};

// 点击消息
const handleMessageClick = ({ linkUrl }: { linkUrl?: string }) => {
  if (linkUrl) {
    router.push(linkUrl);
  }
};

// 初始化渲染
onMounted(() => {
  nextTick(() => {
    initChart1();
    initChart2();
    initChart3();
    initChart4();
    initChart5();
    window.addEventListener("resize", resizeChart);
  });
});

//销毁
onBeforeUnmount(() => {
  window.removeEventListener("resize", resizeChart);
});

getTaskCount();
// defineExpose无需导入，直接使用
// defineExpose({ value });
</script>

<style lang="less" scoped>
@message-width: 80px;

.page {
  padding-bottom: 20px;
}

.grid-message {
  display: flex;
  align-items: center;
  height: @message-width;
  cursor: pointer;
}

.grid-msg-right {
  flex: 1;
  text-align: center;
  font-size: 14px;
  color: #999;
}

.grid-msg-num {
  font-size: 30px;
  font-weight: bold;
}

.grid-msg-icon {
  font-size: 50px;
  width: @message-width;
  height: @message-width;
  text-align: center;
  line-height: @message-width;
  color: #fff;
}

.chart-cont {
  height: 140px;

  > h1 {
    color: #606266;
    font-weight: normal;
    display: flex;
    font-size: 14px;
    justify-content: space-between;
  }

  > p {
    margin: 0;
    padding: 0;
    font-size: 30px;
    line-height: 40px;
  }

  > div {
    height: 60px;
    width: 100%;

    > p {
      margin: 0;
      padding-top: 10px;
      line-height: 20px;
      font-size: 12px;
      color: #909399;
    }
  }
}

.chart-cont2 {
  height: 240px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
