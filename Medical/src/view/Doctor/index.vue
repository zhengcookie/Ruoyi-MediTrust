<template>
    <div>
        <div style="display: flex; justify-content: space-between; margin-top: 20px">
            <el-card style="width: 48%">
                <template #header>
                    <div class="info-header">
                        <span class="header-text">医疗信息统计</span>
                    </div>
                </template>
                <div class="chart-stats">
                    <div class="stat-item">
                        <div class="stat-value">{{ blockchainStats.blockNumber }}</div>
                        <div class="stat-label">区块高度</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-value">{{ blockchainStats.transactionCount }}</div>
                        <div class="stat-label">交易总数</div>
                    </div>

                </div>
                <div ref="pieChartRef" style="width: 100%; height: 400px"></div>
            </el-card>

            <el-card style="width: 48%">
                <template #header>
                    <div class="info-header">
                        <span class="header-text">系统数据统计</span>
                    </div>
                </template>
                <div class="chart-stats">
                    <div class="stat-item">
                        <div class="stat-value">{{ systemStats.appointments }}</div>
                        <div class="stat-label">预约数量</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-value">{{ systemStats.medicalRecords }}</div>
                        <div class="stat-label">病历数量</div>
                    </div>
                </div>
                <div ref="barChartRef" style="width: 100%; height: 400px"></div>
            </el-card>
        </div>
    </div>
</template>

<script setup lang="ts">
import axios from 'axios';
import * as echarts from 'echarts';
import { onMounted, ref, reactive } from 'vue';

const pieChartRef = ref<HTMLDivElement | null>(null);
const barChartRef = ref<HTMLDivElement | null>(null);

const blockchainStats = reactive({
    blockNumber: 0,
    transactionCount: 0,
    query: 0,
    queryCount: 0
});

const systemStats = reactive({
    appointments: 0,
    medicalRecords: 0,
    doctors: 0,
    records: 0
});

const initPieChart = async () => {
    try {
        const blockchainData = await axios.get('http://localhost:8085/blockchainInfo');
        const chart = echarts.init(pieChartRef.value);

        // 更新统计数据
        Object.assign(blockchainStats, blockchainData.data);

        const option = {
            tooltip: {
                trigger: 'item',
                formatter: '{a} <br/>{b}: {c} ({d}%)'
            },
            legend: {
                orient: 'vertical',
                left: 'left'
            },
            series: [
                {
                    name: '区块链数据',
                    type: 'pie',
                    radius: '60%',
                    data: [
                        { value: blockchainData.data.blockNumber, name: '区块高度' },
                        { value: blockchainData.data.transactionCount, name: '交易总数' },
                    ],
                    emphasis: {
                        itemStyle: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    }
                }
            ]
        };

        chart.setOption(option);

        window.addEventListener('resize', () => {
            chart.resize();
        });
    } catch (error) {
        console.error('Failed to load blockchain data:', error);
    }
};
const doctor = localStorage.getItem('accountAddress') || '{}';

const initBarChart = async () => {
    console.log(doctor);
    try {
        const systemData = await axios.get('http://localhost:8085/getAll', {
            params: { address: doctor }
        });

        const chart = echarts.init(barChartRef.value);
        console.log(systemData);
        // 更新统计数据
        systemStats.appointments = systemData.data.appointment;
        systemStats.medicalRecords = systemData.data.medicalRecord;


        const option = {
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                }
            },
            xAxis: {
                type: 'category',
                data: ['预约数量', '病历数量']
            },
            yAxis: {
                type: 'value'
            },
            series: [{
                data: [
                    systemData.data.appointment,
                    systemData.data.medicalRecord,
                ],
                type: 'bar',
                showBackground: true,
                backgroundStyle: {
                    color: 'rgba(180, 180, 180, 0.2)'
                }
            }]
        };

        chart.setOption(option);

        window.addEventListener('resize', () => {
            chart.resize();
        });
    } catch (error) {
        console.error('Failed to load system data:', error);
    }
};

onMounted(() => {
    initPieChart();
    initBarChart();
});
</script>

<style scoped>
/* 卡片样式 */
.el-card {
    background: linear-gradient(135deg, #ffffff 0%, #f0f2f5 100%);
    border: none;
    border-radius: 15px;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
    position: relative;
    overflow: hidden;
}

.el-card::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, #4a9e5c, #4a9eff);
}

/* 统计区域样式 */
.chart-stats {
    display: flex;
    justify-content: space-around;
    /* margin-bottom: 20px; */
    background: linear-gradient(135deg, #f8f9ff 0%, #f0f4ff 100%);
    border-radius: 12px;
    padding: 20px;
    position: relative;
    overflow: hidden;
    border: 1px solid rgba(0, 209, 255, 0.1);
}

/* 科技感光效 */
.chart-stats::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: linear-gradient(
        45deg,
        transparent 45%,
        rgba(0, 209, 255, 0.1) 50%,
        transparent 55%
    );
    animation: light-sweep 3s infinite linear;
}

/* 统计项样式 */
.stat-item {
    position: relative;
    text-align: center;
    padding: 15px 25px;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 8px;
    transition: all 0.3s ease;
    border: 1px solid rgba(0, 209, 255, 0.2);
    z-index: 1;
}

.stat-item:hover {
    transform: translateY(-5px);
    box-shadow: 0 5px 15px rgba(0, 209, 255, 0.2);
}

/* 数值样式 */
.stat-value {
    font-size: 28px;
    font-weight: bold;
    background: linear-gradient(45deg, #4a9e5c, #00BCD4);
    -webkit-background-clip: text;
    color: transparent;
    position: relative;
}

/* 标签样式 */
.stat-label {
    font-size: 16px;
    color: #5c6c8c;
    margin-top: 5px;
    font-weight: 500;
}

/* 标题样式 */
.info-header {
    padding: 15px 0;
    position: relative;
}

.header-text {
    font-size: 18px;
    font-weight: 600;
    color: #2c3e50;
    position: relative;
    padding-left: 15px;
}

.header-text::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 4px;
    height: 18px;
    background: linear-gradient(to bottom, #4a9e5c, #4a9eff);
    border-radius: 2px;
}

/* 动画效果 */
@keyframes light-sweep {
    0% {
        transform: translate(-50%, -50%) rotate(0deg);
    }
    100% {
        transform: translate(-50%, -50%) rotate(360deg);
    }
}

/* 图表容器样式 */
.el-card :deep(.echarts) {
    border-radius: 8px;
    padding: 10px;
    background: rgba(255, 255, 255, 0.8);
}
</style>