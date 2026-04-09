<template>
  <div class="pie-chart" ref="chartRef"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'

interface DataItem {
  name: string
  value: number
}

interface Props {
  data: DataItem[]
  title?: string
  height?: string
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  height: '280px'
})

const chartRef = ref<HTMLElement>()
let chartInstance: echarts.ECharts | null = null

const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#00d4aa', '#ff7875']

const initChart = () => {
  if (!chartRef.value) return
  
  // 检查是否已存在实例，避免重复初始化
  const existingInstance = chartInstance || echarts.getInstanceByDom(chartRef.value)
  if (existingInstance) {
    chartInstance = existingInstance
  } else {
    chartInstance = echarts.init(chartRef.value)
  }
  
  // 确保 data 是数组
  const chartData = Array.isArray(props.data) ? props.data : []
  
  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: '#e2e8f0',
      borderWidth: 1,
      textStyle: {
        color: '#4a5568'
      },
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      textStyle: {
        color: '#4a5568'
      }
    },
    series: [
      {
        name: props.title,
        type: 'pie',
        radius: ['45%', '70%'],
        center: ['35%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: chartData.map((item, index) => ({
          ...item,
          itemStyle: {
            color: colors[index % colors.length]
          }
        }))
      }
    ]
  }
  
  chartInstance.setOption(option)
}

const handleResize = () => {
  chartInstance?.resize()
}

watch(() => props.data, () => {
  if (chartInstance) {
    updateChart()
  } else {
    initChart()
  }
}, { deep: true })

const updateChart = () => {
  if (!chartInstance || !chartRef.value) return
  
  // 确保 data 是数组
  const chartData = Array.isArray(props.data) ? props.data : []
  
  const option: echarts.EChartsOption = {
    series: [
      {
        data: chartData.map((item, index) => ({
          ...item,
          itemStyle: {
            color: colors[index % colors.length]
          }
        }))
      }
    ]
  }
  
  chartInstance.setOption(option)
}

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>

<style scoped>
.pie-chart {
  width: 100%;
  height: v-bind(height);
}
</style>

